import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;      

public class Test {
    public static void main(String[] args) throws InterruptedException {
        Warehouse warehouse = new Warehouse();

        int numClients = 10;
        int numItems = 20;     
        int initialStock = 3;  
        List<Thread> clients = new ArrayList<>();

        // Initialize the warehouse with initial stock for each item
        for (int i = 1; i <= numItems; i++) {
            warehouse.supply("item" + i, initialStock);  
        }

        // Display initial stock in the warehouse
        System.out.println("Initial warehouse stock:");
        for (int i = 1; i <= numItems; i++) {
            System.out.println("item" + i + ": " + initialStock + " units");
        }
        System.out.println();

        // Set to track finished clients to avoid starvation
        Set<Integer> finishedClients = Collections.synchronizedSet(new HashSet<>());

        // Thread for supplying items to the warehouse concurrently
        Thread supplyThread = new Thread(() -> {
            Random rand = new Random();
            while (finishedClients.size() < numClients) {
                try {
                    for(int i = 1; i <= numItems; i++)
                    warehouse.supply("item" + i, 1);
                    Thread.sleep(5000);  // Sleep for 1-3 seconds between supplies
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        // Start the supply thread
        supplyThread.start();

        // Clients thread for consuming items concurrently
        for (int i = 0; i < numClients; i++) {
            final int clientId = i;
            Set<String> itemsToConsume = new HashSet<>();

            Random rand = new Random();
            for (int j = 0; j < 5; j++) {  // Each client will try to consume 5 random items
                int itemNumber = rand.nextInt(numItems) + 1;
                itemsToConsume.add("item" + itemNumber);
            }

            Thread clientThread = new Thread(() -> {
                try {
                    System.out.println("Client " + clientId + " started. Wants to consume: " + itemsToConsume);
                    // Add random sleep before consuming
                    Thread.sleep(rand.nextInt(3000));  // Random sleep between 500-1500ms before consuming
                    warehouse.consume(itemsToConsume);  // Client tries to consume the items
                    System.out.println("Client " + clientId + " finished consuming items.");
                    finishedClients.add(clientId);
                } catch (InterruptedException e) {
                    System.out.println("Client " + clientId + " was interrupted.");
                }
            });

            clients.add(clientThread);
        }

        // Start all client threads
        clients.forEach(thread -> thread.start());

        long startTime = System.currentTimeMillis();
        long maxWaitTime = 100000; // Timeout of 10 seconds

        // Monitor for starvation
        while (finishedClients.size() < numClients) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            if (elapsedTime > maxWaitTime) {
                System.out.println("Warning: Not all clients have finished within the timeout. Possible starvation.");
                break;
            }
            Thread.sleep(100);  // Sleep for a short while to avoid tight loop
        }

        if (finishedClients.size() == numClients) {
            System.out.println("Test passed: No client experienced starvation.");
        } else {
            System.out.println("Test failed: Some clients may have been starved.");
        }

        // Ensure all threads have finished
        clients.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        supplyThread.join();  // Wait for the supply thread to finish
        System.out.println("All clients and supply operations have finished.");
    }
}

import java.util.*;
import java.util.concurrent.*;

public class Test {
    public static void main(String[] args) throws InterruptedException {
        Warehouse warehouse = new Warehouse();

        int numClients = 5;  
        int numItems = 10;  
        List<Thread> clients = new ArrayList<>();

        for (int i = 1; i <= numItems; i++) {
            warehouse.supply("item" + i, 10);  
        }

        for (int i = 0; i < numClients; i++) {
            final int clientId = i;
            Set<String> itemsToConsume = new HashSet<>();

            Random rand = new Random();
            for (int j = 0; j < 5; j++) { 
                int itemNumber = rand.nextInt(numItems) + 1;  
                itemsToConsume.add("item" + itemNumber);
            }

            Thread clientThread = new Thread(() -> {
                try {
                    System.out.println("Client " + clientId + " started.");
                    warehouse.consume(itemsToConsume);
                    System.out.println("Client " + clientId + " finished consuming items.");
                } catch (InterruptedException e) {
                    System.out.println("Client " + clientId + " was interrupted.");
                }
            });
            clients.add(clientThread);
            clientThread.start();
        }

        for (Thread clientThread : clients) {
            clientThread.join();
        }

        System.out.println("All clients have finished consuming.");
    }
}

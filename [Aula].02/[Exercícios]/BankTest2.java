import java.util.Random;

public class BankTest2 {

    private static class Mover implements Runnable {
        Banco b;
        int s; // Number of accounts

        public Mover(Banco b, int s) { this.b=b; this.s=s; }

        public void run() {
            final int moves=100000;
            int from, to;
            Random rand = new Random();

            for (int m=0; m<moves; m++)
            {
                from=rand.nextInt(s); // Get one
                while ((to=rand.nextInt(s))==from); // Slow way to get distinct
                b.transfer(from,to,1);
            }
        }
    }

    private static class Observer implements Runnable {
        private Banco b;
        private int expectedBalance;

        public Observer(Banco b, int expectedBalance) {
            this.b = b;
            this.expectedBalance = expectedBalance;
        }

        @Override
        public void run() {
            final int balanceOperations = 100000;

            for (int i = 0; i < balanceOperations; i++) {
                int currentBalance = b.totalBalance();
                if (currentBalance != this.expectedBalance) {
                    throw new RuntimeException("Unexpected balance (" + currentBalance + ") at ["+ i +"]");
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        final int N = 5000;

        Banco b = new Banco(N);

        for (int i = 0; i < N; i++)
            b.deposit(i, 1000);

        int initialBalance = b.totalBalance();
        System.out.println(initialBalance);

        Thread t1 = new Thread(new Mover(b, 10));
        Thread t2 = new Thread(new Mover(b, 10));
        Thread t3 = new Thread(new Observer(b, initialBalance));

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        System.out.println(b.totalBalance());
    }
}


/*
 *  Com estes parâmetros, a estratégia da exclusão mútua global (usar um lock global no banco)
 *  precisa de menos de 1 segundo para executar o programa, enquanto que a da exclusão mútua 
 *  ao nível das contas individuais (1 lock por conta) precisa de cerca de 8 segundos. 
 */

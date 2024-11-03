import java.util.Random;

public class BankTest2 {

    private static class Mover implements Runnable {
        Bank b;
        int s; // Number of accounts

        public Mover(Bank b, int s) { this.b=b; this.s=s; }

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
        private Bank b;
        private int expectedBalance;
        private int amountAccounts;
        private int totalDeContasRemovidas = 0;
        private int ContasRemovidas = 0;

        public Observer(Bank b, int expectedBalance, int amountAccounts) {
            this.b = b;
            this.expectedBalance = expectedBalance;
            this.amountAccounts = amountAccounts; 
        }

        @Override
        public void run() {
            final int balanceOperations = 10000;
            Random rand = new Random();
            int totalRemovidoDoBanco = 0, contasRemovidasNumCiclo = 0;
            
            System.out.println(".............................................");
            for (int j = 0; j < 10; j++) {
              contasRemovidasNumCiclo = 0;
              totalRemovidoDoBanco = 0;      
              
              for(int a = 0; a < 200; a++){
                int x = b.closeAccount(rand.nextInt(this.amountAccounts));

                if(x != 0){
                  this.expectedBalance -= x;
                  totalRemovidoDoBanco += x;
                  contasRemovidasNumCiclo++;
                }

                
              }
              totalDeContasRemovidas += totalRemovidoDoBanco;
              ContasRemovidas += contasRemovidasNumCiclo;

              System.out.println("[" + totalRemovidoDoBanco + "]€ Removidos de [" + contasRemovidasNumCiclo + "] contas apagadas" );

              for (int i = 0; i < balanceOperations; i++) {

                  int currentBalance = b.totalBalance(b.getAccountIDs());
                  if (currentBalance != this.expectedBalance) {
                      throw new RuntimeException("Unexpected balance (" + currentBalance + ") at ["+ i +"]");
                  }
              }
            }
          System.out.println(".............................................");
          System.out.println("[" + this.totalDeContasRemovidas + "]€ Perdidos de [" + amountAccounts + "]");
          System.out.println(".............................................");
          System.out.println("[" + b.totalBalance(b.getAccountIDs()) + "]€ Presentes em [" + ContasRemovidas + "] contas");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        final int N = 5000;

        Bank b = new Bank();

        for (int i = 0; i < N; i++){
          b.createAccount(2000);
          b.deposit(i, 1000);
        }

        int initialBalance = b.totalBalance(b.getAccountIDs());
        System.out.println("[" + initialBalance + "]€ (Montante Total Depositado)");

        Thread t1 = new Thread(new Mover(b, N));
        Thread t2 = new Thread(new Mover(b, N));
        Thread t3 = new Thread(new Mover(b, N));
        Thread t4 = new Thread(new Mover(b, N));
        Thread t5 = new Thread(new Mover(b, N));
        Thread t6 = new Thread(new Mover(b, N));
        Thread t7 = new Thread(new Mover(b, N));
        Thread t8 = new Thread(new Mover(b, N));
        Thread t9 = new Thread(new Mover(b, N));
        Thread t10 = new Thread(new Observer(b, initialBalance, N));

        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
        t6.start();
        t7.start();
        t8.start();
        t9.start();
        t10.start();

        t1.join();
        t2.join();
        t3.join();
        t4.join();
        t5.join();
        t6.join();
        t7.join();
        t8.join();
        t9.join();
        t10.join();
    }
}


/*
 *  Com estes parâmetros, a estratégia da exclusão mútua global (usar um lock global no banco)
 *  precisa de menos de 1 segundo para executar o programa, enquanto que a da exclusão mútua 
 *  ao nível das contas individuais (1 lock por conta) precisa de cerca de 8 segundos. 
 */


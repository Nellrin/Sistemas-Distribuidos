import java.util.concurrent.locks.ReentrantLock;

class Bank {

      private static class Account {
        private int balance;
        private ReentrantLock lock = new ReentrantLock();

        Account(int balance) {
          this.balance = balance;
        }

        int balance() {
          
          int x = 0;
          lock.lock();
   
          try{
            x = balance;
          } finally {
            lock.unlock();
          }
  
          return x;
        }

        boolean deposit(int value) {
          lock.lock();
          try{
            balance += value;
          } finally {
            lock.unlock();
          }

          return true;
        }
      }






      // Our single account, for now
      private Account savings = new Account(0);

      // Account balance
      public int balance() {
        return savings.balance();
      }

      // Deposit
      boolean deposit(int value) {
        return savings.deposit(value);
      }
}



















public class ex3 implements Runnable{
  
  final Bank b;

  public ex3(Bank b){
    this.b = b;
  }

  public void run(){
    final long I = 1000;
    int V = 100;

    for(int i = 0; i < I; i++)
    this.b.deposit(V);
  }
}


class Main{
   public static void main(String[] args){
    
    Bank b = new Bank();

    int N = Integer.valueOf(args[0]);
    Thread[] lt = new Thread[N];

    for(int i = 0; i < N; i++)
    lt[i] = new Thread(new ex3(b));


    for(int i = 0; i < N; i++)
    lt[i].start();
    

    for(int i = 0; i < N; i++){
      try{lt[i].join();}
      catch(InterruptedException e){e.printStackTrace();}
    }
  
      System.out.println("A conta tem " + b.balance() + "€");

  }
}

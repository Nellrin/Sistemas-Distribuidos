import java.util.concurrent.locks.ReentrantLock;

public class Banco{

    private static class Account {
        private int balance;
        public ReentrantLock lock = new ReentrantLock();

        Account (int balance) { this.balance = balance; }
    
        int balance () { return balance; }
        
        boolean deposit (int value) {
            balance += value;
            return true;
        }
        
        boolean withdraw (int value) {
            if (value > balance)
                return false;
            balance -= value;
            return true;
        }
    }

    // Bank slots and vector of accounts
    private final int slots;
    private Account[] av;
    private ReentrantLock BancoLock = new ReentrantLock();

    public Banco (int n) {
        slots=n;
        av=new Account[slots];
        for (int i=0; i<slots; i++)
            av[i]=new Account(0);
    }


    // Account balance
    public int balance (int id) {
        if (id < 0 || id >= slots)
            return 0;
        return av[id].balance();
    }

    // Deposit
    public boolean deposit (int id, int value) {
        if (id < 0 || id >= slots)
            return false;
        return av[id].deposit(value);
    }

    // Withdraw; fails if no such account or insufficient balance
    public boolean withdraw (int id, int value) {
        if (id < 0 || id >= slots)
            return false;
        return av[id].withdraw(value);
    }






                      // Transfer
                      public boolean transfer(int from, int to, int value){
                        int minID = Math.min(from,to);
                        int maxID = Math.max(from,to);


                        this.BancoLock.lock();
                        //this.av[minID].lock.lock();
                        //this.av[maxID].lock.lock();
                        
                        try{

                          if(!this.withdraw(from,value))
                          return false;

                          return this.deposit(to,value);

                        } finally {
                          
                        
                          this.BancoLock.unlock();
                          //this.av[maxID].lock.unlock();
                          //this.av[minID].lock.unlock();
                        }
                      }







                      // TotalBalance
                      public int totalBalance(){
                        int sum = 0;
                        
                        this.BancoLock.lock();
                        
                        //for(int i = 0; i < this.slots; i++)
                        //this.av[i].lock.lock();

                        try{
                          for(int i = 0; i < this.slots; i++)
                          sum += this.balance(i);
                        
                        } finally {
                          //for(int i = this.slots - 1; i >= 0; i--)
                          //this.av[i].lock.unlock();
                          
                          this.BancoLock.unlock();
                        }
                          
                        return sum;
                      }
}



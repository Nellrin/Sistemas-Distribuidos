import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

class Bank {

    private static class Account {
        private int balance;
        public ReentrantLock AccLock = new ReentrantLock(); 

        Account(int balance) { this.balance = balance; }

        int balance() { return balance; }
        
        boolean deposit(int value) {
            balance += value;
            return true;
        }
        
        boolean withdraw(int value) {
            if (value > balance)
                return false;
            balance -= value;
            return true;
        }
    }

    private Map<Integer, Account> map = new HashMap<Integer, Account>();
    private int nextId = 0;
    ReentrantLock BankLock = new ReentrantLock();

    // create account and return account id
    public int createAccount(int balance){
      
        this.BankLock.lock();

        try{
          Account c = new Account(balance);
          int id = nextId;
          nextId += 1;
          map.put(id, c);
          return id;
        
        } 
        
        finally{this.BankLock.unlock();}

    }

    // close account and return balance, or 0 if no such account
    public int closeAccount(int id){
        this.BankLock.lock();
        Account c = null;
        try{
          c = map.remove(id);
          if (c == null)
              return 0;
          
          c.AccLock.lock();
          return c.balance();

        }

        finally{
          if(c != null)
          c.AccLock.unlock();
          this.BankLock.unlock();
        }
    }

    // account balance; 0 if no such account
    public int balance(int id){

      this.BankLock.lock();
      Account c = null;
      try{
        c = map.get(id); 
      
        if (c == null)
        return 0;
        
        c.AccLock.lock();
        return c.balance();
      } finally {
        if(c != null)
        c.AccLock.unlock();
        this.BankLock.unlock();
      }
    }

    // deposit; fails if no such account
    public boolean deposit(int id, int value){

      this.BankLock.lock();
      Account c = map.get(id);
      
        if (c == null){
          this.BankLock.unlock();
          return false;
        }
  
        c.AccLock.lock();        
        this.BankLock.unlock();

        try{return c.deposit(value);}
        finally{
          c.AccLock.unlock();
        }
      
    }

    // withdraw; fails if no such account or insufficient balance
    public boolean withdraw(int id, int value){
        
      this.BankLock.lock();
      Account c = map.get(id); 
      
        if (c == null){
          this.BankLock.unlock();
          return false;
        }    
        
        c.AccLock.lock();
        this.BankLock.unlock();
      
        try{return c.withdraw(value);}
        finally{
          c.AccLock.unlock();
        }
    }

    // transfer value between accounts;
    // fails if either account does not exist or insufficient balance
    public boolean transfer(int from, int to, int value) {
        this.BankLock.lock();
        Account cfrom, cto;

          cfrom = map.get(Math.max(from,to)); 
          cto = map.get(Math.min(from,to));
            
          if (cfrom == null || cto == null){
            this.BankLock.unlock();
            return false;
          }
        
          cfrom.AccLock.lock();
          cto.AccLock.lock();
          this.BankLock.unlock();
                  
          try{
            if(!cfrom.withdraw(value))
            return false;

            cfrom.AccLock.unlock();
            return cto.deposit(value);
          
          } finally {
            cto.AccLock.unlock();
          }
    }


    // sum of balances in set of accounts; 0 if some does not exist
    public int totalBalance(int[] ids){
        this.BankLock.lock();
        Account[] lcontas = new Account[ids.length];   
        int total = 0;

          for(int i = 0; i < ids.length; i++){
            lcontas[i] = this.map.get(ids[i]);
            
            if(lcontas[i] == null){
              this.BankLock.unlock();
              return 0;
            }
      
          }

          for(int i = 0; i < ids.length; i++)
          lcontas[i].AccLock.lock();
                              
          this.BankLock.unlock();
          

          for(Account c : lcontas){
            total += c.balance();
            c.AccLock.unlock();
          }


      return total;
    }

  public int[] getAccountIDs(){
    return this.map.keySet()
                   .stream()
                   .mapToInt(Integer::intValue)
                   .toArray();
  }
}

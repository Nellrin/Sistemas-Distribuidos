import java.util.*;
import java.util.concurrent.locks.*;

class Bank {

    private static class Account {
        private int balance;
        private ReentrantLock aLock = new ReentrantLock();

/*        public ReentrantReadWriteLock arwl = new ReentrantReadWriteLock(); 
 *
 *        private Lock arl = arwl.readLock();
 *        private Lock awl = arwl.writeLock();
 */

/*
 *  Não vale a pena alterar os locks das contas porque como 
 *  há mais escritas do que leituras (há mais levantamentos,
 *  depósitos a serem feitos do que leituras de saldo), o
 *  programa ia ter um bottleneck horroroso
 *  (ALGO MUITO MAU!!!)
 */

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
    
    /*
     * Nota: Antes eu fazia diretamente "rwl.readLock().lock()" e o programa,
     * em vez de demorar +/- 4 segundos, passava a demorar 8 segundos.
     * Isto acontece porque, da maneira que estava a fazer, estava a criar sempre
     * novas instancias de readLocks() em vez de usar sempre uma mesma lock().
     * (SUPER MAU PARA DESEMPENHO!!!)
     * */
    private ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

    private Lock readL = rwl.readLock();
    private Lock writeL = rwl.writeLock();

    // create account and return account id
    public int createAccount(int balance){
      
        this.writeL.lock();

        try{
          Account c = new Account(balance);
          int id = nextId;
          nextId += 1;
          map.put(id, c);
          return id;
        
        } 
        
        finally{this.writeL.unlock();}

    }

    // close account and return balance, or 0 if no such account
    public int closeAccount(int id){
        this.writeL.lock();
        Account c = null;
        try{
          c = map.remove(id);
          if (c == null)
              return 0;
          
          c.aLock.lock();
          return c.balance();

        }

        finally{
          if(c != null)
          c.aLock.unlock();
          this.writeL.unlock();
        }
    }

    // account balance; 0 if no such account
    public int balance(int id){

      this.readL.lock();
      Account c = null;
      try{
        c = map.get(id); 
      
        if (c == null)
        return 0;
        
        c.aLock.lock();
        return c.balance();
      
      } finally {
        if(c != null)
        c.aLock.unlock();
        
        this.readL.unlock();
      }
    }

    // deposit; fails if no such account
    public boolean deposit(int id, int value){

      this.readL.lock();
      Account c = map.get(id);
      
        if (c == null){
          this.readL.unlock();
          return false;
        }
  
        c.aLock.lock();
        this.readL.unlock();
        

        try{return c.deposit(value);}
        finally{c.aLock.unlock();}
      
    }

    // withdraw; fails if no such account or insufficient balance
    public boolean withdraw(int id, int value){
        
      this.readL.lock();
      Account c = map.get(id); 
      
        if (c == null){
          this.readL.unlock();
          return false;
        }    
        
        c.aLock.lock();
        this.readL.unlock();

      
        try{return c.withdraw(value);}
        finally{c.aLock.unlock();}
    }

    // transfer value between accounts;
    // fails if either account does not exist or insufficient balance
    public boolean transfer(int from, int to, int value) {
        this.readL.lock();
        Account cfrom, cto;

          cfrom = map.get(Math.max(from,to)); 
          cto = map.get(Math.min(from,to));
            
          if (cfrom == null || cto == null){
            this.readL.unlock();
            return false;
          }
        
          cfrom.aLock.lock();
          cto.aLock.lock();
        
          this.readL.unlock();
          
          try{
            if(!cfrom.withdraw(value))
            return false;

            return cto.deposit(value);
          
          } finally {
            cto.aLock.unlock();
            cfrom.aLock.unlock();
          }
    }


    // sum of balances in set of accounts; 0 if some does not exist
    public int totalBalance(int[] ids){
        this.readL.lock();
        Account[] lcontas = new Account[ids.length];   
        int total = 0;

          for(int i = 0; i < ids.length; i++){
            lcontas[i] = this.map.get(ids[i]);
            
            if(lcontas[i] == null){
              this.readL.unlock();
              return 0;
            }
      
          }

          for(int i = 0; i < ids.length; i++)
          lcontas[i].aLock.lock();
          
          this.readL.unlock();
                    
          for(Account c : lcontas){
            total += c.balance();
            c.aLock.unlock();
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

import java.util.*;
import java.util.concurrent.locks.*;

class Warehouse {
    private Map<String, Product> map =  new HashMap<String, Product>();
    private Map<String, Condition> mapThread =  new HashMap<String, Condition>();
    private ReentrantLock lock = new ReentrantLock();

    private class Product{ 
      int quantity = 0; 
      private Set<String> threads = new HashSet<>();
    }

    private Product get(String item) {
        Product p = map.get(item);
        if (p != null) return p;
        p = new Product();
        map.put(item, p);
        return p;
    }

    public void supply(String item, int quantity) throws InterruptedException{
        this.lock.lock();

        try{
          Product p = get(item);

          p.quantity += quantity;

          if(p.quantity == 0 && quantity > 0 && !p.threads.isEmpty()){
            p.threads.forEach(id -> {
              Condition c = mapThread.get(id);
              c.signal();
            }); 
          }

        } finally {
          this.lock.unlock();
        }
    }

    public void consume(Set<String> items) throws InterruptedException{
        this.lock.lock();
        
        String thread = Thread.currentThread().getName();

        try{
          boolean flag;
          do{
            flag = false;

            for(String s : items){
              Product p = this.get(s);
              if(p.quantity == 0){
                p.threads.add(thread);
                flag = true;
              }
            }

            if(flag){
              Condition c = this.mapThread.get(thread);
              
              if(c == null){
                c = this.lock.newCondition();
                this.mapThread.put(thread,c);
              }

              c.await();

              break;
            }
            
          }while(flag);


          for(String s : items){
            Product p = this.get(s);
            
            if(p.threads.contains(thread))
            p.threads.remove(thread);

            p.quantity--;
          }

          this.mapThread.remove(thread);

        } finally {
          this.lock.unlock();

        }
    }

}

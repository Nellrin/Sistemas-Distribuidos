import java.util.*;
import java.util.concurrent.locks.*;

class Warehouse {
    private Map<String, Product> map =  new HashMap<String, Product>();
    private ReentrantLock lock = new ReentrantLock();

    private class Product{ 
      int quantity = 0; 
      private Condition cond = lock.newCondition();
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

          if(p.quantity == 0 && quantity > 0)
          p.cond.signal();

          p.quantity += quantity;

        } finally {
          this.lock.unlock();
        }
    }

    public void consume(Set<String> items) throws InterruptedException{
        this.lock.lock();

        try{
          boolean flag;
          do{
            flag = false;

            for(String s: items){
              Product p = this.get(s);
              
              if(p.quantity == 0){
                flag = true;
                p.cond.await();

                break;
              }
            }
            

          }while(flag);


          for(String s : items){
            Product p = this.get(s);
            p.quantity--;
          }

        } finally {
          this.lock.unlock();

        }
    }

}

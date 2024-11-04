import java.util.concurrent.locks.*;
import java.util.Arrays;

class Agreement{

  private int ThreadsRestantes;
  private int Referencia;
  private int[] Picks = null;
  private ReentrantLock lock = new ReentrantLock();
  private Condition c = lock.newCondition();

  Agreement(int N){
    this.ThreadsRestantes = N;
    this.Referencia = N;
    this.Picks = new int[N];
  }

  int propose(int choice) throws InterruptedException{
    this.lock.lock();

    try{
      int x = --this.ThreadsRestantes;
      this.Picks[x] = choice;

      if(x == 0){
        c.signalAll();
        this.ThreadsRestantes = this.Referencia;
      }

      else if(this.ThreadsRestantes > 0)
      this.c.await();
    
    } 
    catch(InterruptedException e){throw new RuntimeException(e);}
    finally{
      this.lock.unlock();
      return Arrays.stream(this.Picks).max().orElseThrow();
    }
  }
}

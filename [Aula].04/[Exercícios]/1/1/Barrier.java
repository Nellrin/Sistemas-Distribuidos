import java.util.concurrent.locks.*;

class Barrier {

  private int ThreadsRestantes;
  private ReentrantLock lock = new ReentrantLock();
  private Condition c = lock.newCondition();

  Barrier(int N){
    this.ThreadsRestantes = N;
  }

  void await() throws InterruptedException{
    this.lock.lock();

    try{
      if(--this.ThreadsRestantes == 0)
      c.signalAll();

      else if(this.ThreadsRestantes > 0)
      this.c.await();

      /*
       * Só há 3 estados possíveis:
       *  1. (ThreadsRestantes > 0), onde o programa põe a Thread atual em espera
       *  2. (ThreadsRestantes == 0), onde todas as Threads que se esperavam estão
       *                              presentes, e assim o programa pode acordá-las
       *  3. (ThreadsRestantes < 0), há mais threads do que estávamos à espera...
       *                             este caso é melhor ser ignorado completamente,
       *                             porque se o tratarmos como o 2º, estamos a dar
       *                             mais um signalAll() do que era suposto, e podemos
       *                             afetar futuras partes do código que podem envolver
       *                             outros .await()'s
       */
    
    } 
    catch(InterruptedException e){throw new RuntimeException(e);}
    finally {this.lock.unlock();}
  }
}

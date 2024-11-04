public class Test{
  public static void main(String[] args){
    final int N = 10;
    Barrier b = new Barrier(N);

    Runnable task = () -> {
      try{
        System.out.println("[" + Thread.currentThread().getName() + "] Ready");
        b.await();
        System.out.println("[" + Thread.currentThread().getName() + "] OUT!");

      } catch (InterruptedException e){
        Thread.currentThread().interrupt();
      }
    };

    for(int i = 0; i < N; i++)
    new Thread(task).start();
  }
}

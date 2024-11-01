class ex1 implements Runnable{
  public void run(){
    int I = 100;

    for(int i = 1; i <= I; i++)
      System.out.print("[" + i + "] ");
  }
}

class Main{
  public static void main(String[] args){

    int N = 10;
    Thread[] lt = new Thread[N];

    for(int i = 0; i < N; i++)
    lt[i] = new Thread(new ex1());     



    for(int i = 0; i < N; i++){
      lt[i].start();
    }

    for(int i = 0; i < N; i++){
      try{
        lt[i].join();
      } catch (InterruptedException e){
        e.printStackTrace();
      }

    }


    System.out.println("FIM");
  }  
}

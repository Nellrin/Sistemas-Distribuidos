import java.util.Random;

public class Test{
    public static void main(String[] args) {
      final int N = 5;
      Random random = new Random();
      Agreement agreement = new Agreement(N); 
      
      Thread[] lt = new Thread[N];

      for(int i = 0; i < 4; i++){
        for(int j = 0; j < 5; j++){
          lt[j] = new Thread(() -> {
            int choice = random.nextInt(100);
            System.out.println("[" + choice + "]");

            proposeValue(choice, agreement);
          });
        }
        
        for(int j = 0; j < 5; j++)
        lt[j].start();

        try{
          for(int j = 0; j < 5; j++)
          lt[j].join();
        } catch (InterruptedException e){
          Thread.currentThread().interrupt();
        }
      }
    }
        
    private static void proposeValue(int value, Agreement agreement) {
      try {
          int agreedValue = agreement.propose(value);
          if (agreedValue != -1) {System.out.println("[" + Thread.currentThread().getName() + "] Agreed value: " + agreedValue);}
      } 
      catch (InterruptedException e) {Thread.currentThread().interrupt();}
    }
}


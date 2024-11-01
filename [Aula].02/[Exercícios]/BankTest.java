import java.util.Random;

class Mover implements Runnable {
    Banco b;
    int s; // Number of accounts

    public Mover (Banco b, int s) { this.b=b; this.s=s; }

    public void run() {
        System.out.println ("Started thread-" + Thread.currentThread().getId());
        final int moves = 10000000;
        int from, to;
        Random rand = new Random();

        for (int m=0; m < moves; m++) {
            from=rand.nextInt(s); // Get one
            while ((to=rand.nextInt(s))==from); // Slow way to get distinct
            b.transfer(from,to,1);
        }
    }
}

public class BankTest {
    public static void main(String[] args) throws InterruptedException {
        final int N = 123456;

        Banco b = new Banco(N);

        for (int i = 0; i < N; i++) {
            b.deposit(i, 10000);
        }

        System.out.println (b.totalBalance());

        Thread t1 = new Thread(new Mover(b,123456));
        Thread t2 = new Thread(new Mover(b,123456));


        t1.start(); t2.start();
        t1.join(); t2.join();

        System.out.println(b.totalBalance());
    }
}



/*
 *  Com estes parâmetros, a estratégia da exclusão mútua global (usar um lock global no banco)
 *  precisa de cerca de 4 segundos para executar o programa, enquanto que a da exclusão mútua 
 *  ao nível das contas individuais (1 lock por conta) precisa de cerca de 2 segundos. 
 */

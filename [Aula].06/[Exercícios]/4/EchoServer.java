import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class EchoServer {
    public static void main(String[] args) {
        LinkedList<Thread> llt = new LinkedList<Thread>();
        try{
            ServerSocket ss = new ServerSocket(12345);
            StatTrack st = new StatTrack();
            while (true) {
                Thread cliente = new Thread(new ThreadPerClient(ss.accept(), st));
                System.out.println("[" + ss.getInetAddress() + "]");
                llt.add(cliente);
        
                cliente.start();
            }
        } catch(IOException e){
          e.printStackTrace();
        } 
    /*    
        finally {
      
          try{
            for (Thread t : llt){
              t.join();
            }
          } catch (InterruptedException e){
            e.printStackTrace();
          }
            
            llt.clear();
        }
    */
    }
}

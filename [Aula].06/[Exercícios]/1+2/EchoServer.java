import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class EchoServer {
    Map<String,Thread> map = new HashMap<String,Thread>()
    ReentrantLock lock = new ReentrantLock();


    public static void main(String[] args) {

        try {
            ServerSocket ss = new ServerSocket(12345);

            boolean flag = true;

            while (flag){
                Socket socket = ss.accept();

                Thread atual = new Thread();
                String threadName = Thread.currentThread().getName();

                this.map.put(threadName,atual);
                

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream());

                double sum = 0;
                int amount = 0;
                String line = null;

                while (!flag || ((line = in.readLine()) != null)){
                    if(line.equals("Close Server"))
                    flag = false;

                    if(!flag || line.isBlank()){
                      if(amount != 0)
                      sum /= amount;

                      out.println(sum);
                      out.flush();
                      break;  
                    }

                    System.out.println("[" + line + "]");

                    try{
                        sum += Double.parseDouble(line);
                        amount++;
                    }
                    catch(NumberFormatException e){
                      break;
                    }
                    
                    out.println(sum);
                    out.flush();
                }

                socket.shutdownOutput();
                socket.shutdownInput();
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ThreadPerClient implements Runnable{

    private Socket ss;

    ThreadPerClient(Socket ss){
      this.ss = ss;
    }


    public void run(){
        try{
          BufferedReader in = new BufferedReader(new InputStreamReader(this.ss.getInputStream()));
          PrintWriter out = new PrintWriter(this.ss.getOutputStream());

          double sum = 0;
          int amount = 0;
          String line = null;

          while((line = in.readLine()) != null){

            System.out.println("[" + ss.getInetAddress() + "]: " + line);

            try{
              sum += Double.parseDouble(line);
              amount++;

            } catch(NumberFormatException e){
              if(amount != 0)
              sum /= amount;

              out.println(sum);
              out.flush();
              break;
            }
            
            out.println(sum);
            out.flush();
          }

          this.ss.shutdownOutput();
          this.ss.shutdownInput();
          this.ss.close();
      
      } catch (IOException e){
        e.printStackTrace();
      }
  }
}

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ThreadPerClient implements Runnable{

    private Socket ss;
    private StatTrack st;
  
    ThreadPerClient(Socket ss, StatTrack st){
      this.ss = ss;
      this.st = st;
    }


    public void run(){
        try{
          BufferedReader in = new BufferedReader(new InputStreamReader(this.ss.getInputStream()));
          PrintWriter out = new PrintWriter(this.ss.getOutputStream());

          String line = null;

          double x,total = 0;
          int amount = 0;

          while((line = in.readLine()) != null){

            System.out.println("[" + ss.getInetAddress() + "]: " + line);

            try{
              x = Double.parseDouble(line);
              st.add(x);
              total += x;
              amount++;
            } 
            catch(NumberFormatException e){
              break;
            }
            
            out.println(total);
            out.flush();
          }

          out.println(st.done());
          out.flush();

          this.ss.shutdownOutput();
          this.ss.shutdownInput();
          this.ss.close();
      
      } catch (IOException e){
        e.printStackTrace();
      }
  }
}

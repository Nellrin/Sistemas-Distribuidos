import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ThreadPerClient implements Runnable{

    private Socket ss;
    private BufferedReader in;
    PrintWriter out;

    private int amount = 0;
    private double total = 0;

    ThreadPerClient(Socket ss){
      this.ss = ss;
          
      try{
        this.in = new BufferedReader(new InputStreamReader(this.ss.getInputStream()));
        this.out = new PrintWriter(this.ss.getOutputStream());
      } catch(IOException e){e.printStackTrace();}
    }

    public void add(double x){
      this.total += x;
      this.amount++;
      
      out.println(this.total);
      out.flush();
    }

    public void sub(double x){
      this.total -= x;
      this.amount++;
      
      out.println(this.total);
      out.flush();
    }

    public void mul(double x){
      this.total *= x;
      
      out.println(this.total);
      out.flush();
    }
    
    public void div(double x){
      this.total /= x;

      out.println(this.total);
      out.flush();

    }
    
    public void avg(){
      if(this.amount!=0)
      out.println(this.total / this.amount);
      
      else
      out.println("ERR");
      out.flush();
    }

    public void cur(){
      out.println(this.total);
      out.flush();
    }

    public void run(){
        try{
          double x;

          String line = null;
      
          while((line = in.readLine()) != null){
            try{
                System.out.println("[" + ss.getInetAddress() + "]: " + line);
            
                
                switch(line.substring(0,5)){
                  case "[ADD]":
                    x = Double.parseDouble(line.substring(5)); 
                    this.add(x); 
                  break;
                  case "[SUB]":
                    x = Double.parseDouble(line.substring(5)); 
                    this.sub(x);
                  break;
                  case "[MUL]":
                    x = Double.parseDouble(line.substring(5)); 
                    this.mul(x);
                  break;
                  case "[DIV]":
                    x = Double.parseDouble(line.substring(5)); 
                    this.div(x);
                  break;
                  case "[AVG]":
                    this.avg();
                  break;
                  case "[CUR]":
                    this.cur();
                  break;

                  default:
                    out.println("ERR");
                    out.flush();
                  break;
                }
            } catch(NumberFormatException e){
              if(line.equals("DONE")){
                this.out.println("OK!");
                this.out.flush();
                break;
              }

              else{
                this.out.println("ERR");
                this.out.flush();
              }

            } catch(StringIndexOutOfBoundsException i){
              this.out.println("ERR");
              this.out.flush();
            }
          }

          this.ss.shutdownOutput();
          this.ss.shutdownInput();
          this.ss.close();
      
      } catch (IOException e){
        e.printStackTrace();
      }
  }
}

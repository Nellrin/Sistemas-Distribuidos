import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

public class StatTrack {
    double total = 0;
    int amount = 0;
    ReentrantLock lock = new ReentrantLock();

    public void add(double x){
        this.lock.lock();
      
        try{
          this.total += x;
          amount++;
        } finally{this.lock.unlock();}
    }

    public double total(){
        this.lock.lock();
      
        try{return this.total;} 
        finally{this.lock.unlock();}
    }

    public double done(){
        this.lock.lock();
      
        try{
          if(this.amount == 0)
          return 0;

          return this.total/this.amount;
        } finally{this.lock.unlock();}
    }
}

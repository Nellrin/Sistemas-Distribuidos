import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class EchoClient{
    public static void main(String[] args) {
    try {
        Socket socket = new Socket("localhost", 12345);

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream());

        BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));

        String serverResponse = null;
        String userInput = null;

        while(true){
            userInput = systemIn.readLine();
            out.println(userInput);
            out.flush();
            
            serverResponse = in.readLine();     
            
            if(userInput == null)
            break;


            else
            System.out.println("Somatório atual: " + serverResponse);
        }
        
        System.out.println("A média é de: " + serverResponse);

        socket.shutdownOutput();
        socket.shutdownInput();
        socket.close();

    } catch (Exception e) {
        e.printStackTrace();
    }
  }
}

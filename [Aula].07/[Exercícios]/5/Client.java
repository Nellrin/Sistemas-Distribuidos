import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class Client {

    public static Contact parseLine(String userInput) {
        String[] tokens = userInput.split(" ");

        System.out.println(tokens.length);

        if(tokens.length < 4 || tokens[0] == null) return null;

        if (tokens[3].equals("null")) tokens[3] = null;

        return new Contact(
                tokens[0],
                Integer.parseInt(tokens[1]),
                Long.parseLong(tokens[2]),
                tokens[3],
                new ArrayList<>(Arrays.asList(tokens).subList(4, tokens.length)));
    }


    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 12345);

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        
        DataOutputStream toServer = new DataOutputStream(socket.getOutputStream());
        DataInputStream fromServer = new DataInputStream(socket.getInputStream());

        String userInput, serverResponse = null;

              ContactList x =  ContactList.deserialize(fromServer);
        
              for(Contact c : x)
              System.out.println(c.toString());

        try{

          while ((userInput = in.readLine()) != null){
              try{
                Contact newContact = parseLine(userInput);

                if(newContact == null) continue;


                System.out.println("["+newContact.toString()+"]");
          

                toServer.writeBoolean(false);
                toServer.flush();
                newContact.serialize(toServer);
              

                serverResponse = fromServer.readUTF();
                System.out.println("\n"+ serverResponse);

              }

              catch (NumberFormatException e){continue;} 
              catch (EOFException e){
                System.out.println("O catch está a impedir o programa de fechar o socket numnuts");
                break;

              }
              catch (IOException e){break;}
          }

          toServer.writeBoolean(true);
          toServer.flush();

          System.out.println("EXITED");
          
        } finally{socket.close();} 
    }
}

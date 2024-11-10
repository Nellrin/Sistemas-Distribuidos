import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.*;

import static java.util.Arrays.asList;

class ContactManager {
    private HashMap<String, Contact> contacts = new HashMap<>();

    public void update(Contact c){this.contacts.put(c.name(), c);}

    public ContactList getContacts(){
      ContactList x = new ContactList();
      x.addAll(this.contacts.values());
      return x;
    }
}

class ServerWorker implements Runnable {
    private Socket _socket;
    private ContactManager _manager;
    private ReentrantReadWriteLock _main_lock = new ReentrantReadWriteLock();
    private ReadLock _read_lock = _main_lock.readLock();
    private WriteLock _write_lock = _main_lock.writeLock();

    public ServerWorker(Socket socket, ContactManager manager) {
        this._socket = socket;
        this._manager = manager;
    }

    @Override
    public void run(){
      try{
        DataInputStream in = new DataInputStream(this._socket.getInputStream());
        DataOutputStream out = new DataOutputStream(this._socket.getOutputStream());
        System.out.println("[" + _socket.getInetAddress() + "] Conexão iniciada");

          this._read_lock.lock();
          try{
            ContactList cl = this._manager.getContacts();
            cl.serialize(out);
            out.flush();
          }
          finally{this._read_lock.unlock();}
        
        while(true){
          if(in.readBoolean() == true){
            System.out.println("["+ _socket.getInetAddress() +"] Conexão finalizada");
            break;
          }

          try{
            Contact c = Contact.deserialize(in);
            
            if(c != null){

              this._write_lock.lock();  
              try{this._manager.update(c);}
              finally{this._write_lock.unlock();}

              out.writeUTF(c.name() + " aceite\n");
              out.flush();
            }  
            
            else{
              out.writeUTF("Contacto Inválido\n");
              out.flush();
            }
          
          } catch (EOFException e){
            System.out.println("["+ _socket.getInetAddress() +"] Conexão finalizada");
            break;

          } catch (IOException e){
            e.printStackTrace();
            break;
          }
        }
      }
      catch(IOException e){e.printStackTrace();}

      finally {
        try{this._socket.close();}
        catch(IOException e){e.printStackTrace();}
      }
    }
}


public class Server {

    public static void main (String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(12345);
        ContactManager manager = new ContactManager();

        manager.update(new Contact("John", 20, 253123321, null, asList("john@mail.com")));
        manager.update(new Contact("Alice", 30, 253987654, "CompanyInc.", asList("alice.personal@mail.com", "alice.business@mail.com")));
        manager.update(new Contact("Bob", 40, 253123456, "Comp.Ld", asList("bob@mail.com", "bob.work@mail.com")));

        while (true) {
            Socket socket = serverSocket.accept();
            Thread worker = new Thread(new ServerWorker(socket, manager));
            worker.start();
        }
    }

}

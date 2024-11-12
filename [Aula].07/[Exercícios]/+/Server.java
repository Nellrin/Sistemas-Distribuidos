import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

class ContactManager {
    private HashMap<String, Contact> contacts = new HashMap<>();

    public void update(Contact c){
      List<String> lf = c.getFriends();

      if(lf.size() > 0){
          lf.removeIf(f -> !this.contacts.containsKey(f));
          lf.forEach(f -> this.contacts.get(f).addFriend(f));

          c.setFriends(lf);
      }

      this.contacts.put(c.name(), c);
    }

    public ContactList getContacts(){
      ContactList x = new ContactList();
      x.addAll(this.contacts.values());
      return x;
    }
/*
    public void addFriends(String[] lc){
      if(lc.length < 2) 
      return;

      int i = 0;
      
      for(; i < lc.length; i++){
        if(this.contacts.get(lc[i+1]) == null){i++; continue;}
        if(this.contacts.get(lc[i]) == null) continue;
        
        ...
      }

      List<String> l = new ArrayList<String>();
      lc.removeIf(s -> !this.contacts.containsKey(s))
      lc.forEach(s -> this.contacts.get(s).addFriend(c));
    }
*/

    public void addFriends(String c, List<String> lc){
      List<String> l = new ArrayList<String>();
      
      Contact x = this.contacts.get(c);

      lc.removeIf(s -> !this.contacts.containsKey(s));
      lc.forEach(s -> {
        x.addFriend(s);
        this.contacts.get(s).addFriend(c);
      });
    }
    
    public Contact getContact(String c){return this.contacts.get(c);}
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
     
        int choice = -1;
        
        while(choice != 0){
          choice = in.readInt();
          switch(choice){
            case 0:
                  System.out.println("["+ _socket.getInetAddress() +"] Conexão finalizada");
            break;
  
            case 1:
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
            break;

            case 2:
                  List<String> list = Arrays.stream(in.readUTF().split("->"))
                                            .map(e -> e.replaceAll("\\s+",""))
                                            .collect(Collectors.toList());
                  
                  String client = list.get(0);

                  this._write_lock.lock();
                  try{
                    list.removeIf(e -> e.equals(client));
                    this._manager.addFriends(client,list);
                  }
                  finally{this._write_lock.unlock();}
            break;

            case 3:
              String userId = in.readUTF();
              Contact user = this._manager.getContact(userId);

              if(user != null){
                List<String> visited = new ArrayList<String>();
                List<String> friends = user.getFriends(); 
                visited.addAll(friends);

                for(String s : friends){
                  Contact c = this._manager.getContact(s);
                  List<String> x = c.getFriends();

                  for(String y : x){
                    if(!visited.contains(y))
                    visited.add(y);
                  }
                }

                out.writeInt(visited.size());
                out.flush();

                for(int i = 0; i < visited.size(); i++){
                  out.writeUTF(visited.get(i));
                  out.flush();
                }
              }
              
            break;

            default:
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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

class Contact {
    private String _name;
    private int _age;
    private long _phoneNumber;
    private String _company;     // Pode ser null
    private ArrayList<String> _emails;

    public Contact(String name, int age, long phoneNumber, String company, List<String> emails) {
        this._name = name;
        this._age = age;
        this._phoneNumber = phoneNumber;
        this._company = company;
        this._emails = new ArrayList<>(emails);
    }

    public String name(){return this._name;}
    public int age(){return this._age;}
    public long phoneNumber(){return this._phoneNumber;}
    public String company(){return this._company;}
    public List<String> emails(){return new ArrayList(this._emails);}

    public void serialize(DataOutputStream out) throws IOException{
      int amountArgs = 3;
      boolean flag = this._company != null;

      if(flag)
      amountArgs++;

      out.writeInt(amountArgs);
      out.flush();

      out.writeUTF(this._name);
      out.flush();

      out.writeInt(this._age);
      out.flush();

      out.writeLong(this._phoneNumber);
      out.flush();
      
      if(flag){
        out.writeUTF(this._company);
        out.flush();
      }
      
      out.writeInt(this._emails.size());
      out.flush();

      for(String e : this._emails){
        out.writeUTF(e);
        out.flush();
      }

    }
    
/*    private static String[] _parse_array(String big_string, String s){
      return big_string.substring(0, big_string.length() - 1)
                               .split(s);
    }
*/

    public static Contact deserialize(DataInputStream in) throws IOException{

      try{
        int amount = in.readInt();
        String name = in.readUTF();
        int age = in.readInt();
        long phone = in.readLong();
        String company = null;
        
        if(amount == 4)
        company = in.readUTF();

        amount = in.readInt();
        String[] emails = new String[amount];

        for(int i = 0; i < amount; i++)
        emails[i] = in.readUTF();
        
        return new Contact(name,age,phone,company,Arrays.asList(emails));

      } 
      catch (IOException e){e.printStackTrace();}

      return null;
    }
    
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this._name).append(";");
        builder.append(this._age).append(";");
        builder.append(this._phoneNumber).append(";");
        builder.append(this._company).append(";");
        builder.append(this._emails.toString());
        return builder.toString();
    }

}

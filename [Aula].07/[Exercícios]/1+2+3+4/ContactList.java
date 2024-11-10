import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

class ContactList extends ArrayList<Contact> {

    public void serialize(DataOutputStream out) throws IOException{
      out.writeInt(this.size());

      for(Contact c: this)
      c.serialize(out);
    }

    public static ContactList deserialize(DataInputStream in) throws IOException{
      int amount = in.readInt();
      Contact[] list = new Contact[amount];
      
      for(int i = 0; i < amount; i++)
      list[i] = Contact.deserialize(in);

      ContactList cl = new ContactList();
      cl.addAll(Arrays.asList(list));

      return cl;
    }

}


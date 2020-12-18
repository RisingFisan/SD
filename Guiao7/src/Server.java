import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


class ContactList {
    private List<Contact> contacts;

    public ContactList() {
        contacts = new ArrayList<>();

        contacts.add(new Contact("John", 20, 253123321, null, new ArrayList<>(Arrays.asList("john@mail.com"))));
        contacts.add(new Contact("Alice", 30, 253987654, "CompanyInc.", new ArrayList<>(Arrays.asList("alice.personal@mail.com", "alice.business@mail.com"))));
        contacts.add(new Contact("Bob", 40, 253123456, "Comp.Ld", new ArrayList<>(Arrays.asList("bob@mail.com", "bob.work@mail.com"))));
    }

    // @TODO
    public boolean addContact (Contact contact) throws IOException {
        contacts.add(contact);
        return true;
    }

    // @TODO
    public void getContacts (DataOutputStream out) throws IOException {
        System.out.println("B");
        out.writeInt(contacts.size());
        System.out.println("C");
        for(Contact contact : contacts) {
            contact.serialize(out);
            out.flush();
        }
    }
    
}

class ServerWorker implements Runnable {
    private Socket socket;
    private ContactList contactList;

    public ServerWorker (Socket socket, ContactList contactList) {
        this.contactList = contactList;
        this.socket = socket;
    }

    // @TODO
    @Override
    public void run() {
        try {
            DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            while (true) {
                int option = in.readInt();
                switch (option) {
                    case 1:
                        Contact newContact = Contact.deserialize(in);
                        contactList.addContact(newContact);
                        System.out.println(newContact.toString());
                        break;
                    case 2:
                        contactList.getContacts(out);
                        break;
                    case 0:
                        throw new EOFException();
                }
            }

        } catch (EOFException e) {
            System.out.println("Connection closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


public class Server {

    public static void main (String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(12345);
        ContactList contactList = new ContactList();

        while (true) {
            Socket socket = serverSocket.accept();
            Thread worker = new Thread(new ServerWorker(socket, contactList));
            worker.start();
        }
    }

}

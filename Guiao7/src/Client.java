import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Client {

    public static Contact parseLine (String userInput) {
        String[] tokens = userInput.split(" ");

        if (tokens[3].equals("null")) tokens[3] = null;

        return new Contact(
                tokens[0],
                Integer.parseInt(tokens[1]),
                Long.parseLong(tokens[2]),
                tokens[3],
                new ArrayList<>(Arrays.asList(tokens).subList(4, tokens.length)));
    }


    public static void main (String[] args) throws IOException {
        Socket socket = new Socket("localhost", 12345);

        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

        System.out.println("1) Adicionar novo contacto.\n2) Imprimir lista contactos\n\n0) Sair\n\nDigite a opção pretendida:");

        String userInput;
        while ((userInput = stdin.readLine()) != null) {
            int option = Integer.parseInt(userInput);
            out.writeInt(option);
            out.flush();
            switch (option) {
                case 1:
                    userInput = stdin.readLine();
                    Contact newContact = parseLine(userInput);
                    newContact.serialize(out);
                    out.flush();
                    System.out.println(newContact.toString());
                    break;
                case 2:
                    int n_contacts = in.readInt();
                    List<Contact> contacts = new ArrayList<>(n_contacts);
                    for(int i = 0; i < n_contacts; i++) {
                        contacts.add(Contact.deserialize(in));
                    }
                    for (Contact contact : contacts)
                        System.out.println(contact.toString());
                    break;
                case 0:
                    socket.close();
                    return;
            }

        }

        socket.close();
    }
}

import java.io.*;
import java.util.*;

class Contact implements Serializable {
    private String name;
    private int age;
    private long phoneNumber;
    private String company;     // Pode ser null
    private List<String> emails;

    public Contact (String name, int age, long phone_number, String company, List<String> emails) {
        this.name = name;
        this.age = age;
        this.phoneNumber = phone_number;
        this.company = company;
        this.emails = new ArrayList<>(emails);
    }

    public String toString () {
        StringBuilder builder = new StringBuilder("{");
        builder.append(this.name).append(";");
        builder.append(this.age).append(";");
        builder.append(this.phoneNumber).append(";");
        builder.append(this.company).append(";");
        builder.append("{");
        for (String s : this.emails) {
            builder.append(s).append(";");
        }
        builder.append("}");
        return builder.toString();
    }

    public void serialize(DataOutputStream out) throws IOException {
        out.writeUTF(this.name);
        out.writeInt(this.age);
        out.writeLong(this.phoneNumber);
        out.writeBoolean(this.company != null);
        if(this.company != null)
            out.writeUTF(this.company);
        out.writeInt(this.emails.toArray().length);
        for(String email : emails)
            out.writeUTF(email);
    }

    public static Contact deserialize(DataInputStream in) throws IOException {
        String name = in.readUTF();
        int age = in.readInt();
        long phoneNumber = in.readLong();
        boolean hasCompany = in.readBoolean();
        String company = null;
        if(hasCompany)
            company = in.readUTF();
        int nEmails = in.readInt();
        List<String> emails = new ArrayList<>();
        for(int i = 0; i < nEmails; i++) {
            emails.add(in.readUTF());
        }
        return new Contact(name,age,phoneNumber,company,emails);
    }
}

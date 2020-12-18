import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(12345);

        while (true) {
            Socket socket = ss.accept();
            Thread worker = new Thread(new ServerWorker(socket));
            worker.start();
        }
    }
}

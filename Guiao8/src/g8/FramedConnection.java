package g8;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

public class FramedConnection implements AutoCloseable {

    DataInputStream dis;
    DataOutputStream dos;
    ReentrantLock rl = new ReentrantLock();
    ReentrantLock wl = new ReentrantLock();

    public FramedConnection(Socket socket) throws IOException {
        this.dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        this.dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

    public void send(byte[] data) throws IOException {
        try {
            wl.lock();
            this.dos.writeInt(data.length);
            this.dos.write(data);
            this.dos.flush();
        }
        finally {
            wl.unlock();
        }
    }

    public byte[] receive() throws IOException {
        try {
            rl.lock();
            int n = this.dis.readInt();
            byte[] received = new byte[n];
            this.dis.readFully(received);
            return received;
        }
        finally {
            rl.unlock();
        }
    }

    @Override
    public void close() throws IOException {
        this.dis.close();
        this.dos.close();
    }
}

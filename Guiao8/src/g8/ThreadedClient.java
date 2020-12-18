package g8;

import java.net.Socket;

public class ThreadedClient {
    public static void main(String[] args) throws Exception {
        Socket s = new Socket("localhost", 12345);
        Demultiplexer m = new Demultiplexer(new TaggedConnection(s));
        m.start();

        Thread[] threads = {

            new Thread(() -> {
                try  {
                    // send request
                    m.send(1, "Ola".getBytes());
                    Thread.sleep(100);
                    // get reply
                    byte[] data = m.receive(1);
                    System.out.println("(1) Reply: " + new String(data));
                }  catch (Exception ignored) {}
            }),

            new Thread(() -> {
                try  {
                    // send request
                    m.send(3, "Hello".getBytes());
                    Thread.sleep(100);
                    // get reply
                    byte[] data = m.receive(3);
                    System.out.println("(2) Reply: " + new String(data));
                }  catch (Exception ignored) {}
            }),

            new Thread(() -> {
                try  {
                    // One-way
                    m.send(0, ":-p".getBytes());
                }  catch (Exception ignored) {}
            }),

            new Thread(() -> {
                try  {
                    // Get stream of messages until empty msg
                    m.send(2, "ABCDE".getBytes());
                    for (;;) {
                        byte[] data = m.receive(2);
                        if (data.length == 0)
                            break;
                        System.out.println("(4) From stream: " + new String(data));
                    }
                } catch (Exception ignored) {}
            }),

            new Thread(() -> {
                try  {
                    // Get stream of messages until empty msg
                    m.send(4, "123".getBytes());
                    for (;;) {
                        byte[] data = m.receive(4);
                        if (data.length == 0)
                            break;
                        System.out.println("(5) From stream: " + new String(data));
                    }
                } catch (Exception ignored) {}
            })

        };

        for (Thread t: threads) t.start();
        for (Thread t: threads) t.join();
        m.close();
    }
}

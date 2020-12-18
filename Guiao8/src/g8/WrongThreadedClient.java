package g8;

import java.net.Socket;
import static g8.TaggedConnection.Frame;

public class WrongThreadedClient {
    public static void main(String[] args) throws Exception {
        Socket s = new Socket("localhost", 12345);
        TaggedConnection c = new TaggedConnection(s);

        Thread[] threads = {

            new Thread(() -> {
                try  {
                    // send request
                    c.send(1, "Ola".getBytes());
                    Thread.sleep(100);
                    // get reply
                    Frame f = c.receive();
                    assert f.tag == 1;  // can fail
                    System.out.println("(1) Reply: " + new String(f.data));
                }  catch (Exception ignored) {}
            }),

            new Thread(() -> {
                try  {
                    // send request
                    c.send(3, "Hello".getBytes());
                    Thread.sleep(100);
                    // get reply
                    Frame f = c.receive();
                    assert f.tag == 3;  // can fail
                    System.out.println("(2) Reply: " + new String(f.data));
                }  catch (Exception ignored) {}
            }),

            new Thread(() -> {
                try  {
                    // One-way
                    c.send(0, ":-p".getBytes());
                }  catch (Exception ignored) {}
            }),

            new Thread(() -> {
                try  {
                    // Get stream of messages until empty msg
                    c.send(2, "ABCDE".getBytes());
                    for (;;) {
                        Frame f = c.receive();
                        assert f.tag == 2;  // can fail
                        if (f.data.length == 0)
                            break;
                        System.out.println("(4) From stream: " + new String(f.data));
                    }
                } catch (Exception ignored) {}
            }),

            new Thread(() -> {
                try  {
                    // Get stream of messages until empty msg
                    c.send(4, "123".getBytes());
                    for (;;) {
                        Frame f = c.receive();
                        assert f.tag == 4;  // can fail
                        if (f.data.length == 0)
                            break;
                        System.out.println("(5) From stream: " + new String(f.data));
                    }
                } catch (Exception ignored) {}
            })

        };

        for (Thread t: threads) t.start();
        for (Thread t: threads) t.join();
        c.close();
    }
}

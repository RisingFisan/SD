package g8;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Demultiplexer {

    private TaggedConnection tc;
    private ReentrantLock l = new ReentrantLock();
    private Map<Integer, FrameValue> map = new HashMap<>();
    private IOException exception = null;

    private class FrameValue {
        int waiters = 0;
        Queue<byte[]> queue = new ArrayDeque<>();
        Condition c = l.newCondition();

        public FrameValue() {

        }
    }

    public Demultiplexer(TaggedConnection conn) throws IOException {
        this.tc = conn;
    }

    public void start() {
        new Thread(() -> {
            try {
                while (true) {
                    TaggedConnection.Frame frame = tc.receive();
                    l.lock();
                    try {
                        FrameValue fv = map.get(frame.tag);
                        if (fv == null) {
                            fv = new FrameValue();
                            map.put(frame.tag, fv);
                        }
                        fv.queue.add(frame.data);
                        fv.c.signal();
                    }
                    finally {
                        l.unlock();
                    }
                }
            }
            catch (IOException e) {
                exception = e;
            }
        }).start();
    }

    public void send(TaggedConnection.Frame frame) throws IOException {
        tc.send(frame);
    }

    public void send(int tag, byte[] data) throws IOException {
        tc.send(tag, data);
    }

    public byte[] receive(int tag) throws IOException, InterruptedException {
        l.lock();
        FrameValue fv;
        try {
            fv = map.get(tag);
            if (fv == null) {
                fv = new FrameValue();
                map.put(tag, fv);
            }
            fv.waiters++;
            while(true) {
                if(! fv.queue.isEmpty()) {
                    fv.waiters--;
                    byte[] reply = fv.queue.poll();
                    if (fv.waiters == 0 && fv.queue.isEmpty())
                        map.remove(tag);
                    return reply;
                }
                if (exception != null) {
                    throw exception;
                }
                fv.c.await();
            }
        }
        finally {
            l.unlock();
        }
    }


    public void close() throws IOException {
        tc.close();
    }
}

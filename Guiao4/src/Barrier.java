import java.sql.SQLOutput;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Barrier {
    private Lock l = new ReentrantLock();
    private Condition c = l.newCondition();

    private int counter = 0;
    private final int N;

    private int epoch;
    //private boolean open = false;

    Barrier (int N) {
        this.N = N;
        epoch = 0;
    }
/*
    void await() throws InterruptedException {
        l.lock();
        try {
            while (open)
                c.await();
            //if (counter == N) counter = 0;
            counter += 1;
            if (counter < N) {
                while (counter < N)
                    c.await();
            }
            else {
                open = true;
                c.signalAll();
            }
            counter -= 1;
            if (counter == 0) {
                open = false;
                c.signalAll();
            }
        }
        finally {
            l.unlock();
        }
    } */

    void await() throws InterruptedException {
        l.lock();
        try {
            int epoch = this.epoch;
            counter += 1;
            if (counter < N) {
                while (epoch == this.epoch)
                    c.await();
            }
            else {
                this.epoch += 1;
                counter = 0;
                c.signalAll();
            }
        }
        finally {
            l.unlock();
        }
    }
}

class Main {
    public static void main(String[] args) {
        Barrier b = new Barrier(3);

        new Thread(() -> {
            try {
                Thread.sleep(1000);
                System.out.println("Vou fazer await");
                b.await();
            } catch (Exception e) {}
            System.out.println("await retornou");
            try {
                Thread.sleep(2000);
                System.out.println("Vou fazer await");
                b.await();
            } catch (Exception e) {}
            System.out.println("await retornou");
        }).start();

        new Thread(() -> {
            try {
                Thread.sleep(3000);
                System.out.println("Vou fazer await");
                b.await();
            } catch (Exception e) {}
            System.out.println("await retornou");
            try {
                Thread.sleep(1000);
                System.out.println("Vou fazer await");
                b.await();
            } catch (Exception e) {}
            System.out.println("await retornou");
        }).start();

        new Thread(() -> {
            try {
                Thread.sleep(2000);
                System.out.println("Vou fazer await");
                b.await();
            } catch (Exception e) {}
            System.out.println("await retornou");
            try {
                Thread.sleep(3000);
                System.out.println("Vou fazer await");
                b.await();
            } catch (Exception e) {}
            System.out.println("await retornou");
        }).start();
    }
}
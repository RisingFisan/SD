import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

class Warehouse {
  Lock l = new ReentrantLock();
  private Map<String, Product> m =  new HashMap<String, Product>();

  private class Product {
    Condition c = l.newCondition();
    int q = 0;
  }

  private Product get(String s) {
    Product p;
    l.lock();
    try {
      p = m.get(s);
      if (p != null) return p;
    }
    finally {
      l.unlock();
    }
    p = new Product();
    m.put(s, p);
    return p;
  }

  public void supply(String s, int q) {
    l.lock();
    Product p = get(s);
    p.q += q;
    p.c.signalAll();
    l.unlock();
  }

  // Errado se faltar algum produto...
  public void consume(String[] a) throws InterruptedException {
    l.lock();
    for (String s : Arrays.stream(a).sorted().collect(Collectors.toCollection(ArrayList::new))) {
        Product p = get(s);
        while(p.q == 0)
          p.c.await();
        p.q -= 1;
    }
    l.unlock();
  }
}

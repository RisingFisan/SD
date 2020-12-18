import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class Bank {

    ReadWriteLock lock = new ReentrantReadWriteLock();

    private static class Account {
        public ReadWriteLock lock = new ReentrantReadWriteLock();
        private int balance;
        Account(int balance) { this.balance = balance; }
        int balance() { return balance; }
        boolean deposit(int value) {
            balance += value;
            return true;
        }
        boolean withdraw(int value) {
            if (value > balance)
                return false;
            balance -= value;
            return true;
        }
    }

    private Map<Integer, Account> map = new HashMap<Integer, Account>();
    private int nextId = 0;

    // create account and return account id
    public int createAccount(int balance) {
        Account c = new Account(balance);
        lock.writeLock().lock();
        int id = nextId;
        nextId += 1;
        map.put(id, c);
        lock.writeLock().unlock();
        return id;
    }

    // close account and return balance, or 0 if no such account
    public int closeAccount(int id) {
        Account c;
        lock.writeLock().lock();
        try {
            c = map.remove(id);
            if (c == null)
                return 0;
            c.lock.readLock().lock();
        }
        finally {
            lock.writeLock().unlock();
        }
        try {
            return c.balance();
        }
        finally {
            c.lock.readLock().unlock();
        }
    }

    // account balance; 0 if no such account
    public int balance(int id) {
        Account c;
        lock.readLock().lock();
        try {
            c = map.get(id);
            if (c == null)
                return 0;
            c.lock.readLock().lock();
        }
        finally {
            lock.readLock().unlock();
        }
        try {
            return c.balance();
        }
        finally {
            c.lock.readLock().unlock();
        }
    }

    // deposit; fails if no such account
    public boolean deposit(int id, int value) {
        Account c;
        lock.readLock().lock();
        try {
            c = map.get(id);
            if (c == null)
                return false;
            c.lock.writeLock().lock();
        }
        finally {
            lock.readLock().unlock();
        }
        try {
            return c.deposit(value);
        }
        finally {
            c.lock.writeLock().unlock();
        }
    }

    // withdraw; fails if no such account or insufficient balance
    public boolean withdraw(int id, int value) {
        Account c;
        lock.readLock().lock();
        try {
            c = map.get(id);
            if (c == null)
                return false;
            c.lock.writeLock().lock();
        }
        finally {
            lock.readLock().unlock();
        }
        try {
            return c.withdraw(value);
        }
        finally {
            c.lock.writeLock().unlock();
        }
    }

    // transfer value between accounts;
    // fails if either account does not exist or insufficient balance
    public boolean transfer(int from, int to, int value) {
        Account cfrom, cto;
        lock.readLock().lock();
        try {
            cfrom = map.get(from);
            cto = map.get(to);
            if (cfrom == null || cto == null)
                return false;
            if(from < to) {
                cfrom.lock.writeLock().lock();
                cto.lock.writeLock().lock();
            }
            else {
                cto.lock.writeLock().lock();
                cfrom.lock.writeLock().lock();
            }
        }
        finally {
            lock.readLock().unlock();
        }
        try {
            try {
                if (!cfrom.withdraw(value))
                    return false;
            } finally {
                cfrom.lock.writeLock().unlock();
            }
            return cto.deposit(value);
        }
        finally {
            cto.lock.writeLock().unlock();
        }
    }

    // sum of balances in set of accounts; 0 if some does not exist
    public int totalBalance(int[] ids) {
        List<Account> cs = new ArrayList<>();
        lock.readLock().lock();
        try {
            for (int i : Arrays.stream(ids).sorted().toArray()) {
                Account c = map.get(i);
                if (c == null)
                    return 0;
                cs.add(c);
            }
            for(Account c : cs)
                c.lock.readLock().lock();
        }
        finally {
            lock.readLock().unlock();
        }
        int total = 0;
        for (Account c : cs) {
            total += c.balance();
            c.lock.readLock().unlock();
        }
        return total;
  }

}

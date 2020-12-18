import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Bank {
  private static class Account {
    Lock lock = new ReentrantLock();
    private int balance;
    Account(int balance) { this.balance = balance; }
    int balance() { return balance; }
    boolean deposit(int value) {
      try {
        lock.lock();
        balance += value;
        return true;
      }
      finally {
        lock.unlock();
      }
    }
    boolean withdraw(int value) {
      try {
        lock.lock();
        if (value > balance)
          return false;
        balance -= value;
        return true;
      }
      finally {
        lock.unlock();
      }
    }
  }

  // Bank slots and vector of accounts
  private int slots;
  private Account[] av; 
  //Lock lock = new ReentrantLock();

  public Bank(int n)
  {
    slots=n;
    av=new Account[slots];
    for (int i=0; i<slots; i++) av[i]=new Account(0);
  }

  // Account balance
  public int balance(int id) {/*
    try {
      lock.lock();*/
      if (id < 0 || id >= slots)
        return 0;
      return av[id].balance();/*
    }
    finally {
      lock.unlock();
    }*/
  }

  // Deposit
  boolean deposit(int id, int value) {/*
    try {
      lock.lock();*/
      if (id < 0 || id >= slots)
        return false;
      return av[id].deposit(value);/*
    }
    finally {
      lock.unlock();
    }*/
  }

  // Withdraw; fails if no such account or insufficient balance
  public boolean withdraw(int id, int value) {/*
    try {
      lock.lock();*/
      if (id < 0 || id >= slots)
        return false;
      return av[id].withdraw(value);/*
    }
    finally {
      lock.unlock();
    }*/
  }

  public boolean transfer(int from, int to, int value) {/*
    try {
      lock.lock();*/
      if (!withdraw(from, value)) return false;
      return deposit(to, value);/*
    }
    finally {
      lock.unlock();
    }*/
  }

  int totalBalance() {/*
    try {
      lock.lock();*/
      return Arrays.stream(this.av)
              .mapToInt(Account::balance)
              .sum();/*
    }
    finally {
      lock.unlock();
    } */
  }
}

public class Ex2 {

    public static void main(String[] args) {
        final int  N = 10;
        Bank bank = new Bank();
        Thread threads[] = new Thread[N];
        Deposit deposit = new Deposit(bank);

        for(int i = 0; i < N; i++) {
            threads[i] = new Thread(deposit);
        }
        for(int i = 0; i < N; i++) {
            threads[i].run();
        }

        for(int i = 0; i < N; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Account balance: " + bank.balance());
    }
}

class Deposit implements Runnable {
    Bank bank;

    public Deposit(Bank bank) {
        this.bank = bank;
    }

    public void run() {
        int I = 1000;
        int V = 100;

        for(int j = 0; j < I; j++) {
            bank.deposit(V);
        }
    }
}
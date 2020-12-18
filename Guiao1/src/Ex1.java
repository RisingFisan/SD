public class Ex1 {
    public static void main(String[] args) {
        int N = 10;

        Thread threads[] = new Thread[N];
        Incrementer inc = new Incrementer();
        for(int i = 0; i < N; i++) {
            threads[i] = new Thread(inc);
        }
        for(int i = 0; i < N; i++) {
            threads[i].start();
        }
        for(int i = 0; i < N; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Fim.");
    }
}

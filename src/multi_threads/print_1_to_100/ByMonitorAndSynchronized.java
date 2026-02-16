package multi_threads.print_1_to_100;

public class ByMonitorAndSynchronized {
    private static final int MAX = 100;
    private static final int THREADS = 4;
    private static final Object LOCK = new Object();
    private static int counter = 1;

    public static void main(String[] args) {
        for (int i = 0; i < THREADS; i++) {
            int threadId = i;
            Runnable task = () -> {
                while (true) {
                    synchronized (LOCK) {
                        while (counter <= MAX && (counter - 1) % THREADS != threadId) {
                            try {
                                LOCK.wait();
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                return;
                            }
                        }
                        if (counter > MAX) {
                            LOCK.notifyAll();
                            return;
                        }
                        System.out.println(Thread.currentThread().getName() + " -> " + counter++);
                        LOCK.notifyAll();
                    }
                }
            };
            new Thread(task, "T" + (i + 1)).start();
        }
    }
}

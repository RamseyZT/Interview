package multi_threads.print_1_to_100;

public class ByMonitorAndSynchronized {
    private static final int MAX = 100;
    private static final int THREADS = 4;
    // 使用同一个监视器锁进行互斥和轮转协调。
    private static final Object LOCK = new Object();
    private static int counter = 1;

    public static void main(String[] args) {
        for (int i = 0; i < THREADS; i++) {
            int threadId = i;
            Runnable task = () -> {
                while (true) {
                    synchronized (LOCK) {
                        // 等待轮到当前线程。
                        while (counter <= MAX && (counter - 1) % THREADS != threadId) {
                            try {
                                LOCK.wait();
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                return;
                            }
                        }
                        if (counter > MAX) {
                            // 唤醒其他线程，便于它们退出。
                            LOCK.notifyAll();
                            return;
                        }
                        System.out.println(Thread.currentThread().getName() + " -> " + counter++);
                        // 交棒给下一个线程。
                        LOCK.notifyAll();
                    }
                }
            };
            new Thread(task, "T" + (i + 1)).start();
        }
    }
}

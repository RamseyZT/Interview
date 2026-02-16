package multi_threads.print_1_to_100;

import java.util.concurrent.Semaphore;

public class BySemaphore {
    private static final int MAX = 100;
    private static final int THREADS = 4;
    // 每个线程一个信号量，用于严格轮转。
    private static final Semaphore[] SEMAPHORES = new Semaphore[THREADS];
    private static int counter = 1;

    public static void main(String[] args) {
        for (int i = 0; i < THREADS; i++) {
            // 只有线程0一开始有许可。
            SEMAPHORES[i] = new Semaphore(i == 0 ? 1 : 0);
        }

        for (int i = 0; i < THREADS; i++) {
            int threadId = i;
            Runnable task = () -> {
                while (true) {
                    try {
                        // 等待当前线程的许可。
                        SEMAPHORES[threadId].acquire();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }

                    if (counter > MAX) {
                        // 放行下一个线程，便于它退出。
                        SEMAPHORES[(threadId + 1) % THREADS].release();
                        return;
                    }

                    System.out.println(Thread.currentThread().getName() + " -> " + counter++);
                    // 把许可交给下一个线程。
                    SEMAPHORES[(threadId + 1) % THREADS].release();
                }
            };
            new Thread(task, "T" + (i + 1)).start();
        }
    }
}

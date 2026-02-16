package multi_threads.print_1_to_100;

import java.util.concurrent.CountDownLatch;

public class ByCountDownLatch {
    private static final int MAX = 100;
    private static final int THREADS = 4;
    // 每轮一个闩锁链，释放一次就允许一个线程继续。
    private static final CountDownLatch[] LATCHES = new CountDownLatch[THREADS];
    private static int counter = 1;

    public static void main(String[] args) {
        for (int i = 0; i < THREADS; i++) {
            LATCHES[i] = new CountDownLatch(i == 0 ? 0 : 1);
        }

        for (int i = 0; i < THREADS; i++) {
            int threadId = i;
            Runnable task = () -> {
                while (true) {
                    try {
                        // 等待当前线程对应的闩锁被释放。
                        LATCHES[threadId].await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }

                    if (counter > MAX) {
                        // 释放下一个线程，便于它退出。
                        LATCHES[(threadId + 1) % THREADS].countDown();
                        return;
                    }

                    System.out.println(Thread.currentThread().getName() + " -> " + counter++);
                    // 重置当前线程的闩锁，并释放下一个线程。
                    LATCHES[threadId] = new CountDownLatch(1);
                    LATCHES[(threadId + 1) % THREADS].countDown();
                }
            };
            new Thread(task, "T" + (i + 1)).start();
        }
    }
}

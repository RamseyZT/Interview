package multi_threads.print_1_to_100;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ByBlockingQueue {
    private static final int MAX = 100;
    private static final int THREADS = 4;
    // 用令牌队列来强制轮转顺序。
    private static final BlockingQueue<Integer>[] QUEUES = new BlockingQueue[THREADS];
    private static int counter = 1;

    public static void main(String[] args) {
        for (int i = 0; i < THREADS; i++) {
            QUEUES[i] = new ArrayBlockingQueue<>(1);
        }

        try {
            // 先放入第一个令牌，让线程0启动。
            QUEUES[0].put(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        for (int i = 0; i < THREADS; i++) {
            int threadId = i;
            Runnable task = () -> {
                while (true) {
                    try {
                        // 等待上一个线程传来的令牌。
                        QUEUES[threadId].take();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }

                    if (counter > MAX) {
                        // 把令牌传下去，便于下一个线程退出。
                        QUEUES[(threadId + 1) % THREADS].offer(1);
                        return;
                    }

                    System.out.println(Thread.currentThread().getName() + " -> " + counter++);
                    // 把令牌交给下一个线程。
                    QUEUES[(threadId + 1) % THREADS].offer(1);
                }
            };
            new Thread(task, "T" + (i + 1)).start();
        }
    }
}

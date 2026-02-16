package multi_threads.print_1_to_100;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ByCondition {
    private static final int MAX = 100;
    private static final int THREADS = 4;
    // 显式锁 + 每个线程一个条件变量。
    private static final ReentrantLock LOCK = new ReentrantLock();
    private static final Condition[] CONDITIONS = new Condition[THREADS];
    private static int counter = 1;
    // 记录下一次该由哪个线程打印。
    private static int turn = 0;

    public static void main(String[] args) {
        for (int i = 0; i < THREADS; i++) {
            CONDITIONS[i] = LOCK.newCondition();
        }

        for (int i = 0; i < THREADS; i++) {
            int threadId = i;
            Runnable task = () -> {
                while (true) {
                    LOCK.lock();
                    try {
                        // 等待轮到当前线程。
                        while (counter <= MAX && turn != threadId) {
                            CONDITIONS[threadId].await();
                        }
                        if (counter > MAX) {
                            // 通知下一个线程，便于它退出。
                            CONDITIONS[(threadId + 1) % THREADS].signal();
                            return;
                        }
                        System.out.println(Thread.currentThread().getName() + " -> " + counter++);
                        // 轮转到下一个线程并唤醒它。
                        turn = (turn + 1) % THREADS;
                        CONDITIONS[turn].signal();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    } finally {
                        LOCK.unlock();
                    }
                }
            };
            new Thread(task, "T" + (i + 1)).start();
        }
    }
}

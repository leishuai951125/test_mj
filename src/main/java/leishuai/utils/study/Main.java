package leishuai.utils.study;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description TODO
 * @Author leishuai
 * @Date 2019/3/30 12:02
 * @Version 1.0
 */
public class Main {
    static int sum = 0;
    static Main main = new Main();
    volatile boolean isLock = false;

    public static void main(String[] args) throws InterruptedException {
        for (int k = 0; k < 10; k++) {
            new Thread() {
                @Override
                public void run() {
                    for (int kk = 0; kk < 100000; kk++) {
                        main.addOne();
//                        try {
//                            main.addOneSafe();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
                    }
                }
            }.start();
        }
        Thread.sleep(1000);
        System.out.println("sum:" + sum);
    }

    void addOne() {
        sum++;
    }

    void addOneSafe() throws InterruptedException {
        main.lock();
        sum++;
        main.unLock();
    }

    synchronized void lock() throws InterruptedException {
        while (true) {
            if (!isLock) {
                isLock = !isLock;
                break;
            } else {
                this.wait();
            }
        }
    }

    synchronized void unLock() {
        isLock = false;
        notifyAll();
    }

}



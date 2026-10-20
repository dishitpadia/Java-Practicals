import java.util.concurrent.atomic.AtomicLong;

class SharedData {
    static final int SIZE = 1_000_000;
    static final int NUM_THREADS = 4;

    static int[] arr = new int[SIZE];

    static long sharedTotal = 0;
    static long synchronizedTotal = 0;
    static AtomicLong atomicTotal = new AtomicLong(0);

    static final Object lock = new Object();

    static {
        for (int i = 0; i < SIZE; i++) {
            arr[i] = 1;
        }
    }
}

class Worker extends Thread {
    int start, end, method;

    Worker(int start, int end, int method) {
        this.start = start;
        this.end = end;
        this.method = method;
    }

    public void run() {
        for (int i = start; i < end; i++) {

            if (method == 1) {
                SharedData.sharedTotal += SharedData.arr[i];
            } 
            else if (method == 2) {
                synchronized (SharedData.lock) {
                    SharedData.synchronizedTotal += SharedData.arr[i];
                }
            } 
            else if (method == 3) {
                SharedData.atomicTotal.addAndGet(SharedData.arr[i]);
            }
        }
    }
}

class ThreadRunner {

    static double execute(int method) throws InterruptedException {
        Thread[] threads = new Thread[SharedData.NUM_THREADS];

        int chunkSize = SharedData.SIZE / SharedData.NUM_THREADS;

        long startTime = System.nanoTime();

        for (int i = 0; i < SharedData.NUM_THREADS; i++) {
            int start = i * chunkSize;
            int end = (i == SharedData.NUM_THREADS - 1)
                    ? SharedData.SIZE
                    : start + chunkSize;

            threads[i] = new Worker(start, end, method);
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        long endTime = System.nanoTime();

        return (endTime - startTime) / 1_000_000.0;
    }
}

public class ArraySplit {

    public static void main(String[] args) throws InterruptedException {

        long expectedTotal = SharedData.SIZE;

        double wrongTime = ThreadRunner.execute(1);
        double synchronizedTime = ThreadRunner.execute(2);
        double atomicTime = ThreadRunner.execute(3);

        System.out.println("Expected Total: " + expectedTotal);

        System.out.println("\n1. Without Synchronization (Race Condition)");
        System.out.println("Total: " + SharedData.sharedTotal);
        System.out.println("Time: " + wrongTime + " ms");

        System.out.println("\n2. Using synchronized");
        System.out.println("Total: " + SharedData.synchronizedTotal);
        System.out.println("Time: " + synchronizedTime + " ms");

        System.out.println("\n3. Using AtomicLong");
        System.out.println("Total: " + SharedData.atomicTotal.get());
        System.out.println("Time: " + atomicTime + " ms");

        System.out.println("\n--- Performance Comparison ---");
        System.out.println("Without synchronization: " + wrongTime + " ms");
        System.out.println("synchronized: " + synchronizedTime + " ms");
        System.out.println("AtomicLong: " + atomicTime + " ms");

        System.out.println("\nNote: Execution times vary by system.");
    }
}
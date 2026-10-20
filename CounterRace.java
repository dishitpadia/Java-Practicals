class Counter {
    int count = 0;

    public synchronized void increment() {
        count++;
    }
}

public class CounterRace {
    public static void main(String[] args) throws InterruptedException {
        Counter counter = new Counter();

        int numberOfThreads = 10;
        int incrementsPerThread = 100000;

        Thread[] threads = new Thread[numberOfThreads];

        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    counter.increment();
                }
            });

            threads[i].start();
        }

        for (int i = 0; i < numberOfThreads; i++) {
            threads[i].join();
        }

        int expectedCount = numberOfThreads * incrementsPerThread;

        System.out.println("Expected Count: " + expectedCount);
        System.out.println("Actual Count: " + counter.count);
    }
}
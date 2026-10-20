class Resource {
    String name;

    Resource(String name) {
        this.name = name;
    }
}

class DeadlockTask1 implements Runnable {
    private final Resource resourceA;
    private final Resource resourceB;

    DeadlockTask1(Resource resourceA, Resource resourceB) {
        this.resourceA = resourceA;
        this.resourceB = resourceB;
    }

    @Override
    public void run() {
        synchronized (resourceA) {
            System.out.println("Thread 1 locked Resource A");

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            System.out.println("Thread 1 waiting for Resource B");

            synchronized (resourceB) {
                System.out.println("Thread 1 locked Resource B");
            }
        }
    }
}

class DeadlockTask2 implements Runnable {
    private final Resource resourceA;
    private final Resource resourceB;

    DeadlockTask2(Resource resourceA, Resource resourceB) {
        this.resourceA = resourceA;
        this.resourceB = resourceB;
    }

    @Override
    public void run() {
        synchronized (resourceB) {
            System.out.println("Thread 2 locked Resource B");

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            System.out.println("Thread 2 waiting for Resource A");

            synchronized (resourceA) {
                System.out.println("Thread 2 locked Resource A");
            }
        }
    }
}

class SafeTask implements Runnable {
    private final Resource resourceA;
    private final Resource resourceB;
    private final int threadId;

    SafeTask(Resource resourceA, Resource resourceB, int threadId) {
        this.resourceA = resourceA;
        this.resourceB = resourceB;
        this.threadId = threadId;
    }

    @Override
    public void run() {
        synchronized (resourceA) {
            System.out.println("Thread " + threadId + " locked Resource A");

            synchronized (resourceB) {
                System.out.println("Thread " + threadId + " locked Resource B");
            }
        }

        System.out.println("Thread " + threadId + " completed successfully");
    }
}

public class DeadlockDemo {
    public static void main(String[] args) {

        Resource resourceA = new Resource("Resource A");
        Resource resourceB = new Resource("Resource B");

        // System.out.println("1. Demonstrating Deadlock");

        // Thread t1 = new Thread(new DeadlockTask1(resourceA, resourceB));
        // Thread t2 = new Thread(new DeadlockTask2(resourceA, resourceB));

        // t1.start();
        // t2.start();

        // To demonstrate the solution, comment out the two lines above
        // and uncomment the following lines.

        
        System.out.println("2. Demonstrating Deadlock Prevention");

        Thread t3 = new Thread(new SafeTask(resourceA, resourceB, 1));
        Thread t4 = new Thread(new SafeTask(resourceA, resourceB, 2));

        t3.start();
        t4.start();
    }
}
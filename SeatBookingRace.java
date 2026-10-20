class SeatBooking {
    int seatsLeft = 5;
    int successfulBookings = 0;

    public void bookWithoutSync() {
        if (seatsLeft > 0) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            seatsLeft--;
            successfulBookings++;
            System.out.println(Thread.currentThread().getName() + " booked a seat.");
        }
    }

    public synchronized void book() {
        if (seatsLeft > 0) {
            seatsLeft--;
            successfulBookings++;
            System.out.println(Thread.currentThread().getName() + " booked a seat.");
        }
    }
}

public class SeatBookingRace {
    public static void main(String[] args) throws InterruptedException {

        SeatBooking booking1 = new SeatBooking();
        Thread[] threads = new Thread[10];

        System.out.println("=== Without Synchronization ===");

        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(
                () -> booking1.bookWithoutSync(),
                "Thread-" + (i + 1)
            );
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("Successful bookings: " + booking1.successfulBookings);
        System.out.println("Seats left: " + booking1.seatsLeft);

        SeatBooking booking2 = new SeatBooking();

        System.out.println("\n=== With Synchronization ===");

        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(
                () -> booking2.book(),
                "Thread-" + (i + 1)
            );
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("Successful bookings: " + booking2.successfulBookings);
        System.out.println("Seats left: " + booking2.seatsLeft);
    }
}
class CinemaShow {

    private String title;
    private int seatsAvailable;
    private final int Capacity;
    private static int totalBooked = 0;

    public CinemaShow(String title, int capacity) {
        this.title = title;
        this.Capacity = capacity;
        this.seatsAvailable = capacity;
    }

    public CinemaShow(String title) {
        this(title, 100);
    }

    public boolean book(int n) {
        if (n <= seatsAvailable) {
            seatsAvailable -= n;
            totalBooked += n;
            return true;
        }

        else
            return false;
    }

    public void cancel(int n) {
        if (n <= totalBooked && n <= Capacity) {
            totalBooked -= n;
            seatsAvailable += n;
        }
    }

    public int getSeatsAvailable() {
        return seatsAvailable;
    }

    public int getTotalBooked() {
        return totalBooked;
    }

    public String getTitle()
    {
        return title;
    }
}

public class CinemaShowDemo {
    public static void main(String[] args) {
    CinemaShow show = new CinemaShow("Alpha", 100);

    System.out.println("Show title : " + show.getTitle());

    boolean available = show.book(30);

    if (available) {
        System.out.println("Seats booked successfully");

    }

    else {
        System.out.println("Seats not available");
    }

    show.cancel(15);

    System.out.println("Number of seats available : " + show.getSeatsAvailable());
}
}

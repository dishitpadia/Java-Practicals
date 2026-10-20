class Thermostat {

    private String location;
    private int temperature;

    private static final int MIN = 16;
    private static final int MAX = 30;

    private static int activeCount = 0;

    public Thermostat(String location, int startTemp) {
        this.location = location;

        if (startTemp >= MIN && startTemp <= MAX)
            temperature = startTemp;
        else
            temperature = 22;

        activeCount++;
    }

    public Thermostat(String location) {
        this(location, 22);
    }

    public void raise() {
        if (temperature < MAX)
            temperature++;
        else
            System.out.println(location + " is already at maximum temperature.");
    }

    public void lower() {
        if (temperature > MIN)
            temperature--;
        else
            System.out.println(location + " is already at minimum temperature.");
    }

    public int getTemperature() {
        return temperature;
    }

    public String getLocation() {
        return location;
    }

    public static int getActiveCount() {
        return activeCount;
    }
}

public class ThermostatDemo {

    public static void main(String[] args) {

        Thermostat temp1 = new Thermostat("Living Room", 25);
        Thermostat temp2 = new Thermostat("Bedroom");

        System.out.println("Location: " + temp1.getLocation());

        for (int i = 0; i < 10; i++) {
            temp1.raise();
            System.out.println("Current temperature: " + temp1.getTemperature());
        }

        System.out.println();

        System.out.println("Location: " + temp2.getLocation());

        for (int i = 0; i < 10; i++) {
            temp2.raise();
            System.out.println("Current temperature: " + temp2.getTemperature());
        }

        System.out.println();

        for (int i = 0; i < 20; i++) {
            temp1.lower();
            System.out.println("Current temperature: " + temp1.getTemperature());
        }

        System.out.println();

        for (int i = 0; i < 20; i++) {
            temp2.lower();
            System.out.println("Current temperature: " + temp2.getTemperature());
        }

        System.out.println("\nActive Count: " + Thermostat.getActiveCount());
    }
}
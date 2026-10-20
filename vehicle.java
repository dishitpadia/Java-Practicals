import java.util.Scanner;

public record vehicle(String number, String type) {}

class tollBooth {

    public static void main(String args[]) {
        Scanner sc = new Scanner(System.in);

        int bike_counter = 0, car_counter = 0, truck_counter = 0, total_toll = 0;
        String type, number, frequent = "";

        while (true) {
            System.out.print("Enter number of vehicle : ");
            number = sc.nextLine();

            if (number.equalsIgnoreCase("done"))
                break;

            System.out.print("Enter the type of the vehicle : ");
            type = sc.nextLine();

            vehicle Vehicle = new vehicle(number, type);

            int toll = switch (Vehicle.type()) {
                case "BIKE" -> {
                    bike_counter++;
                    yield 20;
                }

                case "CAR" -> {
                    car_counter++;
                    yield 50;
                }

                case "TRUCK" -> {
                    truck_counter++;
                    yield 150;
                }

                default -> 0;
            };

            total_toll += toll;

        }

        if (bike_counter > car_counter && bike_counter > truck_counter) {
            frequent = "Bike";
        }

        else if (car_counter > bike_counter && car_counter > truck_counter) {
            frequent = "Car";
        }

        else if (truck_counter > bike_counter && truck_counter > car_counter) {
            frequent = "Truck";
        }

        System.out.println("Total toll : " + total_toll);
        System.out.println("Most frequent : " + frequent);

        sc.close();
    }
}

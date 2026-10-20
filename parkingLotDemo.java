class ParkingLot {
    private int twoWheelers;
    private int fourWheelers;
    private final int twoCap = 50;
    private final int fourCap = 25;

    private static long revenue = 0;

    void park(String type)
    {
        if(type == "two")
        {
            if(twoWheelers < twoCap)
            {
                System.out.println("Two wheeler parked");
                twoWheelers++;
                revenue += 20;
            }

            else
            {
                System.out.println("The parking lot for two wheelers is full");
            }
        }

        if(type == "four")
        {
            if(fourWheelers < fourCap)
            {
                System.out.println("Four wheeler parked");
                fourWheelers++;
                revenue += 40;
            }

            else
            {
                System.out.println("The parking lot for four wheelers is full");
            }
        }
    }

    void leave(String type)
    {
        if(type == "two")
        {
            if(twoWheelers > 0)
            {
                System.out.println("Two wheeler taken out from parking");
                twoWheelers--;
            }

            else
            {
                System.out.println("No four wheeler parked in the parking lot");
            }
        }

        if(type == "four")
        {
            if(fourWheelers > 0)
            {
                System.out.println("Four wheeler taken out from parking");
                fourWheelers--;
            }

            else
            {
                System.out.println("No four wheeler parked in the parking lot");
            }
        }
    }

    long getRevenue(){
        return revenue;
    }
}

public class parkingLotDemo {
    public static void main(String[] args) {
        ParkingLot parking = new ParkingLot();

        parking.park("two");
        parking.park("two");
        parking.park("four");
        parking.park("four");
        parking.park("two");

        parking.leave("two");
        parking.leave("two");
        parking.leave("two");
        parking.leave("four");
        parking.leave("four");

        System.out.println("Total revenue : " + parking.getRevenue());
        
    }
}
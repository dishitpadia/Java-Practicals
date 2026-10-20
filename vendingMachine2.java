import java.util.Scanner;

public class vendingMachine2 {
    
    public enum Coin
    {
        ONE,
        TWO,
        FIVE,
        TEN
    }

    public static void main(String args[])
    {
        int snack_price = 15;
        int total = 0;

        Scanner sc = new Scanner(System.in);

        while(!(total >= snack_price))
        {
            System.out.print("Enter the coin : ");

            String input = sc.next();

            Coin coin = Coin.valueOf(input.toUpperCase());

            int value = switch(coin)
            {
                case ONE -> 1;
                case TWO -> 2;
                case FIVE -> 5;
                case TEN -> 10;
            };

            total += value;
        }

        System.out.println("Paid. Change " + (total - snack_price));

        sc.close();
    }
}

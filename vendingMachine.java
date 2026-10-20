import java.util.Scanner;

public class vendingMachine {
    
    public enum Coin
    {
        ONE, 
        TWO, 
        FIVE,
        TEN
    }

    public static void main(String[] args)
    {
        int snack_price = 15;
        int total = 0;

        Scanner sc = new Scanner(System.in);

        while(!(total >= snack_price))
        {
            System.out.print("Enter the coin(ONE, TWO, FIVE, TEN) : ");
            
            String input = sc.next();
        
            Coin coin = Coin.valueOf(input);

            int value = switch(coin){
                case ONE -> 1;
                case TWO -> 2;
                case FIVE -> 5;
                case TEN -> 10;
            };

            total += value;
        }

        System.out.print("Paid. ");
        System.out.println("Change " + (total - snack_price));

        sc.close();
    }

}

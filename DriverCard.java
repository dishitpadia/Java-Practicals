public class DriverCard {
    public static void main(String[] args) {

        Card[] cards = new Card[5];

        cards[0] = new Card("Queen", "Hearts");
        cards[1] = new Card("Queen", "Spade");
        cards[2] = new Card("Queen", "Diamond");
        cards[3] = new Card("Queen", "Club");
        cards[4] = new Card("Queen", "Hearts");

        for (int i = 1; i < cards.length; i++) 
        {
            for (int j = 0; j < i; j++) 
            {
                if (cards[i].equals(cards[j])) 
                {
                    System.out.println("Duplicate found: " + cards[i]);
                    break;
                }
            }

            System.out.println("No duplicate found");
        }
    }
}

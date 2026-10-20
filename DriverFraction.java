public class DriverFraction {
    public static void main(String[] args) {
        Fraction f1 = new Fraction(2, 4);
        Fraction f2 = new Fraction(1, 2);

        System.out.println("Previous fraction : " + f1);
        System.out.println("new Fraction(2,4) : " + f1);
        System.out.println("new Fraction(2,4) equals new Fraction(1,2): " + f1.equals(f2));
    }
}

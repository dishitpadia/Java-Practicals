import java.util.Scanner;

public class Driverpassword {
    public static void main(String[] args) {

        passwordChecker passCheck = new passwordChecker();
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter your password: ");

        String password = sc.next();

        int count = passCheck.strength(password);

        if(count == 0 || count == 1)
            System.out.println("Weak: " + password);

        if(count == 2 || count == 3)
            System.out.println("Medium: " + password);

        if(count == 4)
            System.out.println("Strong: " + password);

        sc.close();
    }
}

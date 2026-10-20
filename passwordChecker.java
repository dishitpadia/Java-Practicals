import java.util.regex.Pattern;

public class passwordChecker {
    
    public int strength(String password)
    {
        int counter = 0;

        if(password.length() >= 8)
        {
            System.out.println("Longer than 8 characters");
            counter++;
        }

        if(Pattern.matches(".*[A-Z].*", password))
        {
            System.out.println("Has at least one uppercase letter");
            counter++;
        }

        if(Pattern.matches(".*[!@#$%^&*].*", password))
        {
            System.out.println("Has at least one special character");
            counter++;
        }

        if(Pattern.matches(".*[0-9].*", password))
        {
            System.out.println("Has at least one digit");
            counter++;
        }

        return counter;
    }
}

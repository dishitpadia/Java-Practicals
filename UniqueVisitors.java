import java.util.*;

public class UniqueVisitors {
    public static void main(String[] args) {
        List<String> loginNames = Arrays.asList(
            "Rahul", "Priya", "Amit", "Rahul",
            "Neha", "Priya", "Karan", "Amit"
        );

        HashSet<String> uniqueUsers = new HashSet<>(loginNames);

        LinkedHashSet<String> orderedUsers = new LinkedHashSet<>(loginNames);

        System.out.println("Total unique visitors: " + uniqueUsers.size());

        System.out.println("First three distinct visitors:");

        int count = 0;
        for (String name : orderedUsers) {
            System.out.println(name);
            count++;

            if (count == 3) {
                break;
            }
        }
    }
}
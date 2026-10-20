import java.util.Scanner;

public class DriverChat {
    
    public static void main(String[] args) {
        
        String[] messages = {"10:05 alice Hello there", "10:10 bob Good morning evryone",
            "10:15 charlie Hello alice", "10:20 david", "10:25 alice Have a nice day" };

        Scanner sc = new Scanner(System.in);

        StringBuilder report = new StringBuilder();

        System.out.print("Enter the keyword: ");
        String keyword = sc.next();

        ChatFilter filter = new ChatFilter();

        System.out.println("Matches: " + filter.searchMessages(messages, keyword, report));
        System.out.println(report);

        sc.close();
    }
}

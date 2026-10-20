import java.util.Scanner;

interface Notifier {
    void send(String message);
}

interface urgent {}

public class NotificationSender {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        Notifier email = message -> System.out.println("Email: " + message);

        Notifier sms = (Notifier & urgent) message -> System.out.println("SMS: " + message);

        System.out.print("Enter the message: ");
        String message = sc.nextLine();

        Notifier[] senders = {email, sms};

        for(Notifier sender : senders) {
            sender.send(message);

            if(sender instanceof urgent) {
                sender.send(message);
            }
        }    
        
        sc.close();
    }
}

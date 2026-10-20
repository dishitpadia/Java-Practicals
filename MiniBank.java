import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Pattern;

record BankInfo(String name, String branch) {
}

record Command(TrasactionType type, String accountNumber, long amount) {
}

class customer {

    private String name;
    private String email;
    private String mobile;
    private final String customerID;
    private static long customerCounter = 0;
    private Address address;

    customer(String name, String email, String mobile) {
        this.name = name;
        this.email = email;
        this.mobile = mobile;

        customerID = String.format("CUST%04d", customerCounter);
        customerCounter++;
    }

    class Address {
        private String line, city, pincode;

        Address(String line, String city, String pincode) {
            this.line = line;
            this.city = city;
            this.pincode = pincode;
        }

        public String getLine() {
            return line;
        }

        public String getCity() {
            return city;
        }

        public String getPincode() {
            return pincode;
        }

    }

    public String getCustomerID() {
        return customerID;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public Address getAddress() {
        return address;
    }

}

abstract class account extends customer {
    private String ownerName;
    protected long balance = 0;

    static int counter = 0;

    private final String accountNumber = String.format("AC%04d", ++counter);

    account(String ownerName, long balance, String email, String mobile) {
        super(ownerName, email, mobile);

        this.ownerName = ownerName;
        this.balance = balance;
    }

    abstract double interestRate();

    abstract boolean canWithdraw(long amount);

    @Override
    public String toString() {
        return ("Account number: " + accountNumber + "\n" +
                "Owner name: " + ownerName + "\n" + "Balance: " + balance);
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj)
            return true;

        if (obj == null || getClass() != obj.getClass())
            return false;

        account other = (account) obj;

        return accountNumber == other.accountNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }

    public void deposit(long amount) {
        if (amount > 0)
            balance += amount;

        else
            System.out.println("Invalid input for amount");
    }

    public boolean withdraw(long amount) {
        if (balance >= amount) {
            balance -= amount;
            return true;
        }

        else
            return false;
    }

    public String getAccNum() {
        return accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public long getBalance() {
        return balance;
    }
}

class SavingsAccount extends account {

    long minBalance;

    SavingsAccount(String ownerName, long balance, String email, String mobile) {
        super(ownerName, balance, email, mobile);
        minBalance = 2000;
    }

    double interestRate() {
        return 4.0;
    }

    boolean canWithdraw(long amount) {

        if ((balance = balance - amount) > minBalance) {
            return true;
        }

        else {
            return false;
        }
    }

}

class currentAccount extends account {
    long overdraftLimit;

    currentAccount(String ownerName, long balance, String email, String mobile) {
        super(ownerName, balance, email, mobile);
        overdraftLimit = 3000;
    }

    double interestRate() {
        return 0;
    }

    boolean canWithdraw(long amount) {

        if ((balance = balance - amount) >= overdraftLimit) {
            return true;
        }

        else {
            return false;
        }
    }
}

class FixedDepositAccount extends account {

    FixedDepositAccount(String ownerName, long balance, String email, String mobile) {
        super(ownerName, balance, email, mobile);
    }

    double interestRate() {
        return 7.0;
    }

    boolean canWithdraw(long amount) {
        return false;
    }
}

class validator {

    static boolean isValidMobile(String mobileNum) {

        String mobilePattern = "^[6-9]\\d{9}$";

        return Pattern.matches(mobilePattern, mobileNum);
    }

    static boolean isValidEmail(String email) {

        String emailPattern = "^[\\w-.]+@[\\w-.]+[\\w]{2,}$";

        return Pattern.matches(emailPattern, email);
    }

    static boolean isValidPan(String pan) {

        String panPattern = "[A-Z]{5}[0-9]{4}[A-Z]";

        return Pattern.matches(panPattern, pan);
    }

    static boolean isValidIfsc(String IFSC) {

        String IFSCPattern = "^[A-Z]{4}0[A-Z0-9]{6}$";

        return Pattern.matches(IFSCPattern, IFSC);
    }
}

enum TrasactionType {
    DEPOSIT,
    WITHDRAW,
    TRANSFER
}

class CommandParser {

    public static Command parse(String line) {

        String[] parts = line.split(" ");

        TrasactionType type = TrasactionType.valueOf(parts[0]);
        String accNum = parts[1];
        long amount = Long.parseLong(parts[2]);

        return new Command(type, accNum, amount);
    }
}

class StatementFormatter {
    public static String buildStatement(account acc) {
        StringBuilder statement = new StringBuilder();

        statement.append("Account statement");
        statement.append("Account number: ").append(acc.getAccNum()).append("\n");
        statement.append("Balance: ").append(acc.getBalance()).append("\n");

        return statement.toString();
    }
}

public class MiniBank {

    public enum MenuOption {
        OPEN_ACCOUNT,
        DEPOSIT,
        WITHDRAW,
        TRANSFER,
        EXIT
    }

    public static void main(String[] args) {

        account acc[] = new account[3];
        Scanner sc = new Scanner(System.in);
        String name, mobile, email, IFSC, pan;
        long balance, amount;
        int cnt = 0;

        System.out.println("Different acocunt types demo: ");

        account accounts[] = { new FixedDepositAccount("abc", 10000, "abc@gmail.com", "6799679453"),
                new SavingsAccount("abcs", 25000, "abcd@gmail.com", "6789045623"),
                new currentAccount("abcde", 15000, "abcde@gmail.com", "9756346789")
        };

        for (int i = 0; i < accounts.length; ++i) {

            System.out.println("Enter the amount to withdraw: ");
            amount = sc.nextLong();
            
            System.out.println("The interest rate for account " + i + 1 + " is: " + accounts[i].interestRate());
            System.out.println("Can withdraw? " + accounts[i].canWithdraw(amount));
        }

        while (true) {
            System.out.println("1. Open account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Print account information");
            System.out.println("6. Exit");

            System.out.print("Choose from the above options : ");

            int choice = sc.nextInt();

            if (choice == 6) {
                System.out.println("Good Bye");
                break;
            }

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter your name: ");
                    name = sc.next();

                    System.out.print("Enter the initiale balance: ");
                    balance = sc.nextInt();

                    System.out.print("Enter the mobile number: ");
                    mobile = sc.next();

                    while (!validator.isValidMobile(mobile)) {
                        System.out.println("Enter a valid mobile number: ");
                        mobile = sc.next();
                    }

                    System.out.print("Enter email: ");
                    email = sc.next();

                    while (!validator.isValidEmail(email)) {
                        System.out.println("Enter a valid emial: ");
                        email = sc.next();
                    }

                    // acc[cnt] = new account(name, balance, email, mobile);
                    cnt++;

                }

                case 2 -> {
                    System.out.print("Enter the amount to deposit: ");
                    amount = sc.nextLong();

                    acc[cnt - 1].deposit(amount);

                    System.out.println("Balance: " + acc[cnt - 1].getBalance());
                }

                case 3 -> {
                    System.out.print("Enter the amount to withdraw: ");
                    amount = sc.nextLong();

                    if (acc[cnt - 1].withdraw(amount)) {
                        System.out.println("Withdrawn successfully");
                    }

                    else
                        System.out.println("Insufficient balance");

                    System.out.println("Balance: " + acc[cnt - 1].getBalance());
                }

                case 4 -> {
                    System.out.print("Enter the IFCS number:");

                    IFSC = sc.next();

                    while (!validator.isValidIfsc(IFSC)) {
                        System.out.println("Enter valid IFSC: ");
                        IFSC = sc.next();
                    }

                    System.out.print("Enter the pan number: ");

                    pan = sc.next();

                    while (!validator.isValidPan(pan)) {
                        System.out.println("Enter valid pan: ");
                        pan = sc.next();
                    }

                    System.out.println("Transfer - To be implemented in later lab");
                }

                case 5 -> {

                    for (int i = 0; i < acc.length; ++i) {
                        StatementFormatter.buildStatement(acc[i]);
                    }

                    System.out.println("Account details");

                    for (int i = 0; i < 3; ++i) {
                        System.out.println(acc[i].toString());
                    }
                }

                default -> {
                    System.out.println("Invalid input");
                }
            }
            ;

            System.out.println(acc[cnt - 1]);
        }

        if (acc[cnt - 1].equals(acc[cnt]))
            System.out.println("Accounts are equal");

        sc.close();
    }
}

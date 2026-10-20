import exception.BankException;
import exception.BankException.InsufficientFundsException;
import exception.BankException.InvalidAmountException;
import model.Account;
import model.Account.CheckingAccount;
import model.Account.InterestBearing;
import model.Account.Premium;
import model.Account.SavingsAccount;
import model.Account.WithdrawRule;
import model.Customer;
import service.BankService;
import service.BankService.AccountStore;
import service.BankService.AccountWorker;
import service.BankService.Repository;
import service.BankService.SessionCloser;
import service.BankService.TransactionBuffer;
import service.BankService.TransactionProcessor;
import util.Utilities.AnnotationValidator;
import util.Utilities.StatePersister;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

import static util.Utilities.formatMoney;

public class MiniBank {
    public static void main(String[] args) {
        BankService bank = new BankService();
        bank.loadOnStart();
        if (args.length > 0 && "--demo".equals(args[0])) {
            runLabDemos();
            bank.saveOnExit();
            return;
        }
        try (SessionCloser session = new SessionCloser("teller-console");
             Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                printMenu();
                String choice = scanner.nextLine().trim();
                try {
                    switch (choice) {
                        case "1" -> openAccount(scanner, bank);
                        case "2" -> deposit(scanner, bank);
                        case "3" -> withdraw(scanner, bank);
                        case "4" -> transfer(scanner, bank);
                        case "5" -> statement(scanner, bank);
                        case "6" -> listAccounts(bank);
                        case "7" -> {
                            bank.processBatch();
                            System.out.println("Batch processed on the thread pool.");
                            listAccounts(bank);
                        }
                        case "8" -> System.out.println(bank.generateReport());
                        case "9" -> runLabDemos();
                        case "0" -> running = false;
                        default -> System.out.println("Unknown option.");
                    }
                } catch (BankException ex) {
                    System.out.println("Operation failed: " + ex.getMessage());
                } catch (Exception ex) {
                    System.out.println("Unexpected error: " + ex.getMessage());
                } finally {
                    System.out.println();
                }
            }
        } finally {
            bank.saveOnExit();
        }
    }

    private static void printMenu() {
        System.out.println("===== MiniBank =====");
        System.out.println("1 Open account");
        System.out.println("2 Deposit");
        System.out.println("3 Withdraw");
        System.out.println("4 Transfer");
        System.out.println("5 Statement");
        System.out.println("6 List accounts");
        System.out.println("7 Process batch");
        System.out.println("8 End-of-day report");
        System.out.println("9 Run lab demonstrations");
        System.out.println("0 Exit");
        System.out.print("Choose: ");
    }

    private static void openAccount(Scanner scanner, BankService bank) throws BankException {
        System.out.print("Account number: ");
        String number = scanner.nextLine().trim();
        System.out.print("Customer id: ");
        String customerId = scanner.nextLine().trim();
        System.out.print("Customer name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Opening balance: ");
        long balance = Long.parseLong(scanner.nextLine().trim());
        System.out.print("Savings account? (y/n): ");
        boolean savings = scanner.nextLine().trim().toLowerCase().startsWith("y");
        Account account = bank.openAccount(number, customerId, name, balance, savings);
        System.out.println("Opened " + account);
    }

    private static void deposit(Scanner scanner, BankService bank) throws BankException {
        System.out.print("Account number: ");
        String number = scanner.nextLine().trim();
        System.out.print("Amount: ");
        long amount = Long.parseLong(scanner.nextLine().trim());
        bank.deposit(number, amount);
        System.out.println("Deposited " + formatMoney(amount));
    }

    private static void withdraw(Scanner scanner, BankService bank) throws BankException {
        System.out.print("Account number: ");
        String number = scanner.nextLine().trim();
        System.out.print("Amount: ");
        long amount = Long.parseLong(scanner.nextLine().trim());
        bank.withdraw(number, amount);
        System.out.println("Withdrew " + formatMoney(amount));
    }

    private static void transfer(Scanner scanner, BankService bank) throws BankException {
        System.out.print("From account: ");
        String from = scanner.nextLine().trim();
        System.out.print("To account: ");
        String to = scanner.nextLine().trim();
        System.out.print("Amount: ");
        long amount = Long.parseLong(scanner.nextLine().trim());
        bank.transfer(from, to, amount);
        System.out.println("Transferred " + formatMoney(amount));
    }

    private static void statement(Scanner scanner, BankService bank) throws BankException {
        System.out.print("Account number: ");
        String number = scanner.nextLine().trim();
        ArrayList<String> lines = bank.statement(number);
        if (lines.isEmpty()) {
            System.out.println("No movements recorded for " + number);
            return;
        }
        lines.forEach(System.out::println);
    }

    private static void listAccounts(BankService bank) {
        ArrayList<Account> accounts = bank.listAccounts();
        if (accounts.isEmpty()) {
            System.out.println("No accounts yet.");
            return;
        }
        System.out.println("Accounts sorted by balance:");
        for (Account account : accounts) {
            System.out.println(account + " (" + formatMoney(account.getBalance()) + ")");
        }
    }

    static void runLabDemos() {
        System.out.println("--- Interfaces and withdraw rules ---");
        Account demo = new SavingsAccount("S100", new Customer("C100", "Ada"), 500, 0.05);
        WithdrawRule keepReserve = new WithdrawRule() {
            @Override
            public boolean allow(Account account, long amount) {
                return account.getBalance() - amount >= 100;
            }
        };
        WithdrawRule hasFunds = (account, amount) -> amount > 0 && amount <= account.getBalance();
        System.out.println("Anonymous allow 450: " + keepReserve.allow(demo, 450));
        System.out.println("Lambda allow 450: " + hasFunds.allow(demo, 450));
        System.out.println("Premium? " + (demo instanceof Premium));
        System.out.println("Yearly interest: " + ((InterestBearing) demo).yearlyInterest());

        System.out.println("--- Annotation validation ---");
        Account invalid = new CheckingAccount("X1", new Customer("C0", "TooLongNameThatExceedsTheFortyCharacterLimit"), -50);
        for (String error : AnnotationValidator.validate(invalid)) {
            System.out.println("Account error: " + error);
        }
        for (String error : AnnotationValidator.validate(invalid.getCustomer())) {
            System.out.println("Customer error: " + error);
        }

        System.out.println("--- Exceptions ---");
        try {
            demo.withdraw(-1);
        } catch (InvalidAmountException ex) {
            System.out.println("Friendly: " + ex.getMessage());
        } catch (BankException ex) {
            System.out.println("Friendly: " + ex.getMessage());
        } finally {
            System.out.println("Withdraw attempt finished.");
        }
        try {
            demo.withdraw(10_000);
        } catch (InsufficientFundsException ex) {
            System.out.println("Friendly: shortfall " + ex.getShortfall());
        } catch (BankException ex) {
            System.out.println("Friendly: " + ex.getMessage());
        }

        demonstrateRace();
        demonstratePoolAndProducerConsumer();
        demonstrateDeadlockAndFix();
        demonstratePersistenceAndStore();
    }

    private static void demonstrateRace() {
        System.out.println("--- Race condition then synchronized deposits ---");
        Account unsafe = new Account("RACE1", new Customer("CR", "Race"), 0);
        runWorkers(unsafe, false);
        System.out.println("Unsafe balance (often wrong): " + unsafe.getBalance() + " expected 10000");

        Account safe = new Account("RACE2", new Customer("CS", "Safe"), 0);
        runWorkers(safe, true);
        System.out.println("Safe synchronized balance: " + safe.getBalance() + " expected 10000");
    }

    private static void runWorkers(Account account, boolean safe) {
        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new AccountWorker(account, 1000, 1, safe, true), "Worker-" + i);
            threads[i].start();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static void demonstratePoolAndProducerConsumer() {
        System.out.println("--- Transaction processor ---");
        Account pooled = new Account("P1", new Customer("CP", "Pool"), 100);
        TransactionProcessor processor = new TransactionProcessor();
        processor.register(pooled);
        processor.submit(() -> {
            try {
                pooled.deposit(25);
            } catch (InvalidAmountException ignored) {
            }
        });
        processor.submit(() -> {
            try {
                pooled.withdraw(10);
            } catch (BankException ignored) {
            }
        });
        processor.stop();
        System.out.println("After pool tasks: " + pooled);

        System.out.println("--- Producer / consumer ---");
        TransactionBuffer buffer = new TransactionBuffer(4);
        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    String tx = "TX-" + i;
                    buffer.put(tx);
                    System.out.println(Thread.currentThread().getName() + " produced " + tx);
                }
                buffer.put("DONE");
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }, "Producer");
        Thread consumer = new Thread(() -> {
            try {
                while (true) {
                    String tx = buffer.take();
                    System.out.println(Thread.currentThread().getName() + " consumed " + tx);
                    if ("DONE".equals(tx)) {
                        break;
                    }
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }, "Consumer");
        producer.start();
        consumer.start();
        try {
            producer.join();
            consumer.join();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    private static void demonstrateDeadlockAndFix() {
        System.out.println("--- Deadlock (opposite lock order) ---");
        Account a = new Account("A100", new Customer("CA", "Ann"), 1_000);
        Account b = new Account("B100", new Customer("CB", "Ben"), 1_000);
        Thread t1 = new Thread(() -> {
            try {
                Account.transferDeadlockProne(a, b, 10);
            } catch (BankException ignored) {
            }
        }, "Deadlock-A-to-B");
        Thread t2 = new Thread(() -> {
            try {
                Account.transferDeadlockProne(b, a, 10);
            } catch (BankException ignored) {
            }
        }, "Deadlock-B-to-A");
        t1.setDaemon(true);
        t2.setDaemon(true);
        t1.start();
        t2.start();
        try {
            t1.join(400);
            t2.join(400);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        if (t1.isAlive() || t2.isAlive()) {
            System.out.println("Threads still blocked: deadlock observed.");
        } else {
            System.out.println("No deadlock this run.");
        }

        System.out.println("--- Fixed transfer (lock smaller accountNumber first) ---");
        Account c = new Account("C100", new Customer("CC", "Cara"), 1_000);
        Account d = new Account("D100", new Customer("CD", "Dan"), 1_000);
        Thread u1 = new Thread(() -> {
            try {
                c.transfer(d, 15);
            } catch (BankException ignored) {
            }
        }, "Safe-C-to-D");
        Thread u2 = new Thread(() -> {
            try {
                d.transfer(c, 20);
            } catch (BankException ignored) {
            }
        }, "Safe-D-to-C");
        u1.start();
        u2.start();
        try {
            u1.join();
            u2.join();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        System.out.println(c);
        System.out.println(d);
    }

    private static void demonstratePersistenceAndStore() {
        System.out.println("--- Serializable save/load ---");
        try {
            Path file = Path.of("data", "demo-accounts.ser");
            Account[] original = {
                    new SavingsAccount("S200", new Customer("C200", "Sam"), 300, 0.04),
                    new CheckingAccount("C200", new Customer("C201", "Kim"), 150)
            };
            StatePersister.save(original, file);
            Account[] reloaded = StatePersister.load(file);
            System.out.println("Reloaded accounts:");
            for (Account account : reloaded) {
                System.out.println("  " + account);
            }
            Files.deleteIfExists(file);
        } catch (Exception ex) {
            System.out.println("Persistence demo failed: " + ex.getMessage());
        }

        System.out.println("--- AccountStore sorted by balance ---");
        AccountStore store = new AccountStore();
        store.add(new Account("M1", new Customer("CM1", "Mia"), 80));
        store.add(new Account("M2", new Customer("CM2", "Ned"), 400));
        store.add(new Account("M3", new Customer("CM3", "Ora"), 120));
        for (Account account : store.sortedByBalance()) {
            System.out.println(account);
        }

        System.out.println("--- Generic repository ---");
        Repository<Account> repository = new Repository<>();
        repository.saveAll(
                new Account("R1", new Customer("CR1", "Pat"), 10),
                new Account("R2", new Customer("CR2", "Quin"), 20),
                new Account("R3", new Customer("CR3", "Ray"), 30)
        );
        System.out.println("findById R2: " + repository.findById("R2"));
        System.out.println("findAll: " + repository.findAll());
        System.out.println("average: " + Repository.average(new Long[] {10L, 20L, 30L}));
    }
}

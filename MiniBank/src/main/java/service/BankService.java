package service;

import exception.BankException;
import exception.BankException.AccountNotFoundException;
import exception.BankException.InvalidAmountException;
import model.Account;
import model.Account.CheckingAccount;
import model.Account.SavingsAccount;
import model.Customer;
import model.annotation.Id;
import util.Utilities.AnnotationValidator;
import util.Utilities.AuditLogger;
import util.Utilities.ReportGenerator;
import util.Utilities.StatePersister;
import util.Utilities.TransactionLog;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BankService {
    private final Repository<Account> repository = new Repository<>();
    private final ArrayList<String> history = new ArrayList<>();
    private final Path dataFile;
    private final Path logFile;
    private final Path logsFolder;
    private final Path reportFile;

    public BankService() {
        this(Path.of("data", "accounts.ser"), Path.of("data", "logs"), Path.of("data", "reports", "eod-summary.txt"));
    }

    public BankService(Path dataFile, Path logsFolder, Path reportFile) {
        this.dataFile = dataFile;
        this.logsFolder = logsFolder;
        this.logFile = logsFolder.resolve("transactions.log");
        this.reportFile = reportFile;
    }

    @Audit
    public Account openAccount(String accountNumber, String customerId, String customerName, long openingBalance, boolean savings) throws BankException {
        AuditLogger.record(this, "openAccount");
        if (repository.findById(accountNumber) != null) {
            throw new InvalidAmountException("Account already exists: " + accountNumber);
        }
        Customer customer = new Customer(customerId, customerName);
        Account account = savings
                ? new SavingsAccount(accountNumber, customer, openingBalance, 0.04)
                : new CheckingAccount(accountNumber, customer, openingBalance);
        String[] errors = AnnotationValidator.validate(account);
        if (errors.length > 0 || AnnotationValidator.validate(customer).length > 0) {
            String[] nameErrors = AnnotationValidator.validate(customer);
            String message = errors.length > 0 ? errors[0] : nameErrors[0];
            throw new InvalidAmountException(message);
        }
        repository.save(account);
        record("OPEN", accountNumber, openingBalance);
        return account;
    }

    @Audit
    public void deposit(String accountNumber, long amount) throws BankException {
        AuditLogger.record(this, "deposit");
        Account account = require(accountNumber);
        account.deposit(amount);
        record("DEPOSIT", accountNumber, amount);
    }

    @Audit
    public void withdraw(String accountNumber, long amount) throws BankException {
        AuditLogger.record(this, "withdraw");
        Account account = require(accountNumber);
        account.withdraw(amount);
        record("WITHDRAW", accountNumber, amount);
    }

    @Audit
    public void transfer(String fromNumber, String toNumber, long amount) throws BankException {
        AuditLogger.record(this, "transfer");
        Account from = require(fromNumber);
        Account to = require(toNumber);
        from.transfer(to, amount);
        record("TRANSFER-OUT", fromNumber, amount);
        record("TRANSFER-IN", toNumber, amount);
    }

    @Audit
    public ArrayList<String> statement(String accountNumber) throws AccountNotFoundException {
        AuditLogger.record(this, "statement");
        require(accountNumber);
        ArrayList<String> lines = new ArrayList<>();
        for (String event : history) {
            if (event.contains(accountNumber)) {
                lines.add(event);
            }
        }
        return lines;
    }

    @Audit
    public ArrayList<Account> listAccounts() {
        AuditLogger.record(this, "listAccounts");
        ArrayList<Account> accounts = repository.findAll();
        accounts.sort((left, right) -> Long.compare(left.getBalance(), right.getBalance()));
        return accounts;
    }

    public Repository<Account> getRepository() {
        return repository;
    }

    public void loadOnStart() {
        if (!Files.exists(dataFile)) {
            return;
        }
        try {
            Account[] loaded = StatePersister.load(dataFile);
            repository.saveAll(loaded);
            System.out.println("Loaded " + loaded.length + " accounts from " + dataFile);
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("Could not load saved accounts: " + ex.getMessage());
        }
    }

    public void saveOnExit() {
        try {
            ArrayList<Account> accounts = repository.findAll();
            StatePersister.save(accounts.toArray(Account[]::new), dataFile);
            System.out.println("Saved " + accounts.size() + " accounts to " + dataFile);
        } catch (IOException ex) {
            System.out.println("Could not save accounts: " + ex.getMessage());
        }
    }

    public void processBatch() throws BankException {
        TransactionProcessor processor = new TransactionProcessor();
        for (Account account : repository.findAll()) {
            processor.register(account);
        }
        if (processor.getAccounts().isEmpty()) {
            throw new AccountNotFoundException("no accounts for batch");
        }
        for (Account account : processor.getAccounts().values()) {
            processor.submit(() -> {
                try {
                    account.deposit(10);
                    record("DEPOSIT", account.getAccountNumber(), 10);
                } catch (BankException ex) {
                    System.out.println("Batch deposit failed: " + ex.getMessage());
                }
            });
        }
        processor.stop();
    }

    public String generateReport() throws IOException {
        return ReportGenerator.generate(logsFolder, reportFile);
    }

    private Account require(String accountNumber) throws AccountNotFoundException {
        Account account = repository.findById(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException(accountNumber);
        }
        return account;
    }

    private void record(String type, String accountNumber, long amount) {
        String line = type + " " + accountNumber + " " + amount;
        history.add(line);
        try {
            TransactionLog.append(logFile, line);
        } catch (IOException ex) {
            System.out.println("Could not write transaction log: " + ex.getMessage());
        }
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Audit {
    }

    public static class Repository<T> {
        private final HashMap<String, T> store = new HashMap<>();

        public T save(T entity) {
            String id = readId(entity);
            store.put(id, entity);
            return entity;
        }

        public T findById(String id) {
            return store.get(id);
        }

        public ArrayList<T> findAll() {
            return new ArrayList<>(store.values());
        }

        public boolean delete(String id) {
            return store.remove(id) != null;
        }

        @SafeVarargs
        public final void saveAll(T... items) {
            if (items == null) {
                return;
            }
            for (T item : items) {
                save(item);
            }
        }

        public static <N extends Number> double average(N[] values) {
            if (values == null || values.length == 0) {
                return 0.0;
            }
            double sum = 0;
            for (N value : values) {
                sum += value.doubleValue();
            }
            return sum / values.length;
        }

        private String readId(T entity) {
            if (entity == null) {
                throw new IllegalArgumentException("entity is required");
            }
            Class<?> type = entity.getClass();
            while (type != null && type != Object.class) {
                for (Field field : type.getDeclaredFields()) {
                    if (field.isAnnotationPresent(Id.class)) {
                        field.setAccessible(true);
                        try {
                            Object value = field.get(entity);
                            if (value == null) {
                                throw new IllegalArgumentException("@Id field is null");
                            }
                            return value.toString();
                        } catch (IllegalAccessException ex) {
                            throw new IllegalStateException("Cannot read @Id field", ex);
                        }
                    }
                }
                type = type.getSuperclass();
            }
            throw new IllegalArgumentException("No @Id field on " + entity.getClass().getName());
        }
    }

    public static class AccountStore {
        private final HashMap<String, Account> accounts = new HashMap<>();
        private final ArrayList<String> history = new ArrayList<>();

        public void add(Account account) {
            accounts.put(account.getAccountNumber(), account);
            history.add("OPEN " + account.getAccountNumber());
        }

        public Account get(String accountNumber) {
            return accounts.get(accountNumber);
        }

        public ArrayList<Account> getAll() {
            return new ArrayList<>(accounts.values());
        }

        public void record(String event) {
            history.add(event);
        }

        public ArrayList<String> getHistory() {
            return new ArrayList<>(history);
        }

        public ArrayList<Account> sortedByBalance() {
            ArrayList<Account> list = getAll();
            list.sort(Comparator.comparingLong(Account::getBalance).thenComparing(Account::getAccountNumber));
            return list;
        }
    }

    public static class AccountWorker implements Runnable {
        private final Account account;
        private final int times;
        private final long amount;
        private final boolean safe;
        private final boolean logLifecycle;

        public AccountWorker(Account account, int times, long amount) {
            this(account, times, amount, true, false);
        }

        public AccountWorker(Account account, int times, long amount, boolean safe) {
            this(account, times, amount, safe, false);
        }

        public AccountWorker(Account account, int times, long amount, boolean safe, boolean logLifecycle) {
            this.account = account;
            this.times = times;
            this.amount = amount;
            this.safe = safe;
            this.logLifecycle = logLifecycle;
        }

        @Override
        public void run() {
            String name = Thread.currentThread().getName();
            if (logLifecycle) {
                System.out.println(name + " started");
            }
            for (int i = 0; i < times; i++) {
                try {
                    if (safe) {
                        account.deposit(amount);
                    } else {
                        account.depositUnsafe(amount);
                    }
                } catch (InvalidAmountException ex) {
                    System.out.println(name + " failed: " + ex.getMessage());
                }
            }
            if (logLifecycle) {
                System.out.println(name + " finished");
            }
        }
    }

    public static class TransactionProcessor {
        private final ExecutorService pool = Executors.newFixedThreadPool(4);
        private final ConcurrentHashMap<String, Account> accounts = new ConcurrentHashMap<>();

        public void register(Account account) {
            accounts.put(account.getAccountNumber(), account);
        }

        public ConcurrentHashMap<String, Account> getAccounts() {
            return accounts;
        }

        public void submit(Runnable task) {
            pool.execute(task);
        }

        public void stop() {
            pool.shutdown();
            try {
                if (!pool.awaitTermination(30, TimeUnit.SECONDS)) {
                    pool.shutdownNow();
                }
            } catch (InterruptedException ex) {
                pool.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    public static class TransactionBuffer {
        private final String[] buffer;
        private int count;
        private int putIndex;
        private int takeIndex;

        public TransactionBuffer(int capacity) {
            this.buffer = new String[capacity];
        }

        public synchronized void put(String transaction) throws InterruptedException {
            while (count == buffer.length) {
                wait();
            }
            buffer[putIndex] = transaction;
            putIndex = (putIndex + 1) % buffer.length;
            count++;
            notifyAll();
        }

        public synchronized String take() throws InterruptedException {
            while (count == 0) {
                wait();
            }
            String transaction = buffer[takeIndex];
            takeIndex = (takeIndex + 1) % buffer.length;
            count--;
            notifyAll();
            return transaction;
        }
    }

    public static class SessionCloser implements AutoCloseable {
        private final String name;

        public SessionCloser(String name) {
            this.name = name;
            System.out.println("Opening session: " + name);
        }

        @Override
        public void close() {
            System.out.println("Closing session: " + name);
        }
    }
}

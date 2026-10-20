package model;

import exception.BankException;
import exception.BankException.AccountNotFoundException;
import exception.BankException.InsufficientFundsException;
import exception.BankException.InvalidAmountException;
import model.annotation.Id;
import model.annotation.Positive;

import java.io.Serializable;
import java.util.Objects;

public class Account implements Transactable, Comparable<Account>, Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private final String accountNumber;

    private final Customer customer;

    @Positive(message = "must be > 0")
    private long balance;

    public Account(String accountNumber, Customer customer, long balance) {
        this.accountNumber = accountNumber;
        this.customer = customer;
        this.balance = balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public Customer getCustomer() {
        return customer;
    }

    public long getBalance() {
        return balance;
    }

    @Override
    public synchronized void deposit(long amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be greater than 0");
        }
        balance += amount;
    }

    public void depositUnsafe(long amount) {
        long current = balance;
        Thread.yield();
        balance = current + amount;
    }

    @Override
    public synchronized boolean withdraw(long amount) throws InsufficientFundsException, InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be greater than 0");
        }
        if (amount > balance) {
            throw new InsufficientFundsException(amount - balance);
        }
        balance -= amount;
        return true;
    }

    public void transfer(Account to, long amount) throws BankException {
        if (to == null) {
            throw new AccountNotFoundException("destination");
        }
        Account first = accountNumber.compareTo(to.accountNumber) <= 0 ? this : to;
        Account second = first == this ? to : this;
        try {
            synchronized (first) {
                synchronized (second) {
                    withdraw(amount);
                    to.deposit(amount);
                }
            }
        } catch (BankException ex) {
            throw ex;
        } finally {
            // Always reached: caller can log or release extra resources here.
        }
    }

    public static void transferDeadlockProne(Account from, Account to, long amount) throws BankException {
        synchronized (from) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            synchronized (to) {
                from.withdraw(amount);
                to.deposit(amount);
            }
        }
    }

    @Override
    public int compareTo(Account other) {
        return accountNumber.compareTo(other.accountNumber);
    }

    @Override
    public String toString() {
        return "Account[" + accountNumber + ", " + customer + ", balance=" + balance + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Account other)) {
            return false;
        }
        return Objects.equals(accountNumber, other.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }

    public interface InterestBearing {
        double interestRate();

        long getBalance();

        default double yearlyInterest() {
            return getBalance() * interestRate();
        }
    }

    @FunctionalInterface
    public interface WithdrawRule {
        boolean allow(Account account, long amount);
    }

    public interface Premium {
    }

    public static class SavingsAccount extends Account implements InterestBearing, Premium {
        private static final long serialVersionUID = 1L;
        private final double rate;

        public SavingsAccount(String accountNumber, Customer customer, long balance, double rate) {
            super(accountNumber, customer, balance);
            this.rate = rate;
        }

        @Override
        public double interestRate() {
            return rate;
        }

        @Override
        public String toString() {
            return "Savings" + super.toString() + " rate=" + rate;
        }
    }

    public static class CheckingAccount extends Account {
        private static final long serialVersionUID = 1L;

        public CheckingAccount(String accountNumber, Customer customer, long balance) {
            super(accountNumber, customer, balance);
        }

        @Override
        public String toString() {
            return "Checking" + super.toString();
        }
    }
}

interface Transactable {
    void deposit(long amount) throws InvalidAmountException;

    boolean withdraw(long amount) throws InsufficientFundsException, InvalidAmountException;
}

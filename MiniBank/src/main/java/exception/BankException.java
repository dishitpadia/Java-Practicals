package exception;

public class BankException extends Exception {
    private static final long serialVersionUID = 1L;

    public BankException(String message) {
        super(message);
    }

    public BankException(String message, Throwable cause) {
        super(message, cause);
    }

    public static class InsufficientFundsException extends BankException {
        private static final long serialVersionUID = 1L;
        private final long shortfall;

        public InsufficientFundsException(long shortfall) {
            super("Insufficient funds; shortfall: " + shortfall);
            this.shortfall = shortfall;
        }

        public long getShortfall() {
            return shortfall;
        }
    }

    public static class AccountNotFoundException extends BankException {
        private static final long serialVersionUID = 1L;

        public AccountNotFoundException(String accountNumber) {
            super("Account not found: " + accountNumber);
        }
    }

    public static class InvalidAmountException extends BankException {
        private static final long serialVersionUID = 1L;

        public InvalidAmountException(String message) {
            super(message);
        }
    }
}

import exception.BankException.InsufficientFundsException;
import exception.BankException.InvalidAmountException;
import model.Account;
import model.Customer;
import org.junit.jupiter.api.Test;
import service.BankService;
import service.BankService.AccountWorker;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BankServiceTest {

    @Test
    void depositIncreasesBalance() throws Exception {
        BankService bank = newService();
        bank.openAccount("T100", "C1", "Test User", 100, false);
        bank.deposit("T100", 40);
        Account account = bank.getRepository().findById("T100");
        assertEquals(140, account.getBalance());
    }

    @Test
    void withdrawalOverLimitThrows() throws Exception {
        BankService bank = newService();
        bank.openAccount("T200", "C2", "Test User", 50, true);
        InsufficientFundsException ex = assertThrows(
                InsufficientFundsException.class,
                () -> bank.withdraw("T200", 80)
        );
        assertEquals(30, ex.getShortfall());
    }

    @Test
    void nonPositiveDepositThrows() throws Exception {
        BankService bank = newService();
        bank.openAccount("T300", "C3", "Test User", 10, false);
        assertThrows(InvalidAmountException.class, () -> bank.deposit("T300", 0));
    }

    @Test
    void concurrentDepositsKeepBalanceCorrect() throws Exception {
        Account account = new Account("T400", new Customer("C4", "Concurrent"), 0);
        int threads = 10;
        int times = 1000;
        Thread[] workers = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            workers[i] = new Thread(new AccountWorker(account, times, 1, true), "TestWorker-" + i);
            workers[i].start();
        }
        for (Thread worker : workers) {
            worker.join();
        }
        assertEquals(threads * times, account.getBalance());
        assertTrue(account.getBalance() > 0);
    }

    private static BankService newService() {
        String stamp = String.valueOf(System.nanoTime());
        return new BankService(
                Path.of("target", "test-data", stamp, "accounts.ser"),
                Path.of("target", "test-data", stamp, "logs"),
                Path.of("target", "test-data", stamp, "report.txt")
        );
    }
}

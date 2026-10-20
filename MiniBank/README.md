# MiniBank

Console banking lab covering classes, interfaces, annotations, exceptions, threads, NIO, collections and a generic repository. The teller menu is wired to `BankService`; accounts are saved on exit and reloaded on start.

## Layout

```
MiniBank/
├── pom.xml
├── README.md
└── src/
    ├── main/java/
    │   ├── MiniBank.java
    │   ├── model/
    │   │   ├── Account.java          (interfaces + Savings/Checking)
    │   │   ├── Customer.java
    │   │   └── annotation/
    │   │       ├── Id.java
    │   │       ├── Positive.java
    │   │       └── MaxLength.java
    │   ├── util/
    │   │   └── Utilities.java        (validator, persistence, log, report)
    │   ├── exception/
    │   │   └── BankException.java    (all bank exceptions)
    │   └── service/
    │       └── BankService.java      (repository, store, threads, processor)
    └── test/java/
        └── BankServiceTest.java
```

## Build the runnable JAR

```
mvn -q test package
```

The manifest `Main-Class` is `MiniBank`. The JAR is `target/minibank-1.0.0.jar`.

## Run

Interactive menu:

```
java -jar target/minibank-1.0.0.jar
```

Lab demonstrations (interfaces, validation, race condition, pool, producer/consumer, deadlock fix, persistence, store, repository):

```
java -jar target/minibank-1.0.0.jar --demo
```

## Menu

1. Open account
2. Deposit
3. Withdraw
4. Transfer
5. Statement
6. List accounts (sorted by balance)
7. Process a batch on a 4-thread pool
8. End-of-day report from `data/logs`
0. Exit (saves `data/accounts.ser`)

Option 9 runs the same demonstrations as `--demo`.

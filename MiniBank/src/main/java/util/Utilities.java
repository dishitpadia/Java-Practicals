package util;

import model.annotation.MaxLength;
import model.annotation.Positive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Locale;

import model.Account;
import service.BankService;

public final class Utilities {
    private Utilities() {
    }

    public static String formatMoney(long amount) {
        return String.format(Locale.US, "%,d", amount);
    }

    public static long parseAmount(String text) {
        return Long.parseLong(text.trim());
    }

    public static final class AnnotationValidator {
        private AnnotationValidator() {
        }

        public static String[] validate(Object obj) {
            ArrayList<String> errors = new ArrayList<>();
            if (obj == null) {
                return new String[] {"object is null"};
            }
            Class<?> type = obj.getClass();
            while (type != null && type != Object.class) {
                for (Field field : type.getDeclaredFields()) {
                    field.setAccessible(true);
                    Object value;
                    try {
                        value = field.get(obj);
                    } catch (IllegalAccessException ex) {
                        errors.add(field.getName() + " cannot be read");
                        continue;
                    }
                    if (field.isAnnotationPresent(Positive.class)) {
                        Positive rule = field.getAnnotation(Positive.class);
                        if (value instanceof Number number && number.longValue() <= 0) {
                            errors.add(field.getName() + " " + rule.message());
                        }
                    }
                    if (field.isAnnotationPresent(MaxLength.class)) {
                        MaxLength rule = field.getAnnotation(MaxLength.class);
                        if (value != null && value.toString().length() > rule.value()) {
                            errors.add(field.getName() + " length must be <= " + rule.value());
                        }
                    }
                }
                type = type.getSuperclass();
            }
            return errors.toArray(new String[0]);
        }
    }

    public static final class AuditLogger {
        private AuditLogger() {
        }

        public static void record(Object service, String methodName) {
            if (service == null || methodName == null) {
                return;
            }
            for (Method method : service.getClass().getDeclaredMethods()) {
                if (method.getName().equals(methodName) && method.isAnnotationPresent(BankService.Audit.class)) {
                    System.out.println("[AUDIT] " + service.getClass().getSimpleName() + "." + methodName);
                    return;
                }
            }
        }
    }

    public static final class StatePersister {
        private StatePersister() {
        }

        public static void save(Account[] accounts, Path file) throws IOException {
            Path parent = file.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(file))) {
                out.writeObject(accounts);
            }
        }

        public static Account[] load(Path file) throws IOException, ClassNotFoundException {
            try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(file))) {
                return (Account[]) in.readObject();
            }
        }
    }

    public static final class TransactionLog {
        private TransactionLog() {
        }

        public static void append(Path file, String line) throws IOException {
            Path parent = file.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            String stamped = LocalDateTime.now() + " " + line + System.lineSeparator();
            Files.writeString(
                    file,
                    stamped,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        }
    }

    public static final class ReportGenerator {
        private ReportGenerator() {
        }

        public static String generate(Path logsFolder, Path reportFile) throws IOException {
            Files.createDirectories(logsFolder);
            Path parent = reportFile.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            long totalDeposits = 0;
            long totalWithdrawals = 0;
            StringBuilder details = new StringBuilder();

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(logsFolder)) {
                for (Path log : stream) {
                    if (!Files.isRegularFile(log)) {
                        continue;
                    }
                    BasicFileAttributes attrs = Files.readAttributes(log, BasicFileAttributes.class);
                    details.append("File: ").append(log.getFileName())
                            .append(" size=").append(attrs.size())
                            .append(" lastModified=").append(Instant.ofEpochMilli(attrs.lastModifiedTime().toMillis()))
                            .append(System.lineSeparator());
                    try (BufferedReader reader = Files.newBufferedReader(log, StandardCharsets.UTF_8)) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            String upper = line.toUpperCase();
                            if (upper.contains("DEPOSIT")) {
                                totalDeposits += extractAmount(line);
                            } else if (upper.contains("WITHDRAW")) {
                                totalWithdrawals += extractAmount(line);
                            }
                        }
                    }
                }
            }

            String summary = "End-of-day report"
                    + System.lineSeparator()
                    + "Total deposits: " + totalDeposits
                    + System.lineSeparator()
                    + "Total withdrawals: " + totalWithdrawals
                    + System.lineSeparator()
                    + "Net: " + (totalDeposits - totalWithdrawals)
                    + System.lineSeparator()
                    + details;
            Files.writeString(reportFile, summary, StandardCharsets.UTF_8);
            return summary;
        }

        private static long extractAmount(String line) {
            String[] parts = line.trim().split("[, ]+");
            for (int i = parts.length - 1; i >= 0; i--) {
                try {
                    return Long.parseLong(parts[i]);
                } catch (NumberFormatException ignored) {
                    // keep scanning backwards for the amount token
                }
            }
            return 0;
        }
    }
}

import java.util.Scanner;
import java.util.InputMismatchException;

class DivideByZeroException extends Exception {
    DivideByZeroException(String message) {
        super(message);
    }
}

class Calculator {

    public static double calculate(double num1, double num2, char operator)
            throws DivideByZeroException, IllegalArgumentException {

        switch (operator) {
            case '+':
                return num1 + num2;

            case '-':
                return num1 - num2;

            case '*':
                return num1 * num2;

            case '/':
                if (num2 == 0) {
                    throw new DivideByZeroException("Cannot divide by zero");
                }
                return num1 / num2;

            default:
                throw new IllegalArgumentException("Invalid operator: " + operator);
        }
    }
}

public class GuardedCalculator {

    void main() {

        Scanner sc = new Scanner(System.in);
        boolean success = false;

        while (!success) {

            try {
                IO.print("Enter the first number: ");
                double num1 = sc.nextDouble();
                sc.nextLine();

                IO.print("Enter the operator (+, -, *, /): ");
                String input = sc.nextLine();

                if (input.isEmpty()) {
                    throw new IllegalArgumentException("Operator cannot be empty");
                }

                char operator = input.charAt(0);

                IO.print("Enter the second number: ");
                double num2 = sc.nextDouble();

                double result = Calculator.calculate(num1, num2, operator);

                IO.println("Result: " + result);

                success = true;
            }

            catch (InputMismatchException e) {
                IO.println("Error: Please enter a valid number.");
                sc.nextLine();
            }

            catch (DivideByZeroException e) {
                IO.println("Error: " + e.getMessage());
            }

            catch (IllegalArgumentException e) {
                IO.println("Error: " + e.getMessage());
            }

            finally {
                IO.println("Calculation attempt completed.\n");
            }
        }

        sc.close();
    }
}
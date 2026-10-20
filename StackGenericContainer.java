class Stack<T> {
    private Object[] elements;
    private int top;

    public Stack(int capacity) {
        elements = new Object[capacity];
        top = -1;
    }

    public void push(T value) {
        if (top == elements.length - 1) {
            throw new IllegalStateException("Stack is full");
        }

        elements[++top] = value;
    }

    @SuppressWarnings("unchecked")
    public T pop() {
        if (isEmpty()) {
            throw new IllegalStateException("Stack is empty");
        }

        T value = (T) elements[top];
        elements[top] = null;
        top--;

        return value;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Stack is empty");
        }

        return (T) elements[top];
    }

    public boolean isEmpty() {
        return top == -1;
    }

    public int size() {
        return top + 1;
    }
}

public class StackGenericContainer {
    public static void main(String[] args) {

        Stack<Integer> numbers = new Stack<>(5);

        numbers.push(10);
        numbers.push(20);
        numbers.push(30);

        System.out.println("Integer Stack:");
        System.out.println("Top element: " + numbers.peek());
        System.out.println("Removed element: " + numbers.pop());
        System.out.println("Stack size: " + numbers.size());

        Stack<String> names = new Stack<>(5);

        names.push("Rahul");
        names.push("Priya");
        names.push("Amit");

        System.out.println("\nString Stack:");
        System.out.println("Top element: " + names.peek());
        System.out.println("Removed element: " + names.pop());
        System.out.println("Stack size: " + names.size());

        Stack<Integer> emptyStack = new Stack<>(3);

        System.out.println("\nEmpty Stack:");
        System.out.println("Is empty: " + emptyStack.isEmpty());

        try {
            emptyStack.pop();
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}

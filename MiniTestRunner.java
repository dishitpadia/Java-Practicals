import java.lang.annotation.*;
import java.lang.reflect.Method;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface Run {
}

class MyTests {

    @Run
    public void testLogin() {
        System.out.println("Login test executed");
    }

    @Run
    public void testSignup() {
        System.out.println("Signup test executed");
    }

    public void normalMethod() {
        System.out.println("This method should NOT run");
    }

    @Run
    public void testLogout() {
        System.out.println("Logout test executed");
    }
}

public class MiniTestRunner {

    public static void main(String[] args) throws Exception {

        MyTests testObject = new MyTests();

        Method[] methods = MyTests.class.getDeclaredMethods();

        int count = 0;

        for (Method method : methods) {

            if (method.isAnnotationPresent(Run.class)) {

                method.invoke(testObject);

                count++;
            }
        }

        System.out.println("Total @Run methods executed: " + count);
    }
}
import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface NotBlank {
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface MaxLength {
    int value();
}

class SignupForm {

    @NotBlank
    @MaxLength(30)
    private String username;

    @NotBlank
    @MaxLength(50)
    private String email;

    @NotBlank
    @MaxLength(15)
    private String password;

    SignupForm(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}

class FormValidator {

    public static List<String> validate(Object obj) {
        List<String> errors = new ArrayList<>();

        Class<?> cls = obj.getClass();

        for (Field field : cls.getDeclaredFields()) {
            field.setAccessible(true);

            try {
                Object value = field.get(obj);

                if (field.isAnnotationPresent(NotBlank.class)) {
                    if (value == null ||
                        value.toString().trim().isEmpty()) {

                        errors.add(field.getName() + " must not be blank");
                    }
                }

                if (field.isAnnotationPresent(MaxLength.class)) {
                    MaxLength annotation =
                            field.getAnnotation(MaxLength.class);

                    if (value != null &&
                        value.toString().length() > annotation.value()) {

                        errors.add(field.getName() +
                                " must not exceed " +
                                annotation.value() + " characters");
                    }
                }

            } catch (IllegalAccessException e) {
                errors.add("Unable to validate field: " + field.getName());
            }
        }

        return errors;
    }
}

public class ValidateForm {
    public static void main(String[] args) {

        SignupForm form = new SignupForm(
                "",
                "dishit@example.com",
                "thispasswordiswaytoolong"
        );

        List<String> errors = FormValidator.validate(form);

        if (errors.isEmpty()) {
            System.out.println("Form is valid!");
        } else {
            System.out.println("Validation Errors:");

            for (String error : errors) {
                System.out.println(error);
            }
        }
    }
}
import java.lang.annotation.*;
import java.lang.reflect.Field;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface Column {
    String name();
}

class Student {

    @Column(name = "student_name")
    String name;

    @Column(name = "student_age")
    int age;

    @Column(name = "student_email")
    String email;

    public void display() {
        System.out.println("Name  : " + name);
        System.out.println("Age   : " + age);
        System.out.println("Email : " + email);
    }
}

class RowMapper {

    public static <T> T mapRow(
            String[] headers,
            String[] data,
            Class<T> clazz) throws Exception {

        T object = clazz.getDeclaredConstructor().newInstance();

        for (Field field : clazz.getDeclaredFields()) {

            if (field.isAnnotationPresent(Column.class)) {

                Column column = field.getAnnotation(Column.class);

                String columnName = column.name();

                int index = findColumn(headers, columnName);

                if (index == -1) {
                    throw new Exception(
                        "Missing column: " + columnName
                    );
                }

                String value = data[index];

                field.setAccessible(true);

                if (field.getType() == String.class) {

                    field.set(object, value);

                } else if (field.getType() == int.class) {

                    field.setInt(object, Integer.parseInt(value));
                }
            }
        }

        return object;
    }

    private static int findColumn(
            String[] headers,
            String columnName) {

        for (int i = 0; i < headers.length; i++) {

            if (headers[i].equals(columnName)) {
                return i;
            }
        }

        return -1;
    }
}

public class ColumnMatching {
    public static void main(String[] args) {

        String[] headers = {
            "student_name",
            "student_age",
            "student_email"
        };

        String[] data = {
            "Dishit",
            "20",
            "dishit@gmail.com"
        };

        try {

            Student student =
                RowMapper.mapRow(
                    headers,
                    data,
                    Student.class
                );

            student.display();

        } catch (Exception e) {

            System.out.println("Error: " + e.getMessage());
        }
    }
}
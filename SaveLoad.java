import java.io.*;

class student implements Serializable {
    int id;
    String name;
    transient String password;

    student(int id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }
}

public class SaveLoad {
    public static void main(String[] args) {
        student[] students = {
            new student(101, "Rahul", "pass123"),
            new student(102, "Priya", "pass456"),
            new student(103, "Amit", "pass789")
        };

        try {
            ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream("students.dat")
            );

            out.writeObject(students);
            out.close();

            System.out.println("Objects saved successfully.\n");

            ObjectInputStream in = new ObjectInputStream(
                new FileInputStream("students.dat")
            );

            student[] loadedStudents = (student[]) in.readObject();
            in.close();

            System.out.println("Objects loaded from file:\n");

            for (student s : loadedStudents) {
                System.out.println("ID: " + s.id);
                System.out.println("Name: " + s.name);
                System.out.println("Password: " + s.password);
                System.out.println();
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
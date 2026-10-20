abstract class employee{
    
    String name;
    int ID;
    
    public employee(String name, int ID)
    {
        this.name = name;
        this.ID = ID;
    }

    abstract double monthlySalary();
};

class fulltime extends employee
{
    double fixed;

    fulltime(double fixed, String name, int ID)
    {
        super(name, ID);
        this.fixed = fixed;
    }

    double monthlySalary()
    {
        return fixed;
    }
};

class PartTime extends employee
{
    int hours;
    double rate;

    PartTime(int hours, double rate, String name, int ID)
    {
        super(name, ID);
        this.hours = hours;
        this.rate = rate;
    }

    double monthlySalary()
    {
        return (hours * rate);
    }
};

class Intern extends employee
{
    double stipend;

    Intern(double stipend, String name, int ID)
    {
        super(name, ID);
        this.stipend = stipend;
    }

    double monthlySalary()
    {
        return stipend;
    }
};

public class Payroll {
    public static void main(String[] args) {
        
        employee[] employee = {
            new fulltime(100000, "abc", 101),
            new PartTime(6, 250, "abcd", 102),
            new Intern(10000, "abcde", 103),
            new fulltime(50000, "abcdef", 104),
            new PartTime(7, 200, "abcdefg", 105),
            new Intern(8000, "abcdefgh", 105)
        };

        System.out.println("Employee details");

        for(int i = 0; i < employee.length; ++i)
        {
            System.out.println("Employee " + (i + 1) + ":");
            System.out.println("Employee name: " + employee[i].name + (i + 1) + " Salary " + employee[i].monthlySalary());
        }
    }
}
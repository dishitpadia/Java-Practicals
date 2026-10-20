abstract class Shape {

    abstract public double area();

};

class circle extends Shape {

    public float radius;

    public circle(float radius) {
        this.radius = radius;
    }

    public double area() {
        return (Math.PI * radius * radius);
    }

    public String getShape() {
        return "circle";
    }
};

class rectangle extends Shape {

    public float length;
    public float width;

    public rectangle(float length, float width) {
        this.length = length;
        this.width = width;
    }

    public double area() {
        return length * width;
    }

    public String getShape() {
        return "rectangle";
    }
};

class triangle extends Shape {

    public float height;
    public float base;

    public triangle(float height, float base) {
        this.height = height;
        this.base = base;
    }

    public double area() {
        return (height * base) / 2;
    }

    public String getShape() {
        return "triangle";
    }
};

public class ShapeArea {

    public static void main(String[] args) {

        Shape[] shapes = {
                new circle(5),
                new rectangle(4, 6),
                new circle(3),
                new triangle(30, 45),
                new rectangle(15, 15)
        };

        double total = 0, max;
        max = shapes[0].area();

        for(int i = 0; i < shapes.length; ++i)
        {
            total = total + shapes[i].area();
            System.out.println("The area is: " + shapes[i].area());
            System.out.println("Running total: " + total);
        }

        for(int i = 0; i < shapes.length; ++i) {
            if(max < shapes[i].area()) {
                max = shapes[i].area();
            }
        }

        
        System.out.println("Maximum area is " + max);
    }
}

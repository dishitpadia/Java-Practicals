public class BoundedStats {

    public static <N extends Number> double average(N[] values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("Array cannot be null or empty");
        }

        double sum = 0;

        for (N value : values) {
            sum += value.doubleValue();
        }

        return sum / values.length;
    }

    public static void main(String[] args) {
        Integer[] integers = {10, 20, 30, 40};
        Double[] doubles = {2.5, 3.5, 4.5};
        Long[] longs = {100L, 200L, 300L};

        System.out.println("Integer Average: " + average(integers));
        System.out.println("Double Average: " + average(doubles));
        System.out.println("Long Average: " + average(longs));

        // String[] words = {"Java", "C++"};
        // average(words); // Compilation error
    }
}
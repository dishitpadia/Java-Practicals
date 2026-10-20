public class DriverPoint {
    public static void main(String[] args) {

        Point[] points = { new Point(2, 4), 
            new Point(2, 5), new Point(1, 4),
            new Point(2, 4), new Point(2, 5)
        };

        int distinct = 0;

        for (int i = 0; i < points.length; i++) {
            boolean found = false;

            for (int j = 0; j < i; j++) {
                if (points[i].equals(points[j])) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                distinct++;
            }
        }

        System.out.println("Distinct: " + distinct);

    }
}

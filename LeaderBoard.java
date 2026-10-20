import java.util.*;

public class LeaderBoard {
    public static void main(String[] args) {
        Map<String, Integer> scores = new HashMap<>();

        scores.put("Rahul", 850);
        scores.put("Priya", 920);
        scores.put("Amit", 780);
        scores.put("Neha", 920);
        scores.put("Karan", 870);
        scores.put("Riya", 850);

        int N = 4;

        List<Map.Entry<String, Integer>> leaderboard =
                new ArrayList<>(scores.entrySet());

        leaderboard.sort(new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> a,
                               Map.Entry<String, Integer> b) {
                int scoreCompare = b.getValue().compareTo(a.getValue());

                if (scoreCompare != 0) {
                    return scoreCompare;
                }

                return a.getKey().compareTo(b.getKey());
            }
        });

        System.out.println("Top " + N + " Players:");
        System.out.println("------------------------");

        for (int i = 0; i < Math.min(N, leaderboard.size()); i++) {
            Map.Entry<String, Integer> player = leaderboard.get(i);

            System.out.println(
                    (i + 1) + ". " + player.getKey()
                    + " - " + player.getValue() + " points"
            );
        }
    }
}
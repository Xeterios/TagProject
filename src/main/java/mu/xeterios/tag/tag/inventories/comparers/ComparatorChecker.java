package mu.xeterios.tag.tag.inventories.comparers;

import mu.xeterios.tag.tag.inventories.LeaderboardType;
import mu.xeterios.tag.tag.players.PlayerData;

import java.util.ArrayList;

public class ComparatorChecker {
    public static ArrayList<LeaderboardType> getCompareOrder(LeaderboardType input) {
        ArrayList<LeaderboardType> order = new ArrayList<>();
        order.add(input);
        for (LeaderboardType type : LeaderboardType.values()) {
            if (!type.equals(input)) {
                order.add(type);
            }
        }
        return order;
    }

    public static int getCompareValue(LeaderboardType type, PlayerData o1, PlayerData o2) {
        return switch (type) {
            case Points -> o2.getTotalPoints() - o1.getTotalPoints();
            case Wins -> o2.getTotalWins() - o1.getTotalWins();
            case Winstreak -> o2.getWinStreak() - o1.getWinStreak();
        };
    }

    public static int getCompleteCompareValue(PlayerData o1, PlayerData o2, ArrayList<LeaderboardType> checkOrder){
        int value = 0;
        boolean continueCheck = true;
        for(int i = 0; i < checkOrder.size() && continueCheck; i++){
            int checkValue = ComparatorChecker.getCompareValue(checkOrder.get(i), o1, o2);
            if (checkValue < 0){
                checkValue = -1;
            } else if (checkValue > 0){
                checkValue = 1;
            }
            value = checkValue;
            continueCheck = checkValue == 0;
        }
        return value;
    }
}

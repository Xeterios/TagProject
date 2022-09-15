package mu.xeterios.tag.tag.inventories.comparers;


import mu.xeterios.tag.tag.inventories.LeaderboardType;
import mu.xeterios.tag.tag.players.PlayerData;

import java.util.ArrayList;
import java.util.Comparator;

public class PointComparator implements Comparator<PlayerData> {
    @Override
    public int compare(PlayerData o1, PlayerData o2) {
        ArrayList<LeaderboardType> checkOrder = ComparatorChecker.getCompareOrder(LeaderboardType.Points);
        return ComparatorChecker.getCompleteCompareValue(o1, o2, checkOrder);
    }
}



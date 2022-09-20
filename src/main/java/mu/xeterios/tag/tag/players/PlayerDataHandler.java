package mu.xeterios.tag.tag.players;

import lombok.Getter;
import mu.xeterios.tag.config.Config;
import mu.xeterios.tag.tag.inventories.LeaderboardType;
import mu.xeterios.tag.tag.inventories.comparers.LongestWinStreakComparator;
import mu.xeterios.tag.tag.inventories.comparers.PointComparator;
import mu.xeterios.tag.tag.inventories.comparers.WinsComparator;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class PlayerDataHandler {

    @Getter private ArrayList<PlayerData> allPlayerData;

    public PlayerDataHandler(Config config){
        LoadData(config.LoadPlayerData());
    }

    public void LoadData(ArrayList<PlayerData> data){
        this.allPlayerData = data;
    }

    public void AddPlayer(PlayerData playerData){
        if (!allPlayerData.contains(playerData)){
            allPlayerData.add(playerData);
        }
    }

    public PlayerData GetPlayer(Player player){
        for (PlayerData playerData : allPlayerData){
            if (playerData.getUuid().equals(player.getUniqueId())){
                return playerData;
            }
        }
        return null;
    }

    public PlayerData GetPlayer(UUID uuid){
        for (PlayerData playerData : allPlayerData){
            if (playerData.getUuid().equals(uuid)){
                return playerData;
            }
        }
        return null;
    }

    public OfflinePlayer GetOfflinePlayer(String name){
        for (PlayerData playerData : allPlayerData){
            OfflinePlayer player = Bukkit.getOfflinePlayer(playerData.getUuid());
            assert player.getName() != null;
            if (player.getName().equals(name)){
                return player;
            }
        }
        return null;
    }

    public OfflinePlayer GetOfflinePlayer(UUID uuid){
        for (PlayerData playerData : allPlayerData){
            OfflinePlayer player = Bukkit.getOfflinePlayer(playerData.getUuid());
            assert player.getName() != null;
            if (player.getUniqueId().equals(uuid)){
                return player;
            }
        }
        return null;
    }

    public boolean CheckPlayer(Player player){
        UUID uuid = player.getUniqueId();
        for(PlayerData playerData : allPlayerData){
            if (playerData.getUuid().equals(uuid)){
                return true;
            }
        }
        return false;
    }

    public ArrayList<PlayerData> GetLeaderboard(LeaderboardType sortingType){
        ArrayList<PlayerData> leaderboard = new ArrayList<>(allPlayerData);
        switch (sortingType){
            case Points -> leaderboard.sort(new PointComparator());
            case Wins -> leaderboard.sort(new WinsComparator());
            case Winstreak -> leaderboard.sort(new LongestWinStreakComparator());
        }
        return leaderboard;
    }

    public int GetLeaderboardPosition(LeaderboardType type, PlayerData data){
        ArrayList<PlayerData> leaderboard = GetLeaderboard(type);
        int place = leaderboard.indexOf(data);
        if (place > 0){
            switch (type){
                case Points -> {
                    for(PlayerData leaderboardData : leaderboard){
                        if (leaderboardData.getTotalPoints() == data.getTotalPoints()){
                            place = leaderboard.indexOf(leaderboardData);
                        }
                    }
                }
                case Wins -> {
                    for(PlayerData leaderboardData : leaderboard){
                        if (leaderboardData.getTotalWins() == data.getTotalWins()){
                            place = leaderboard.indexOf(leaderboardData);
                        }
                    }
                }
                case Winstreak -> {
                    for(PlayerData leaderboardData : leaderboard){
                        if (leaderboardData.getWinStreak() == data.getWinStreak()){
                            place = leaderboard.indexOf(leaderboardData);
                        }
                    }
                }
            }
        }
        return place + 1;
    }
}

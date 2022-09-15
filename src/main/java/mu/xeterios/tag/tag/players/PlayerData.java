package mu.xeterios.tag.tag.players;

import lombok.Getter;
import mu.xeterios.tag.Main;
import mu.xeterios.tag.tag.inventories.LeaderboardType;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SerializableAs("PlayerData")
public class PlayerData implements ConfigurationSerializable {

    @Getter private final UUID uuid;
    @Getter private int totalPoints;
    @Getter private int totalWins;
    @Getter private int winStreak;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        this.totalPoints = 0;
        this.totalWins = 0;
        this.winStreak = 0;
    }

    public PlayerData(UUID uuid, int totalPoints, int totalWins, int longestWinStreak) {
        this.uuid = uuid;
        this.totalPoints = totalPoints;
        this.totalWins = totalWins;
        this.winStreak = longestWinStreak;
    }

    public void addPoints(int amount){
        totalPoints += amount;
    }

    public void addWin(){
        totalWins++;
        winStreak++;
    }

    public void resetWinStreakCount(){
        winStreak = 0;
    }

    @Override
    public java.util.@NotNull Map<String, Object> serialize() {
        java.util.Map<String, Object> result = new HashMap<>();
        result.put("uuid", uuid.toString());
        result.put("points", totalPoints);
        result.put("wins", totalWins);
        result.put("winStreak", winStreak);
        return result;
    }

    public static PlayerData deserialize(java.util.Map<String, Object> args) {
        UUID uuid = null;
        int points = 0;
        int wins = 0;
        int longestWinStreak = 0;
        if (args.containsKey("uuid")) {
            uuid = UUID.fromString((String) args.get("uuid"));
        }
        if (args.containsKey("points")) {
            points = (int) args.get("points");
        }
        if (args.containsKey("wins")) {
            wins = (int) args.get("wins");
        }
        if (args.containsKey("winStreak")) {
            longestWinStreak = (int) args.get("winStreak");
        }
        return new PlayerData(uuid, points, wins, longestWinStreak);
    }

    public static ItemStack getPlayerHead(OfflinePlayer target, PlayerData playerData, PlayerDataHandler playerDataHandler, LeaderboardType sortingType){
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
        ItemMeta playerHeadMeta = playerHead.getItemMeta();

        if (sortingType != null){
            playerHeadMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', getPlaceColor(playerDataHandler, playerData, sortingType) + target.getName()));
        } else {
            playerHeadMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&9" + target.getName()));
        }

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.translateAlternateColorCodes('&', "&eStats"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&f" + playerData.getTotalWins() + " wins " + getPlaceColor(playerDataHandler, playerData, LeaderboardType.Wins) + "(#" + playerDataHandler.GetLeaderboardPosition(LeaderboardType.Wins, playerData) + ")"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&f" + playerData.getTotalPoints() + " points " + getPlaceColor(playerDataHandler, playerData, LeaderboardType.Points) + "(#" + playerDataHandler.GetLeaderboardPosition(LeaderboardType.Points, playerData) + ")"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&fWin streak: " + playerData.getWinStreak() + " " + getPlaceColor(playerDataHandler, playerData, LeaderboardType.Winstreak) + "(#" + playerDataHandler.GetLeaderboardPosition(LeaderboardType.Winstreak, playerData) + ")"));
        playerHeadMeta.setLore(lore);

        SkullMeta skullMeta = (SkullMeta) playerHeadMeta;
        skullMeta.setOwningPlayer(target);
        playerHead.setItemMeta(skullMeta);
        return playerHead;
    }

    public static String getPlaceColor(PlayerDataHandler playerDataHandler, PlayerData playerData, LeaderboardType type){
        int place = playerDataHandler.GetLeaderboardPosition(type, playerData);
        switch (place){
            case 1:
                return "&6";
            case 2:
                return TranslateColorToChatColor(Color.SILVER);
            case 3:
                return TranslateColorToChatColor(Color.fromRGB(205, 127, 50));
            case 4:
                return TranslateColorToChatColor(Color.fromRGB(32,178,170));
            default:
                if (place <= 10){
                    return TranslateColorToChatColor(Color.ORANGE);
                } else if (place <= 50){
                    return TranslateColorToChatColor(Color.fromRGB(152, 251, 152));
                } else if (place <= 100){
                    return TranslateColorToChatColor(Color.fromRGB(152, 251, 152));
                } else {
                    return "&f";
                }
        }
    }

    private static String TranslateColorToChatColor(Color color){
        String colorString = color.toString();
        String hexCode = colorString.substring(colorString.indexOf('x'), colorString.indexOf('x') + 7).toLowerCase();
        hexCode = hexCode.replaceAll("\\B|\\b", "&");
        hexCode = hexCode.substring(0, hexCode.lastIndexOf("&"));
        return hexCode;
    }
}
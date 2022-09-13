package mu.xeterios.tag.tag.players;

import lombok.Getter;
import mu.xeterios.tag.Main;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

@SerializableAs("PlayerData")
public class PlayerData implements ConfigurationSerializable {

    @Getter private final UUID uuid;
    @Getter private int totalPoints;
    @Getter private int totalWins;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        this.totalPoints = 0;
        this.totalWins = 0;
    }

    public PlayerData(UUID uuid, int totalPoints, int totalWins) {
        this.uuid = uuid;
        this.totalPoints = totalPoints;
        this.totalWins = totalWins;
    }

    public void addPoints(int amount){
        totalPoints += amount;
        Main.getPlugin(Main.class).getLogger().info(String.valueOf(totalPoints));
    }

    public void addWin(){
        totalWins++;
        Main.getPlugin(Main.class).getLogger().info(String.valueOf(totalWins));
    }

    @Override
    public java.util.@NotNull Map<String, Object> serialize() {
        java.util.Map<String, Object> result = new HashMap<>();
        result.put("uuid", uuid.toString());
        result.put("points", totalPoints);
        result.put("wins", totalWins);
        return result;
    }

    public static PlayerData deserialize(java.util.Map<String, Object> args) {
        UUID uuid = null;
        int points = 0;
        int wins = 0;
        if (args.containsKey("uuid")) {
            uuid = UUID.fromString((String) args.get("uuid"));
        }
        if (args.containsKey("points")) {
            points = (int) args.get("points");
        }
        if (args.containsKey("wins")) {
            wins = (int) args.get("wins");
        }
        return new PlayerData(uuid, points, wins);
    }
}

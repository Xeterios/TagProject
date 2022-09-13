package mu.xeterios.tag.tag.players;

import lombok.Getter;
import mu.xeterios.tag.config.Config;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class PlayerDataHandler {

    @Getter private final ArrayList<PlayerData> allPlayerData;

    public PlayerDataHandler(Config config){
        this.allPlayerData = config.LoadPlayerData();
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

    public boolean CheckPlayer(Player player){
        UUID uuid = player.getUniqueId();
        for(PlayerData playerData : allPlayerData){
            if (playerData.getUuid().equals(uuid)){
                return true;
            }
        }
        return false;
    }
}

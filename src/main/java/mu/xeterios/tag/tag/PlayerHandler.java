package mu.xeterios.tag.tag;

import mu.xeterios.tag.config.Config;
import mu.xeterios.tag.tag.players.PlayerData;
import mu.xeterios.tag.tag.players.PlayerDataHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerHandler implements Listener {

    private final Config config;
    private final PlayerDataHandler playerDataHandler;

    public PlayerHandler(Config config, PlayerDataHandler playerDataHandler){
        this.config = config;
        this.playerDataHandler = playerDataHandler;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        if (!playerDataHandler.CheckPlayer(player)){
            playerDataHandler.AddPlayer(new PlayerData(player.getUniqueId()));
        }
    }
}

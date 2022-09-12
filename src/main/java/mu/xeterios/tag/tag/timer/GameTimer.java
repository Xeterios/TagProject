package mu.xeterios.tag.tag.timer;

import mu.xeterios.tag.Main;
import mu.xeterios.tag.config.Map;
import mu.xeterios.tag.tag.Tag;
import mu.xeterios.tag.tag.players.PlayerManager;
import mu.xeterios.tag.tag.players.PlayerType;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.TimerTask;

public class GameTimer extends TimerTask {

    private final TimerHandler handler;
    private final Tag tag;
    private final PlayerManager playerManager;
    private final Map map;
    private boolean activated;
    private int i;

    public GameTimer(TimerHandler handler, Tag tag, Map map){
        this.activated = false;
        this.handler = handler;
        this.tag = tag;
        this.playerManager = tag.getPlayerManager();
        this.map = map;
        i = Math.max(30 - (tag.getRound() * 5), 5);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), () -> {
            if (activated){
                Particle.DustOptions options;
                if (i > 10 && i <= 15){
                    options = new Particle.DustOptions(Color.YELLOW, 1);
                } else if (i > 7 && i <= 10) {
                    options = new Particle.DustOptions(Color.ORANGE, 1);
                } else if (i > 5 && i <= 7){
                    options = new Particle.DustOptions(Color.ORANGE, 2);
                } else if (i > 3 && i <= 5){
                    options = new Particle.DustOptions(Color.RED, 2);
                } else if (i <= 3){
                    options = new Particle.DustOptions(Color.RED, 3);
                } else {
                    options = new Particle.DustOptions(Color.YELLOW, 1);
                }
                for (Player p : tag.getPlayerManager().GetPlayers(PlayerType.TAGGER)){
                    Location location = p.getLocation();
                    location.getWorld().spawnParticle(Particle.REDSTONE, location, 1, 0.5, 1, 0.5, options);
                }
            }
        }, 0, 1);

    }

    @Override
    public void run() {
        if (i == 0) {
            this.cancel();
            handler.StopTimer();
            this.tag.getObjective().setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "RIP");
            this.activated = false;
            Plugin plugin = Main.getPlugin(Main.class);
            for (Player p : playerManager.GetPlayers(PlayerType.TAGGER)){
                Bukkit.getScheduler().runTask(plugin, () -> playerManager.EliminatePlayer(p));
            }
            for (Player p : playerManager.GetPlayers(PlayerType.RUNNER)){
                playerManager.GetTagPlayer(p).AddPoints(1);
                if (playerManager.GetPlayers(new PlayerType[]{PlayerType.RUNNER, PlayerType.TAGGER}).size() != 1){
                    Bukkit.getScheduler().runTask(plugin, () -> playerManager.SendMessage(p, "$pluginPrefix" + ChatColor.WHITE + "You got 1 point for surviving this round."));
                }
            }
            Bukkit.getScheduler().runTask(plugin, tag.getPowerupHandler()::DespawnPowerups);
            handler.RunTimer(TimerType.NEXTROUND, map);
        } else {
            String message = tag.getConfig().getPluginColor() + "Taggers eliminated in &c" + i + " " + tag.getConfig().getPluginColor() + "seconds.";
            message = message.replaceAll("[&]", "§");
            for (Player p : playerManager.GetAllPlayers()) {
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
            }
            if (i == 15){
                this.activated = true;
            }
        }
        i--;
    }
}

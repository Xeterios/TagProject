package mu.xeterios.tag.tag.timer;

import mu.xeterios.tag.Main;
import mu.xeterios.tag.config.Map;
import mu.xeterios.tag.tag.Tag;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.TimerTask;

public class GameTimer extends TimerTask {

    private final TimerHandler handler;
    private final Tag tag;
    private final Map map;
    private boolean activated;
    private int i;

    public GameTimer(TimerHandler handler, Tag tag, Map map){
        this.activated = false;
        this.handler = handler;
        this.tag = tag;
        this.map = map;
        i = Math.max(30 - (tag.round * 5), 5);

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
                for (Player p : tag.taggers){
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
            this.tag.objective.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "RIP");
            this.activated = false;
            for (Player p : tag.taggers){
                Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), () -> tag.EliminatePlayer(p));
            }
            tag.taggers.clear();
            Bukkit.getScheduler().runTask(tag.plugin, tag.powerupHandler::DespawnPowerups);
            handler.RunTimer(TimerType.NEXTROUND, map);
        } else {
            String message = tag.config.pluginColor + "Taggers eliminated in &c" + i + " " + tag.config.pluginColor + "seconds.";
            message = message.replaceAll("[&]", "§");
            for (Player p : tag.allPlayersAndSpectators) {
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
            }
            if (i == 15){
                this.activated = true;
            }
        }
        i--;
    }
}

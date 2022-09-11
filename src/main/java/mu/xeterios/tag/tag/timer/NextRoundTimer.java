package mu.xeterios.tag.tag.timer;

import mu.xeterios.tag.Main;
import mu.xeterios.tag.config.Map;
import mu.xeterios.tag.tag.Tag;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.TimerTask;

public class NextRoundTimer extends TimerTask {

    private final TimerHandler handler;
    private final Tag tag;
    private final Map map;
    private int i = 5;

    public NextRoundTimer(TimerHandler handler, Tag tag, Map map){
        this.handler = handler;
        this.tag = tag;
        this.map = map;
    }

    @Override
    public void run() {
        if (i == 0) {
            this.cancel();
            handler.StopTimer();
            tag.round++;
            Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), tag::SelectTaggers);
            this.tag.objective.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Taggers");
            for (Player p : tag.allPlayersAndSpectators){
                p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 15, 1);
            }
            handler.RunTimer(TimerType.GAME, map);
        } else {
            String message = tag.config.pluginColor + "Next round in &b" + i + " " + tag.config.pluginColor + "seconds.";
            message = message.replaceAll("[&]", "§");
            for (Player p : tag.allPlayersAndSpectators) {
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
                if (i == 3){
                    if (tag.allPlayers.contains(p)){
                        Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), () -> p.teleport(map.getSpawn()));
                    }
                }
                if (i <= 3){
                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 15, 0);
                }
            }
        }
        i--;
    }
}

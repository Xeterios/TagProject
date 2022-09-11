package mu.xeterios.tag.tag.timer;

import mu.xeterios.tag.Main;
import mu.xeterios.tag.config.Map;
import mu.xeterios.tag.tag.Tag;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.TimerTask;

public class StartupTimer extends TimerTask {

    private final TimerHandler handler;
    private final Tag tag;
    private final Map map;
    private final int start = 30;
    public int i = start;

    public StartupTimer(TimerHandler handler, Tag tag, Map map){
        this.handler = handler;
        this.tag = tag;
        this.map = map;
    }

    @Override
    public void run() {
        if (i % (start + 1) == 0) {
            this.cancel();
            handler.StopTimer();
            Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), tag::SelectTaggers);
            handler.RunTimer(TimerType.GAME, map);
            for (Player p : tag.allPlayersAndSpectators) {
                p.sendTitle(ChatColor.GREEN + "START", "", 10, 20, 10);
                p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 15, 2);
            }
        } else {
            String message = tag.config.pluginColor + "Game starts in &e" + i + " " + tag.config.pluginColor + "seconds.";
            message = message.replaceAll("[&]", "§");
            StringBuilder sb = new StringBuilder();
            switch (i) {
                case start -> {
                    sb.append(ChatColor.GOLD);
                    sb.append(ChatColor.BOLD);
                    sb.append(map.getName());
                }
                case 10, 9, 8, 7 -> {
                    sb.append(ChatColor.GREEN);
                    sb.append(i);
                }
                case 6, 5, 4 -> {
                    sb.append(ChatColor.YELLOW);
                    sb.append(i);
                }
                case 3, 2 -> {
                    sb.append(ChatColor.translateAlternateColorCodes('&', "&x&f&f&a&6&0&0"));
                    sb.append(i);
                }
                case 1 -> {
                    sb.append(ChatColor.RED);
                    sb.append(i);
                }
            }
            for (Player p : tag.allPlayersAndSpectators) {
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
                if (i == start){
                    Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), () -> p.teleport(map.getSpawn()));
                    if (tag.allPlayers.contains(p)){
                        Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), () -> p.setGameMode(GameMode.ADVENTURE));
                    } else {
                        Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), () -> p.setGameMode(GameMode.SPECTATOR));
                    }
                    p.sendTitle(sb.toString(), "", 10, 80, 10);
                }
                if (i <= 10){
                    p.sendTitle(sb.toString(), "", 10, 20, 10);
                }
                if (i > 3 && i <= 10){
                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 15, 0);
                }
                if (i <= 3){
                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 15, 1);
                }

            }
        }
        i--;
    }
}

package mu.xeterios.tag.commands.command;

import mu.xeterios.tag.Main;
import mu.xeterios.tag.config.Config;
import mu.xeterios.tag.config.Map;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Iterator;

public class Maps implements Cmd {

    @Override
    public void Execute(CommandSender sender, Main main, Config config) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8┌────── " + config.pluginColor + "&lTag&8"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8│"));
        if (config.maps.size() > 0){
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8│ "+ config.pluginColor + "Maps:"));
            for (Iterator<String> it = config.maps.keys().asIterator(); it.hasNext(); ) {
                String info = it.next();
                mu.xeterios.tag.config.Map map = config.GetMap(info);
                TextComponent tc = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&8│ &6" + map.getName() + " &7[Info]"));
                StringBuilder sb = new StringBuilder();
                sb.append(ChatColor.GOLD + "Region:\n");
                if (map.getSpawn() != null){
                    sb.append(ChatColor.GOLD + "Spawn: " + ChatColor.GRAY + map.getSpawn().getWorld().getName() + ": " + map.getSpawn().getBlockX() + " " + map.getSpawn().getBlockY() + " " + map.getSpawn().getBlockZ() + "\n");
                } else {
                    sb.append(ChatColor.GOLD + "Spawn: " + ChatColor.GRAY + "" + ChatColor.UNDERLINE + "Not set!\n");
                }
                if (map.getMin() != null){
                    sb.append(ChatColor.GOLD + "Pos1: " + ChatColor.GRAY + map.getMin().getWorld().getName() + ": " + map.getMin().getBlockX() + " " + map.getMin().getBlockY() + " " + map.getMin().getBlockZ() + "\n");
                } else {
                    sb.append(ChatColor.GOLD + "Pos1: " + ChatColor.GRAY + "" + ChatColor.UNDERLINE + "Not set!\n");
                }
                if (map.getMax() != null){
                    sb.append(ChatColor.GOLD + "Pos1: " + ChatColor.GRAY + map.getMax().getWorld().getName() + ": " + map.getMax().getBlockX() + " " + map.getMax().getBlockY() + " " + map.getMax().getBlockZ() + "\n");
                } else {
                    sb.append(ChatColor.GOLD + "Pos1: " + ChatColor.GRAY + "" + ChatColor.UNDERLINE + "Not set!\n");
                }
                tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(sb.toString())));
                sender.sendMessage(tc);
            }
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8│ "+ config.pluginColor + "There are no maps!"));
        }
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8│"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8└────── " + config.pluginColor + "&lTag&8"));
    }

    @Override
    public void SendFaultyMessage(CommandSender sender, Main main, Config config) {
        sender.sendMessage(config.pluginPrefix + ChatColor.RED + "An unknown error occured.");
    }
}

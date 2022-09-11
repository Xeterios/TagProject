package mu.xeterios.tag.commands.command;

import mu.xeterios.tag.Main;
import mu.xeterios.tag.config.Config;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Default implements Cmd {

    @Override
    public void Execute(CommandSender sender, Main main, Config config) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8┌──────────── " + config.pluginColor + "&lTag"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8│ " + config.pluginColor + "/tag &8» &fDisplay this menu"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8│ " + config.pluginColor + "/tag setspawn &8» &fSet the spawnpoint for tag"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8│ " + config.pluginColor + "/tag editregion &8» &fEdit the region for the arena"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8│ " + config.pluginColor + "/tag createmap &8» &fCreate a new map"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8│ " + config.pluginColor + "/tag maps &8» &fList all the maps"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8│ " + config.pluginColor + "/tag start <world> &8» &fStart a game of tag"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8│ " + config.pluginColor + "/tag stop &8» &fStop an active game of tag"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8│ " + config.pluginColor + "/tag reload &8» &fReload the plugin"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8┌──────────── " + config.pluginColor + "&lTag"));
    }

    @Override
    public void SendFaultyMessage(CommandSender sender, Main main, Config config) {
        sender.sendMessage(config.pluginPrefix + ChatColor.RED + "An unknown error occured.");
    }
}

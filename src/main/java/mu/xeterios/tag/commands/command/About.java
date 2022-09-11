package mu.xeterios.tag.commands.command;

import mu.xeterios.tag.Main;
import mu.xeterios.tag.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class About implements Cmd {

    @Override
    public void Execute(CommandSender sender, Main main, Config config) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8┌────── " + config.pluginColor + "&lTag &8──────┐"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8│ " + config.pluginColor + "Version &8» &f" + Bukkit.getPluginManager().getPlugin("Tag").getDescription().getVersion() + "                &8│"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8│ " + config.pluginColor + "Created on &8» &f20-8-2022   &8│"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8└──── " + config.pluginColor + "by Xeterios &8────┘"));
    }

    @Override
    public void SendFaultyMessage(CommandSender sender, Main main, Config config) {
        sender.sendMessage(config.pluginPrefix + ChatColor.RED + "An unknown error occured.");
    }
}

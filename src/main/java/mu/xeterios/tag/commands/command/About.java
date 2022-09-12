package mu.xeterios.tag.commands.command;

import mu.xeterios.tag.Main;
import mu.xeterios.tag.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class About implements Cmd {

    @Override
    public void Execute(CommandSender sender, Main main, Config config) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8┌────── " + config.getPluginColor() + "&lTag"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8│ " + config.getPluginColor() + "Version &8» &f" + Bukkit.getPluginManager().getPlugin("Tag").getDescription().getVersion()));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8│ " + config.getPluginColor() + "Created on &8» &f12-09-2022"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8└──── " + config.getPluginColor() + "by Xeterios"));
    }

    @Override
    public void SendFaultyMessage(CommandSender sender, Main main, Config config) {
        sender.sendMessage(config.getPluginPrefix() + ChatColor.RED + "An unknown error occured.");
    }
}

package mu.xeterios.tag.commands.command;

import mu.xeterios.tag.Main;
import mu.xeterios.tag.config.Config;
import mu.xeterios.tag.tag.Tag;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Start implements Cmd {

    private final String[] args;
    private Tag tag;

    public Start(String[] args){
        this.args = args;
    }

    @Override
    public void Execute(CommandSender sender, Main main, Config config) {
        try {
            tag = main.getTag();
            tag.SetConfig(config);
            if (sender instanceof Player){
                tag.saveOldScoreboard = ((Player) sender).getScoreboard();
            } else {
                tag.saveOldScoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
            }
            if (!tag.CheckStart(args[1], args[2].toLowerCase())){
                SendFaultyMessage(sender, main, config);
            } else {
                tag.Start();
                sender.sendMessage(config.pluginPrefix + "Tag started.");
            }
        } catch (IndexOutOfBoundsException e){
            SendFaultyMessage(sender, main, config);
        }
    }

    @Override
    public void SendFaultyMessage(CommandSender sender, Main main, Config config) {
        if (args.length == 1){
            sender.sendMessage(config.pluginPrefix + ChatColor.RED + "Please enter a valid world name");
        } else if (Bukkit.getWorld(args[1]) == null && !args[1].equals("@a")) {
            sender.sendMessage(config.pluginPrefix + ChatColor.RED + "Please enter a valid world name");
        } else if (args.length == 2){
            sender.sendMessage(config.pluginPrefix + ChatColor.RED + "Please enter a valid map name");
        } else if (config.GetMap(args[2]).getSpawn() == null){
            sender.sendMessage(config.pluginPrefix + ChatColor.RED + "The map does not have a spawn set");
        } else if (config.GetMap(args[2]).getMin() == null){
            sender.sendMessage(config.pluginPrefix + ChatColor.RED + "The map does not have pos1 set");
        } else if (config.GetMap(args[2]).getMax() == null){
            sender.sendMessage(config.pluginPrefix + ChatColor.RED + "The map does not have pos2 set");
        } else if (tag.allPlayers.size() == 1){
            sender.sendMessage(config.pluginPrefix + ChatColor.RED + "You can't play tag alone.");
        } else if (tag.allPlayers.size() == 0){
            sender.sendMessage(config.pluginPrefix + ChatColor.RED + "There is no one who wants to play tag there.");
        } else if (tag.started){
            sender.sendMessage(config.pluginPrefix + ChatColor.RED + "Tag is already started.");
        } else {
            sender.sendMessage(config.pluginPrefix + ChatColor.RED + "An unknown error occurred.");
        }
    }
}

package mu.xeterios.tag.commands.command;

import mu.xeterios.tag.Main;
import mu.xeterios.tag.config.Config;
import mu.xeterios.tag.tag.players.PlayerData;
import mu.xeterios.tag.tag.players.PlayerDataHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Profile implements Cmd {

    private final String[] args;

    public Profile(String[] args){
        this.args = args;
    }

    @Override
    public void Execute(CommandSender sender, Main main, Config config) {
        if (sender instanceof Player player) {
            PlayerDataHandler playerDataHandler = config.getPlayerDataHandler();
            PlayerData playerData = playerDataHandler.GetPlayer(player);
            if (args.length > 1) {
                playerData = playerDataHandler.GetPlayer(Bukkit.getPlayerUniqueId(args[1]));
                if (playerData != null){
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getPluginPrefix() + Bukkit.getOfflinePlayer(args[1]).getName() + " has " + playerData.getTotalPoints()) + " points");
                } else {
                    SendFaultyMessage(sender, main, config);
                }
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getPluginPrefix() + "You have " + playerData.getTotalPoints()) + " points");
            }
        }
    }

    @Override
    public void SendFaultyMessage(CommandSender sender, Main main, Config config) {
        sender.sendMessage(config.getPluginPrefix() + ChatColor.RED + "An unknown error occured.");
    }
}

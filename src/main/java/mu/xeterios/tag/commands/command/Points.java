package mu.xeterios.tag.commands.command;

import mu.xeterios.tag.Main;
import mu.xeterios.tag.config.Config;
import mu.xeterios.tag.tag.players.PlayerData;
import mu.xeterios.tag.tag.players.PlayerDataHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class Points implements Cmd {

    private final String[] args;

    public Points(String[] args){
        this.args = args;
    }

    @Override
    public void Execute(CommandSender sender, Main main, Config config) {
        try {
            if (args.length > 2) {
                PlayerDataHandler handler = config.getPlayerDataHandler();
                OfflinePlayer target = Bukkit.getPlayer(args[2]);
                if (target == null){
                    target = Bukkit.getOfflinePlayerIfCached(args[2]);
                }
                if (target == null){
                    SendFaultyMessage(sender, main, config);
                }
                assert target != null;
                PlayerData playerData = handler.GetPlayer(target.getUniqueId());
                if (args[1].equals("reset")) {
                    playerData.setPoints(0);
                    sender.sendMessage(config.getPluginPrefix() + ChatColor.GREEN + target.getName() + "'s points have been reset");
                } else if (args.length > 3){
                    int amount = Integer.parseInt(args[3]);
                    switch (args[1]){
                        case "set" -> {
                            if (playerData.setPoints(amount)){
                                sender.sendMessage(config.getPluginPrefix() + ChatColor.GREEN + target.getName() + "'s points have been set to " + amount);
                            } else {
                                SendFaultyMessage(sender, main, config);
                            }
                        }
                        case "add" -> {
                            playerData.addPoints(amount);
                            sender.sendMessage(config.getPluginPrefix() + ChatColor.GREEN + target.getName() + "'s has been given " + amount + " points");
                        }
                        case "remove" -> {
                            if (playerData.removePoints(amount)) {
                                sender.sendMessage(config.getPluginPrefix() + ChatColor.GREEN + target.getName() + " has been deducted " + amount + " points");
                            } else {
                                SendFaultyMessage(sender, main, config);
                            }
                        }
                        default -> SendFaultyMessage(sender, main, config);
                    }
                } else {
                    SendFaultyMessage(sender, main, config);
                }
            } else {
                SendFaultyMessage(sender, main, config);
            }
        } catch (Exception ex){
            SendFaultyMessage(sender, main, config);
        }
    }

    @Override
    public void SendFaultyMessage(CommandSender sender, Main main, Config config) {
        if (args.length == 1){
            sender.sendMessage(config.getPluginPrefix() + ChatColor.RED + "You need to use set/add/remove/reset to change points");
        } else if (args.length == 2) {
            if (!(args[1].equals("set") || args[1].equals("add") || args[1].equals("remove") || args[1].equals("reset"))){
                sender.sendMessage(config.getPluginPrefix() + ChatColor.RED + "You need to use set/add/remove/reset to change points");
            } else if (!args[1].equals("reset")){
                sender.sendMessage(config.getPluginPrefix() + ChatColor.RED + "Please add a valid player name");
            }
        } else if (args.length == 3) {
            if (Bukkit.getOfflinePlayerIfCached(args[2]) == null){
                sender.sendMessage(config.getPluginPrefix() + ChatColor.RED + "This player has no statistics");
            } else {
                sender.sendMessage(config.getPluginPrefix() + ChatColor.RED + "Please enter a valid number");
            }
        } else if (args.length == 4){
            try {
                int amount = Integer.parseInt(args[2]);
                if (config.getPlayerDataHandler().GetPlayer(Objects.requireNonNull(Bukkit.getOfflinePlayerIfCached(args[2])).getUniqueId()).getTotalPoints() - amount < 0){
                    sender.sendMessage(config.getPluginPrefix() + ChatColor.RED + "You can't get points below 0");
                }
            } catch (Exception e){
                sender.sendMessage(config.getPluginPrefix() + ChatColor.RED + "Please enter a valid number");
            }
        } else {
            sender.sendMessage(config.getPluginPrefix() + ChatColor.RED + "An unknown error occured.");
        }
    }
}

package mu.xeterios.tag.commands.command;

import mu.xeterios.tag.Main;
import mu.xeterios.tag.config.Config;
import mu.xeterios.tag.tag.players.PlayerData;
import mu.xeterios.tag.tag.players.PlayerDataHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.Objects;

public class Wins implements Cmd {

    private final String[] args;

    public Wins(String[] args){
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
                    playerData.setWins(0);
                    sender.sendMessage(config.getPluginPrefix() + ChatColor.GREEN + target.getName() + "'s wins have been reset");
                } else if (args.length > 3){
                    int amount = Integer.parseInt(args[3]);
                    switch (args[1]){
                        case "set" -> {
                            if (playerData.setWins(amount)){
                                sender.sendMessage(config.getPluginPrefix() + ChatColor.GREEN + target.getName() + "'s wins have been set to " + amount);
                            } else {
                                SendFaultyMessage(sender, main, config);
                            }
                        }
                        case "add" -> {
                            playerData.addWins(amount);
                            sender.sendMessage(config.getPluginPrefix() + ChatColor.GREEN + target.getName() + "'s has been given " + amount + " wins");
                        }
                        case "remove" -> {
                            if (playerData.removeWins(amount)) {
                                sender.sendMessage(config.getPluginPrefix() + ChatColor.GREEN + target.getName() + " has been deducted " + amount + " wins");
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
            sender.sendMessage(config.getPluginPrefix() + ChatColor.RED + "You need to use set/add/remove/reset to change wins");
        } else if (args.length == 2) {
            if (!(args[1].equals("set") || args[1].equals("add") || args[1].equals("remove") || args[1].equals("reset"))){
                sender.sendMessage(config.getPluginPrefix() + ChatColor.RED + "You need to use set/add/remove/reset to change wins");
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
                if (config.getPlayerDataHandler().GetPlayer(Objects.requireNonNull(Bukkit.getOfflinePlayerIfCached(args[2])).getUniqueId()).getTotalWins() - amount < 0){
                    sender.sendMessage(config.getPluginPrefix() + ChatColor.RED + "You can't get wins below 0");
                }
            } catch (Exception e){
                sender.sendMessage(config.getPluginPrefix() + ChatColor.RED + "Please enter a valid number");
            }
        } else {
            sender.sendMessage(config.getPluginPrefix() + ChatColor.RED + "An unknown error occured.");
        }
    }
}

package mu.xeterios.tag.commands.command;

import mu.xeterios.tag.Main;
import mu.xeterios.tag.config.Config;
import mu.xeterios.tag.tag.inventories.LeaderboardInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Leaderboard implements Cmd {

    @Override
    public void Execute(CommandSender sender, Main main, Config config) {
        if (sender instanceof Player player) {
            OpenLeaderboard(player, config);
        }
    }

    @Override
    public void SendFaultyMessage(CommandSender sender, Main main, Config config) {
        sender.sendMessage(config.getPluginPrefix() + ChatColor.RED + "An unknown error occured.");
    }

    private void OpenLeaderboard(Player player, Config config){
        LeaderboardInventory inventory = new LeaderboardInventory(player, config);
        Bukkit.getPluginManager().registerEvents(inventory, Main.getPlugin(Main.class));
    }
}

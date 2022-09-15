package mu.xeterios.tag.commands.command;

import mu.xeterios.tag.Main;
import mu.xeterios.tag.commands.PermissionHandler;
import mu.xeterios.tag.config.Config;
import mu.xeterios.tag.tag.inventories.ProfileInventory;
import mu.xeterios.tag.tag.players.PlayerDataHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
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
            if (args.length == 1 && PermissionHandler.IsCommandMain(args[0])){
                OfflinePlayer target = playerDataHandler.GetOfflinePlayer(args[0]);
                OpenProfile(player, target, config);
            } else if (args.length > 1) {
                OfflinePlayer target = playerDataHandler.GetOfflinePlayer(args[1]);
                OpenProfile(player, target, config);
            } else {
                //sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getPluginPrefix() + "You have " + playerData.getTotalPoints()) + " points");
                OpenProfile(player, player, config);
            }
        }
    }

    @Override
    public void SendFaultyMessage(CommandSender sender, Main main, Config config) {
        sender.sendMessage(config.getPluginPrefix() + ChatColor.RED + "An unknown error occured.");
    }

    private void OpenProfile(Player player, OfflinePlayer target, Config config){
        ProfileInventory inventory = new ProfileInventory(player, target, config);
        Bukkit.getPluginManager().registerEvents(inventory, Main.getPlugin(Main.class));
    }
}

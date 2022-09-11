package mu.xeterios.tag.commands.command;

import mu.xeterios.tag.Main;
import mu.xeterios.tag.config.Config;
import mu.xeterios.tag.config.Map;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawn implements Cmd {

    private final String[] args;

    public SetSpawn(String[] args){
        this.args = args;
    }

    @Override
    public void Execute(CommandSender sender, Main main, Config config) {
        if (sender instanceof Player){
            if (args.length > 1){
                Main.getPlugin(Main.class).getLogger().info(String.valueOf(config.GetMap(args[1].toLowerCase()) != null));
                if (config.GetMap(args[1].toLowerCase()) != null) {
                    Map map = config.GetMap(args[1]);
                    config.EditSpawn(((Player) sender).getLocation(), map);
                    config.SaveConfig();
                    sender.sendMessage(config.pluginPrefix + ChatColor.translateAlternateColorCodes('&', config.pluginColor) + "New spawnpoint set: " + map.getSpawn().getWorld().getName() + ": " + (int) map.getSpawn().getX() + " " + (int) map.getSpawn().getY() + " " + (int) map.getSpawn().getZ());
                } else {
                    SendFaultyMessage(sender, main, config);
                }
            } else {
                SendFaultyMessage(sender, main, config);
            }
        } else {
            SendFaultyMessage(sender, main, config);
        }
    }

    @Override
    public void SendFaultyMessage(CommandSender sender, Main main, Config config) {
        if (args.length == 1){
            sender.sendMessage(config.pluginPrefix + ChatColor.RED + "Please enter a valid map name");
        } else if (config.GetMap(args[1].toLowerCase()) == null){
            sender.sendMessage(config.pluginPrefix + ChatColor.RED + "This map does not exist.");
        } else {
            sender.sendMessage(config.pluginPrefix + ChatColor.RED + "You can only use this command as a player.");
        }
    }
}

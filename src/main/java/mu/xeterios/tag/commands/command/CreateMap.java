package mu.xeterios.tag.commands.command;

import mu.xeterios.tag.Main;
import mu.xeterios.tag.config.Config;
import mu.xeterios.tag.config.Map;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;

public class CreateMap implements Cmd {

    private final String[] args;

    public CreateMap(String[] args){
        this.args = args;
    }

    @Override
    public void Execute(CommandSender sender, Main main, Config config) {
        if (args.length > 1) {
            if (config.GetMap(args[1].toLowerCase()) == null){
                config.CreateMap(args[1].toLowerCase());
                config.SaveConfig();
                sender.sendMessage(config.pluginPrefix + ChatColor.translateAlternateColorCodes('&', config.pluginColor) + "New map added: " + args[1]);
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
        } else if (config.GetMap(args[1].toLowerCase()) != null){
            sender.sendMessage(config.pluginPrefix + ChatColor.RED + "This map already exists.");
        } else {
            sender.sendMessage(config.pluginPrefix + ChatColor.RED + "An unknown error occurred.");
        }
    }
}

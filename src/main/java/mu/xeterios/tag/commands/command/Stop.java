package mu.xeterios.tag.commands.command;

import mu.xeterios.tag.Main;
import mu.xeterios.tag.config.Config;
import mu.xeterios.tag.tag.Tag;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Stop implements Cmd {

    @Override
    public void Execute(CommandSender sender, Main main, Config config) {
        Tag tag = main.getTag();
        if (!tag.Stop()){
            SendFaultyMessage(sender, main, config);
        } else {
            sender.sendMessage(config.getPluginPrefix() + "Tag stopped.");
        }
    }

    @Override
    public void SendFaultyMessage(CommandSender sender, Main main, Config config) {
        sender.sendMessage(config.getPluginPrefix() + ChatColor.RED + "Tag is not started.");
    }
}

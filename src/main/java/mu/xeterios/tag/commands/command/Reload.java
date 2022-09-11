package mu.xeterios.tag.commands.command;

import mu.xeterios.tag.Main;
import mu.xeterios.tag.config.Config;
import mu.xeterios.tag.tag.Tag;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Reload implements Cmd {

    @Override
    public void Execute(CommandSender sender, Main main, Config config) {
        main.getTag().Stop();
        config.ReloadConfig();
        main.setTag(new Tag(main));
        sender.sendMessage(config.pluginPrefix + ChatColor.GREEN + "Plugin reloaded successfully!");
    }

    @Override
    public void SendFaultyMessage(CommandSender sender, Main main, Config config) {
        sender.sendMessage(config.pluginPrefix + ChatColor.RED + "An unknown error occured.");
    }
}

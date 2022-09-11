package mu.xeterios.tag.commands.command;

import mu.xeterios.tag.Main;
import mu.xeterios.tag.config.Config;
import org.bukkit.command.CommandSender;

public interface Cmd {

    void Execute(CommandSender sender, Main main, Config config);
    void SendFaultyMessage(CommandSender sender, Main main, Config config);
}

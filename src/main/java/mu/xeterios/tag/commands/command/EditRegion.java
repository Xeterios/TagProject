package mu.xeterios.tag.commands.command;

import mu.xeterios.tag.Main;
import mu.xeterios.tag.config.Config;
import mu.xeterios.tag.config.Map;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class EditRegion implements Cmd, Listener {


    private final String[] args;
    private Config config;
    private Location pos1;
    private Location pos2;
    private boolean first;

    public EditRegion(String[] args){
        this.args = args;
    }

    @Override
    public void Execute(CommandSender sender, Main main, Config config) {
        if (sender instanceof Player){
            if (args.length > 1){
                if (config.GetMap(args[1].toLowerCase()) != null) {
                    this.config = config;
                    this.first = true;
                    main.getServer().getPluginManager().registerEvents(this, main);
                    sender.sendMessage(config.pluginPrefix + "Left click on a block to set first position");
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
        if (args.length == 1) {
            sender.sendMessage(config.pluginPrefix + ChatColor.RED + "Please enter a valid map name.");
        } else if (config.GetMap(args[1].toLowerCase()) == null){
            sender.sendMessage(config.pluginPrefix + ChatColor.RED + "This map does not exist.");
        } else {
            sender.sendMessage(config.pluginPrefix + ChatColor.RED + "You can only use this command as a player.");
        }
    }

    @EventHandler
    public void OnRightClick(PlayerInteractEvent e){
        if (e.getAction() == Action.LEFT_CLICK_BLOCK){
            if (first)
                this.pos1 = e.getClickedBlock().getLocation();
                e.getPlayer().sendMessage(config.pluginPrefix + "First location set: " + pos1.getX() + ", " + pos1.getY() + ", " + pos1.getZ());
                e.getPlayer().sendMessage(config.pluginPrefix + "Right click on a block to set second position");
                first = false;
            }
            e.setCancelled(true);
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK){
            if (!first){
                this.pos2 = e.getClickedBlock().getLocation();
                e.getPlayer().sendMessage(config.pluginPrefix + "Second location set: " + pos2.getX() + ", " + pos2.getY() + ", " + pos2.getZ());
                if (config.EditRegion(pos1, pos2, config.GetMap(args[1].toLowerCase()))){
                    e.getPlayer().sendMessage(config.pluginPrefix + "New region set successfully.");
                    config.SaveConfig();
                    HandlerList.unregisterAll(this);
                }
            }
        }
    }
}
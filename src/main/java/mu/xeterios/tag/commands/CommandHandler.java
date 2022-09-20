package mu.xeterios.tag.commands;

import mu.xeterios.tag.Main;
import mu.xeterios.tag.commands.command.Cmd;
import mu.xeterios.tag.commands.command.CmdFactory;
import mu.xeterios.tag.commands.command.Default;
import mu.xeterios.tag.commands.command.PermissionType;
import mu.xeterios.tag.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CommandHandler implements CommandExecutor, TabCompleter {

    private final Config config;
    private final Main plugin;

    public CommandHandler(Main plugin, Config config){
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        PermissionHandler handler = new PermissionHandler(sender);
        PermissionType type = handler.CheckPermission(command.getLabel());
        if (args.length > 0 && type == PermissionType.ALLOWED && PermissionHandler.IsCommandMain(label)){
            type = handler.CheckPermission(args[0]);
        }
        CheckPermission(sender, label, args, type);
        return true;
    }

    public void CheckPermission(CommandSender sender, String label, String[] args, PermissionType type) {
        switch (type) {
            case ALLOWED -> {
                CmdFactory cmdFactory = new CmdFactory();
                Cmd cmd = cmdFactory.GetCommand(label, args);
                if (cmd != null) {
                    cmd.Execute(sender, plugin, config);
                }
            }
            case NOPERM -> sender.sendMessage(config.getPluginPrefix() + ChatColor.RED + "You have no permission to use this command.");
            case UNKNOWN -> {
                Default df = new Default();
                df.Execute(sender, plugin, config);
            }
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (command.getLabel().equalsIgnoreCase("tag")){
            PermissionHandler handler = new PermissionHandler(sender);
            ArrayList<String> toReturn = new ArrayList<>();
            if (args.length == 1){
                if (handler.CheckPermission("setspawn") == PermissionType.ALLOWED){
                    toReturn.add("setspawn");
                }
                if (handler.CheckPermission("editregion") == PermissionType.ALLOWED){
                    toReturn.add("editregion");
                }
                if (handler.CheckPermission("createmap") == PermissionType.ALLOWED){
                    toReturn.add("createmap");
                }
                if (handler.CheckPermission("maps") == PermissionType.ALLOWED){
                    toReturn.add("maps");
                }
                if (handler.CheckPermission("profile") == PermissionType.ALLOWED){
                    toReturn.add("profile");
                }
                if (handler.CheckPermission("leaderboard") == PermissionType.ALLOWED){
                    toReturn.add("leaderboard");
                }
                if (handler.CheckPermission("points") == PermissionType.ALLOWED){
                    toReturn.add("points");
                }
                if (handler.CheckPermission("wins") == PermissionType.ALLOWED){
                    toReturn.add("wins");
                }
                if (handler.CheckPermission("start") == PermissionType.ALLOWED){
                    toReturn.add("start");
                }
                if (handler.CheckPermission("stop") == PermissionType.ALLOWED){
                    toReturn.add("stop");
                }
                if (handler.CheckPermission("reload") == PermissionType.ALLOWED){
                    toReturn.add("reload");
                }
                if (handler.CheckPermission("about") == PermissionType.ALLOWED){
                    toReturn.add("about");
                }
            }
            if (args.length == 2){
                if (args[0].equals("start") && handler.CheckPermission("start") == PermissionType.ALLOWED){
                    for(World world : Bukkit.getWorlds()){
                        toReturn.add(world.getName());
                    }
                    toReturn.add("@a");
                }
                if (args[0].equals("setspawn") && handler.CheckPermission("setspawn") == PermissionType.ALLOWED || args[0].equals("editregion") && handler.CheckPermission("editregion") == PermissionType.ALLOWED){
                    for (Iterator<String> it = config.getMaps().keys().asIterator(); it.hasNext(); ) {
                        String info = it.next();
                        toReturn.add(config.getMaps().get(info).getName());
                    }
                }
                if (args[0].equals("profile") && handler.CheckPermission("profile") == PermissionType.ALLOWED){
                    for (Player player : Bukkit.getOnlinePlayers()){
                        toReturn.add(player.getName());
                    }
                }
                if ((args[0].equals("points") && handler.CheckPermission("points") == PermissionType.ALLOWED) || (args[0].equals("wins") && handler.CheckPermission("wins") == PermissionType.ALLOWED)){
                    toReturn.add("set");
                    toReturn.add("add");
                    toReturn.add("remove");
                    toReturn.add("reset");
                }
            }
            if (args.length == 3){
                if (args[0].equals("start") && handler.CheckPermission("start") == PermissionType.ALLOWED){
                    for (Iterator<String> it = config.getMaps().keys().asIterator(); it.hasNext(); ) {
                        String info = it.next();
                        toReturn.add(config.getMaps().get(info).getName());
                    }
                }
                if ((args[0].equals("points") && handler.CheckPermission("points") == PermissionType.ALLOWED) || (args[0].equals("wins") && handler.CheckPermission("wins") == PermissionType.ALLOWED)){
                    for (Player player : Bukkit.getOnlinePlayers()){
                        toReturn.add(player.getName());
                    }
                }
            }
            return toReturn;
        }
        return null;
    }
}

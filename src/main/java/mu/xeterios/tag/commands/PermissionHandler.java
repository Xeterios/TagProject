package mu.xeterios.tag.commands;

import mu.xeterios.tag.commands.command.PermissionType;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

public class PermissionHandler {

    private final CommandSender sender;
    private final Dictionary<String, String> nodes;

    public PermissionHandler(CommandSender player){
        this.sender = player;
        this.nodes = new Hashtable<>();
        FillNodes();
    }

    public PermissionType CheckPermission(String arg) {
        try {
            String perm = GetNode(arg);
            if (sender.hasPermission(perm) || sender.hasPermission("tag.admin")){
                return PermissionType.ALLOWED;
            }
            return PermissionType.NOPERM;
        } catch (NullPointerException | IllegalArgumentException e) {
            return PermissionType.UNKNOWN;
        }
    }

    public static boolean IsCommandMain(String label){
        ArrayList<String> allLabels = new ArrayList<>();
        allLabels.add("tag");
        allLabels.add("profile");
        allLabels.add("leaderboard");
        for(String string : allLabels){
            if (label.equals(string)){
                return false;
            }
        }
        return true;
    }

    public void FillNodes(){
        nodes.put("tag", "tag.help");
        nodes.put("setspawn", "tag.setspawn");
        nodes.put("editregion", "tag.editregion");
        nodes.put("createmap", "tag.createmap");
        nodes.put("maps", "tag.maps");
        nodes.put("profile", "tag.profile");
        nodes.put("leaderboard", "tag.leaderboard");
        nodes.put("stop", "tag.stop");
        nodes.put("start", "tag.start");
        nodes.put("reload", "tag.reload");
        nodes.put("about", "tag.about");
    }

    public String GetNode(String input){
        if (nodes.get(input) != null){
            return nodes.get(input);
        }
        return null;
    }
}

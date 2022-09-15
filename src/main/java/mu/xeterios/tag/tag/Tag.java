package mu.xeterios.tag.tag;

import lombok.Getter;
import lombok.Setter;
import mu.xeterios.tag.Main;
import mu.xeterios.tag.config.Config;
import mu.xeterios.tag.config.Map;
import mu.xeterios.tag.tag.players.PlayerDataHandler;
import mu.xeterios.tag.tag.players.PlayerManager;
import mu.xeterios.tag.tag.players.PlayerType;
import mu.xeterios.tag.tag.players.TagPlayer;
import mu.xeterios.tag.tag.timer.TimerHandler;
import mu.xeterios.tag.tag.timer.TimerType;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.scoreboard.*;

import java.util.*;

public class Tag {

    private final Main plugin;

    @Getter @Setter private Config config;
    @Getter @Setter private int round = 0;

    @Getter private boolean started;
    @Getter private final ArrayList<World> worlds;
    @Getter private Map map;
    @Getter private Scoreboard scoreboard;
    @Setter private Scoreboard oldScoreboard;
    @Getter private Objective objective;

    @Getter private final PlayerManager playerManager;
    @Getter private final TimerHandler handler;
    @Getter private PowerupHandler powerupHandler;
    private final EventsHandler eventsHandler;

    @Getter private final PlayerDataHandler playerDataHandler;

    public Tag(Main plugin, PlayerDataHandler playerDataHandler){
        this.plugin = plugin;
        this.playerManager = new PlayerManager(this);
        this.worlds = new ArrayList<>();
        this.handler = new TimerHandler(this);
        this.eventsHandler = new EventsHandler(this);
        this.playerDataHandler = playerDataHandler;
    }

    public void Start(){
        if (!started){
            plugin.getServer().getPluginManager().registerEvents(eventsHandler, this.plugin);

            for (Player player : playerManager.GetAllPlayers()){
                player.getInventory().clear();
                player.setScoreboard(scoreboard);
            }

            for (Player player : playerManager.GetPlayers(PlayerType.RUNNER)){
                playerManager.MakeRunner(player);
                playerManager.ChangePlayerType(player, PlayerType.RUNNER);
            }

            this.started = true;
            if (config.isPowerups()){
                this.powerupHandler = new PowerupHandler(this);
                plugin.getServer().getPluginManager().registerEvents(powerupHandler, this.plugin);
            }
            handler.RunTimer(TimerType.STARTUP, map);
        }
    }

    public boolean CheckStart(String arg, String arg2){
        if (!started) {
            plugin.getServer().getPluginManager().registerEvents(eventsHandler, this.plugin);
            if (!Setup()) {
                return false;
            }
            if (arg.equals("@a")) {
                this.worlds.addAll(Bukkit.getWorlds());
            } else {
                this.worlds.add(Bukkit.getWorld(arg));
            }
            assert worlds.get(0) != null;
            if (arg2 != null) {
                Map map = config.getMaps().get(arg2);
                if (map != null) {
                    if (map.getSpawn() == null) {
                        return false;
                    }
                    if (map.getMin() == null) {
                        return false;
                    }
                    if (map.getMax() == null) {
                        return false;
                    }
                    this.map = map;
                } else {
                    return false;
                }
            }
            ArrayList<Player> toAdd = new ArrayList<>();
            for (World world : worlds){
                for (Player p : world.getPlayers()) {
                    if (!toAdd.contains(p)) {
                        toAdd.add(p);
                    }
                }
            }
            Main.getPlugin(Main.class).getLogger().info(String.valueOf(toAdd.size()));
            for(Player player : toAdd){
                if (player.hasPermission("tag.exempt")) {
                    playerManager.AddPlayer(player);
                    playerManager.ChangePlayerType(player, PlayerType.SPECTATOR);
                } else {
                    playerManager.AddPlayer(player);
                    playerManager.ChangePlayerType(player, PlayerType.RUNNER);
                }
            }

            if (!(playerManager.GetPlayers(PlayerType.RUNNER).size() > 1)){
                playerManager.ClearPlayers();
                return false;
            }
            return true;
        }
        return false;
    }

    public boolean Stop(){
        if (started){
            ArrayList<Player> runners = playerManager.GetPlayers(PlayerType.RUNNER);
            if (runners.size() > 0 && GetHighestPoints() > 0){
                for (Player player : playerManager.GetAllPlayers()){
                    SendPointsMessage(player);
                }
            }
            for (Player p : playerManager.GetAllPlayers()){
                p.setScoreboard(oldScoreboard);
                p.getInventory().clear();
            }
            if (config.isPowerups()){
                Bukkit.getScheduler().runTaskLater(this.plugin, this.powerupHandler::DespawnPowerups, 20L);
            }
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> HandlerList.unregisterAll(this.eventsHandler), 2L);
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> HandlerList.unregisterAll(this.powerupHandler), 2L);
            Bukkit.getScheduler().runTaskLater(this.plugin, this.playerManager::ClearPlayers, 5L);
            this.round = 0;
            this.started = false;
            this.handler.StopTimer();
            this.worlds.clear();

            return true;
        }
        return false;
    }

    private boolean Setup(){
        try {
            this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            if (scoreboard.getTeam("Taggers") != null){
                Objects.requireNonNull(scoreboard.getTeam("Taggers")).unregister();
            }
            Team t = scoreboard.registerNewTeam("Taggers");
            t.setPrefix(ChatColor.RED + "");
            t.setColor(ChatColor.RED);

            if (scoreboard.getTeam("Runners") != null){
                Objects.requireNonNull(scoreboard.getTeam("Runners")).unregister();
            }
            Team r = scoreboard.registerNewTeam("Runners");
            r.setPrefix(ChatColor.GREEN + "");
            r.setColor(ChatColor.GREEN);

            this.objective = scoreboard.registerNewObjective("Tag", "dummy");
            this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            this.objective.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Taggers");

            return true;
        } catch (Exception e){
            this.objective = null;
            this.scoreboard = null;
            return false;
        }
    }

    private void SendPointsMessage(Player player){
        //// HEADER
        StringBuilder message = new StringBuilder();
        message.append("&8───────────── $pluginColor&lTag &8─────────────\n");
        message.append("                           &e&lTop Five&r\n");
        playerManager.SendMessage(player, message.toString());

        //// MAIN
        int highestPointCount = 0;
        int playerPointCount = 0;
        boolean playerIsTopFive = false;

        // Get a list of players per point count
        TreeMap<Integer, ArrayList<TagPlayer>> pointList = new TreeMap<>();
        for (Player p : playerManager.GetAllPlayers()){
            // Loop through all players and sort them by point count
            TagPlayer tagPlayer = playerManager.GetTagPlayer(p);
            int points = tagPlayer.getPoints();
            // Edit highestPointCount if the player has a higher point count
            if (highestPointCount < points){
                highestPointCount = points;
            }
            // Check if a list with the same point count already exists
            ArrayList<TagPlayer> playerList = pointList.get(points);
            if (playerList == null){
                playerList = new ArrayList<>();
            }
            // Add player point count to the list
            playerList.add(tagPlayer);
            // Add the changed list back to the point list
            pointList.remove(points);
            pointList.put(points, playerList);
        }

        // Loop through all point count lists
        for (int i = highestPointCount; i > highestPointCount - 5; i--){
            message = new StringBuilder();
            // Point count cannot be lower than 0
            if (i >= 0) {
                // Get list per point count
                ArrayList<TagPlayer> tagPlayers = pointList.get(i);
                if (tagPlayers == null){
                    continue;
                }
                //// PERSON DEPENDANT
                for (TagPlayer tagPlayer : tagPlayers) {
                    if (tagPlayer.getPlayer().getName().equals(player.getName())) {
                        playerIsTopFive = true;
                        playerPointCount = tagPlayer.getPoints();
                        break;
                    }
                }
                // Create message if it fits on screen.
                if (tagPlayers.size() < 3) {
                    message.append("       ");
                    message.append(ChatColor.YELLOW);
                    message.append(i);
                    message.append(" pts &8» ");
                    for (TagPlayer tagPlayer : tagPlayers) {
                        message.append(ChatColor.WHITE);
                        message.append(tagPlayer.getPlayer().getName());
                        if (tagPlayer.getBonusPoints() > 0){
                            message.append(ChatColor.AQUA);
                            message.append(" (+");
                            message.append(tagPlayer.getBonusPoints());
                            message.append(")");
                        }
                        message.append(ChatColor.WHITE);
                        if (tagPlayers.indexOf(tagPlayer) != tagPlayers.size() - 1) {
                            message.append(", ");
                        }
                    }
                    playerManager.SendMessage(player, message.toString());
                } else {
                    TextComponent tc = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&f       &e" + i + " pts &8» &f" + tagPlayers.size() + " players"));
                    StringBuilder hover = new StringBuilder();
                    hover.append(ChatColor.GOLD);
                    hover.append("Players:\n");
                    for (TagPlayer tagPlayer : tagPlayers){
                        String playerName = tagPlayer.getPlayer().getName();
                        if (playerName.equals(player.getName())){
                            hover.append(ChatColor.YELLOW);
                        } else {
                            hover.append(ChatColor.WHITE);
                        }
                        hover.append(playerName);
                        hover.append(ChatColor.WHITE);
                        if (tagPlayers.indexOf(tagPlayer) != tagPlayers.size() - 1) {
                            hover.append(", ");
                        }
                    }
                    tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hover.toString())));
                    player.sendMessage(tc);
                }
            }
        }
        if (!playerIsTopFive){
            message = new StringBuilder();
            message.append(ChatColor.WHITE);
            message.append("       ...\n");
            message.append(ChatColor.YELLOW);
            message.append("       ");
            message.append(playerPointCount);
            message.append(" pts &8» &e");
            message.append(player.getName());
            playerManager.SendMessage(player, message.toString());
        }

        //// FOOTER
        message = new StringBuilder();
        message.append("\n");
        message.append("&8───────────── $pluginColor&lTag &8─────────────\n");
        playerManager.SendMessage(player, message.toString());
    }

    private int GetHighestPoints(){
        int highestPoints = 0;
        for (Player p : playerManager.GetAllPlayers()){
            TagPlayer tagPlayer = playerManager.GetTagPlayer(p);
            int points = tagPlayer.getPoints();
            if (highestPoints < points){
                highestPoints = points;
            }
        }
        return highestPoints;
    }

    public void SelectTaggers(){
        for (Player player : playerManager.GetAllPlayers()){
            this.scoreboard.resetScores(player.getName());
        }
        ArrayList<Player> runners = playerManager.GetPlayers(PlayerType.RUNNER);
        ArrayList<Player> taggers = playerManager.GetPlayers(PlayerType.TAGGER);

        StringBuilder message = new StringBuilder();
        message.append("$pluginPrefix");
        double amountToBecomeTagged = Math.ceil((float) runners.size() / 4f);
        for (int i = 0; i < amountToBecomeTagged; i++){
            Random rnd = new Random();
            int selected = rnd.nextInt(runners.size());
            Player player = runners.get(selected);
            while (taggers.contains(player)){
                selected = rnd.nextInt(runners.size());
                player = runners.get(selected);
            }

            Score score = this.objective.getScore(player.getName());
            score.setScore(i);

            taggers.add(player);
            playerManager.ChangePlayerType(player, PlayerType.TAGGER);
            playerManager.MakeTagger(player);

            message.append(ChatColor.RED);
            message.append(player.getName());
            message.append(ChatColor.WHITE);
            if (i != amountToBecomeTagged - 1){
                message.append(", ");
            } else if (i == amountToBecomeTagged - 2){
                message.append(" & ");
            }
        }
        message.append(ChatColor.WHITE);
        if (amountToBecomeTagged == 1){
            message.append(" is now a tagger!");
        }
        else {
            message.append(" are now taggers!");
        }
        playerManager.SendMessage(message.toString());
    }
}

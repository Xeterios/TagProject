package mu.xeterios.tag.tag;

import mu.xeterios.tag.Main;
import mu.xeterios.tag.config.Config;
import mu.xeterios.tag.config.Map;
import mu.xeterios.tag.tag.timer.TimerHandler;
import mu.xeterios.tag.tag.timer.TimerType;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.*;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class Tag {

    public final Main plugin;

    public final ArrayList<Player> allPlayers;
    public final ArrayList<Player> allPlayersAndSpectators;
    public final ArrayList<Player> taggers;
    public final ArrayList<Player> runners;

    public boolean started;

    public ArrayList<World> worlds;
    public Map map;
    public Scoreboard scoreboard;
    public Scoreboard saveOldScoreboard;
    public Objective objective;
    public final TimerHandler handler;
    public final EventsHandler eventsHandler;
    public PowerupHandler powerupHandler;
    public Config config;

    public int round = 0;

    public Tag(Main plugin){
        this.plugin = plugin;
        this.allPlayers = new ArrayList<>();
        this.allPlayersAndSpectators = new ArrayList<>();
        this.taggers = new ArrayList<>();
        this.runners = new ArrayList<>();
        this.worlds = new ArrayList<>();
        this.handler = new TimerHandler(this);
        this.eventsHandler = new EventsHandler(this);
    }

    public void SetConfig(Config config){
        this.config = config;
    }

    public void Start(){
        if (!started){
            plugin.getServer().getPluginManager().registerEvents(eventsHandler, this.plugin);
            for (Player p : allPlayers){
                runners.add(p);
                UnmakeTagger(p);
                p.getInventory().clear();
                Objects.requireNonNull(scoreboard.getTeam("Runners")).addEntry(p.getName());
                p.setScoreboard(scoreboard);
            }
            for(Player p : allPlayersAndSpectators){
                p.getInventory().clear();
                p.setScoreboard(scoreboard);
            }
            this.started = true;
            if (config.powerups){
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
            if (arg.equals("@a")){
                this.worlds.addAll(Bukkit.getWorlds());
            } else {
                this.worlds.add(Bukkit.getWorld(arg));
            }
            assert worlds.get(0) != null;
            if (arg2 != null){
                Map map = config.maps.get(arg2);
                if (map != null){
                    if (map.getSpawn() == null){
                        return false;
                    }
                    if (map.getMin() == null){
                        return false;
                    }
                    if (map.getMax() == null){
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
                    if (!toAdd.contains(p)){
                        toAdd.add(p);
                    }
                }
            }
            for(Player p : toAdd){
                if (!p.hasPermission("tag.exempt")) {
                    allPlayers.add(p);
                }
            }
            allPlayersAndSpectators.addAll(toAdd);

            if (!(allPlayers.size() > 1)){
                this.allPlayers.clear();
                this.allPlayersAndSpectators.clear();
                return false;
            }
            return true;
        }
        return false;
    }

    public boolean Stop(){
        if (started){
            if (runners.size() > 0){
                StringBuilder possibleWinners = new StringBuilder();
                for (int i = 0; i < taggers.size(); i++){
                    UnmakeTagger(taggers.get(i));
                    if (i == taggers.size()-1 && runners.size() == 0){
                        possibleWinners.append(taggers.get(i).getName());
                    } else {
                        possibleWinners.append(taggers.get(i).getName()).append(", ");
                    }
                }
                for(int i = 0; i < runners.size(); i++){
                    Objects.requireNonNull(scoreboard.getTeam("Runners")).removeEntry(runners.get(i).getName());
                    if (i == runners.size()-1){
                        possibleWinners.append(runners.get(i).getName());
                    } else {
                        possibleWinners.append(runners.get(i).getName()).append(", ");
                    }
                }
                if (runners.size() + taggers.size() > 1){
                    possibleWinners.append(" have won!");
                } else {
                    possibleWinners.append(" has won!");
                }
                for (Player p : allPlayersAndSpectators) {
                    p.sendMessage(config.pluginPrefix + possibleWinners);
                }
            }
            for (Player p : allPlayersAndSpectators){
                p.setScoreboard(saveOldScoreboard);
                p.getInventory().clear();
            }
            if (config.powerups){
                Bukkit.getScheduler().runTaskLater(this.plugin, this.powerupHandler::DespawnPowerups, 20L);
            }
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> HandlerList.unregisterAll(this.plugin), 20L);
            this.started = false;
            this.allPlayers.clear();
            this.allPlayersAndSpectators.clear();
            this.taggers.clear();
            this.runners.clear();
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

    public void SelectTaggers(){
        for (Player b : allPlayersAndSpectators){
            this.scoreboard.resetScores(b.getName());
        }
        double amountToBecomeTagged = Math.ceil((float) runners.size() / 4f);
        for (int i = 0; i < amountToBecomeTagged; i++){
            Random rnd = new Random();
            int selected = rnd.nextInt(runners.size());
            Player p = runners.get(selected);
            while (taggers.contains(p)){
                selected = rnd.nextInt(runners.size());
                p = runners.get(selected);
            }

            Score score = this.objective.getScore(p.getName());
            score.setScore(i);

            taggers.add(p);
            runners.remove(p);
            MakeTagger(p);
            for (Player b : allPlayersAndSpectators){
                b.sendMessage(config.pluginPrefix + ChatColor.RESET + p.getName() + ChatColor.RED + " is now a tagger.");
            }
        }
    }

    public void MakeTagger(Player p){
        Objects.requireNonNull(scoreboard.getTeam("Runners")).removeEntry(p.getName());
        Objects.requireNonNull(scoreboard.getTeam("Taggers")).addEntry(p.getName());
        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 1, false, false));

        ItemStack glass = new ItemStack(Material.RED_STAINED_GLASS, 1);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "TAGGER!!!");
        glass.setItemMeta(glassMeta);
        for (int j = 0; j < 9; j++){
            p.getInventory().setItem(j, glass);
        }
    }

    public void UnmakeTagger(Player p){
        Objects.requireNonNull(scoreboard.getTeam("Taggers")).removeEntry(p.getName());
        Objects.requireNonNull(scoreboard.getTeam("Runners")).addEntry(p.getName());
        p.removePotionEffect(PotionEffectType.GLOWING);
        p.removePotionEffect(PotionEffectType.SPEED);

        ItemStack air = new ItemStack(Material.AIR, 1);
        for (int j = 0; j < 9; j++){
            p.getInventory().setItem(j, air);
        }
    }

    public void EliminatePlayer(Player p){
        UnmakeTagger(p);
        Objects.requireNonNull(scoreboard.getTeam("Runners")).removeEntry(p.getName());
        p.setGameMode(GameMode.SPECTATOR);
        for (Player b : allPlayersAndSpectators){
            b.sendMessage(config.pluginPrefix + ChatColor.RESET + p.getName() + ChatColor.DARK_RED + " is eliminated.");
        }
        taggers.remove(p);
        allPlayers.remove(p);
        map.getSpawn().getWorld().spawnParticle(Particle.FIREWORKS_SPARK, p.getLocation().add(0, 1, 0), 50, 0.5, 0.5, 0.5, 0.1);
        map.getSpawn().getWorld().playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 3, 1);
        if (runners.size() == 1 || round == 10){
            this.Stop();
        }
    }
}

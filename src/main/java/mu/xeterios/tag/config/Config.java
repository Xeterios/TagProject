package mu.xeterios.tag.config;

import mu.xeterios.tag.Main;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map;

public class Config {

    private final Plugin plugin;
    private final Main main;
    public String pluginPrefix;
    public String pluginColor;
    public Dictionary<String, mu.xeterios.tag.config.Map> maps;
    public boolean powerups;
    public TreeMap<Double, String> powerupChances;
    private Map<String, Double> powerupConfig;

    public Config(Plugin plugin, Main main){
        this.plugin = plugin;
        this.main = main;
        LoadData();
    }

    public void LoadData(){
        pluginPrefix = ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("locale.prefix"));
        pluginColor = this.plugin.getConfig().getString("locale.color");
        powerups = plugin.getConfig().getBoolean("settings.powerups.enabled");

        Map<Double, String> chances = new HashMap<>();
        chances.put(plugin.getConfig().getDouble("settings.powerups.boost.chance"), "boost");
        chances.put(plugin.getConfig().getDouble("settings.powerups.sniper.chance"), "sniper");
        chances.put(plugin.getConfig().getDouble("settings.powerups.infrasight.chance"), "infrasight");
        chances.put(plugin.getConfig().getDouble("settings.powerups.chains.chance"), "chains");
        chances.put(plugin.getConfig().getDouble("settings.powerups.invisibility.chance"), "invisibility");
        this.powerupChances = new TreeMap<>();
        powerupChances.putAll(chances);

        this.powerupConfig = new HashMap<>();
        powerupConfig.put("boost", plugin.getConfig().getDouble("settings.powerups.boost.chance"));
        powerupConfig.put("sniper", plugin.getConfig().getDouble("settings.powerups.sniper.chance"));
        powerupConfig.put("infrasight", plugin.getConfig().getDouble("settings.powerups.infrasight.chance"));
        powerupConfig.put("chains", plugin.getConfig().getDouble("settings.powerups.chains.chance"));
        powerupConfig.put("invisibility", plugin.getConfig().getDouble("settings.powerups.invisibility.chance"));

/*        World world = Bukkit.getWorld(plugin.getConfig().get("settings.spawnpoint.world").toString());
        double spawnX = plugin.getConfig().getDouble("settings.spawnpoint.location.x");
        double spawnY = plugin.getConfig().getDouble("settings.spawnpoint.location.y");
        double spawnZ = plugin.getConfig().getDouble("settings.spawnpoint.location.z");
        float spawnYaw = (float) plugin.getConfig().getDouble("settings.spawnpoint.location.yaw");
        float spawnPitch = (float) plugin.getConfig().getDouble("settings.spawnpoint.location.pitch");
        this.location = new Location(world, spawnX, spawnY, spawnZ, spawnYaw, spawnPitch);

        World regionWorld = Bukkit.getWorld(plugin.getConfig().get("settings.region.world").toString());
        double pos1X = plugin.getConfig().getDouble("settings.region.pos1.x");
        double pos1Y = plugin.getConfig().getDouble("settings.region.pos1.y");
        double pos1Z = plugin.getConfig().getDouble("settings.region.pos1.z");
        this.minRegion = new Location(regionWorld, pos1X, pos1Y, pos1Z);

        double pos2X = plugin.getConfig().getDouble("settings.region.pos2.x");
        double pos2Y = plugin.getConfig().getDouble("settings.region.pos2.y");
        double pos2Z = plugin.getConfig().getDouble("settings.region.pos2.z");
        this.maxRegion = new Location(regionWorld, pos2X, pos2Y, pos2Z);*/

        this.maps = new Hashtable<>();

        // SETTINGS PER REGION
        File fl = new File(Main.getPlugin(Main.class).getDataFolder() + File.separator + "maps");
        File[] files = fl.listFiles();
        if (files != null){
            for (File child : files){
                FileConfiguration config = new YamlConfiguration();
                try {
                    config.load(child);
                } catch (IOException | InvalidConfigurationException e) {
                    e.printStackTrace();
                }
                for (String string : config.getKeys(false)){
                    mu.xeterios.tag.config.Map map = config.getObject(string, mu.xeterios.tag.config.Map.class);
                    assert map != null;
                    this.maps.put(map.getName(), map);
                }
            }
        }
    }

    public void EditSpawn(Location spawn, mu.xeterios.tag.config.Map map){
        map.setSpawn(spawn);
    }

    public boolean EditRegion(Location pos1, Location pos2, mu.xeterios.tag.config.Map map){
        try {
            map.setRegions(pos1, pos2);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public void CreateMap(String name){
        mu.xeterios.tag.config.Map map = new mu.xeterios.tag.config.Map();
        map.setName(name);
        maps.put(name, map);
    }

    public mu.xeterios.tag.config.Map GetMap(String name){
        return maps.get(name);
    }

    public void SaveConfig(){
        this.plugin.getConfig().set("settings.powerups.enabled", this.powerups);
        this.plugin.getConfig().set("settings.powerups.boost.chance", this.powerupConfig.get("boost"));
        this.plugin.getConfig().set("settings.powerups.sniper.chance", this.powerupConfig.get("sniper"));
        this.plugin.getConfig().set("settings.powerups.infrasight.chance", this.powerupConfig.get("infrasight"));
        this.plugin.getConfig().set("settings.powerups.chains.chance", this.powerupConfig.get("chains"));
        this.plugin.getConfig().set("settings.powerups.invisibility.chance", this.powerupConfig.get("invisibility"));

        this.plugin.getConfig().set("locale.prefix", this.pluginPrefix);
        this.plugin.getConfig().set("locale.color", this.pluginColor);

        this.plugin.saveConfig();
        saveCustomConfig();
    }

    public void saveCustomConfig(){
        // Iterate through all maps
        for (Iterator<String> it = this.maps.keys().asIterator(); it.hasNext(); ) {
            String info = it.next();
            // Retrieve map from data
            mu.xeterios.tag.config.Map map = this.maps.get(info);
            // Create new file
            File fl = new File(Main.getPlugin(Main.class).getDataFolder() + File.separator + "maps", map.getName() + ".yml");
            FileConfiguration fc = new YamlConfiguration();
            // Set config content to serialized map data
            fc.set(fl.getName().replace(".yml", ""), map);
            try {
                // Save file
                fc.save(fl);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void ReloadConfig(){
        this.plugin.reloadConfig();
        LoadData();
    }
}

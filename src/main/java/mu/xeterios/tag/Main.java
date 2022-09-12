package mu.xeterios.tag;

import mu.xeterios.tag.commands.CommandHandler;
import mu.xeterios.tag.config.Config;
import mu.xeterios.tag.config.Map;
import mu.xeterios.tag.tag.Tag;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Main extends JavaPlugin {

    private Tag tag;
    private Config config;

    public File customConfigFile;
    public FileConfiguration customConfig;

    private static final Logger log = Logger.getLogger("Minecraft");

    @Override
    public void onLoad() {
        // Plugin startup logic
        ConfigurationSerialization.registerClass(Map.class, "Map");
    }


    @Override
    public void onEnable() {
        // Plugin startup logic
        createCustomConfig();
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        this.config = new Config(this);
        this.tag = new Tag(this);
        this.getCommand("tag").setExecutor(new CommandHandler(this, config));
        config.saveCustomConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        this.tag.Stop();
    }

    private void createCustomConfig() {
        customConfigFile = new File(getDataFolder() + File.separator + "maps");
        if (!customConfigFile.exists()) {
            boolean success = customConfigFile.mkdirs();
            if (!success) {
                log.severe("[%s] Could not create maps folder.");
            }
        }
    }

    public FileConfiguration getCustomConfig(){
        return customConfig;
    }

    public Tag getTag(){
        return this.tag;
    }

    public void setTag(Tag tag){
        this.tag = tag;
    }
}

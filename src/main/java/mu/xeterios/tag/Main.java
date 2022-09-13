package mu.xeterios.tag;

import lombok.Getter;
import mu.xeterios.tag.commands.CommandHandler;
import mu.xeterios.tag.config.Config;
import mu.xeterios.tag.config.Map;
import mu.xeterios.tag.tag.PlayerHandler;
import mu.xeterios.tag.tag.Tag;
import mu.xeterios.tag.tag.players.PlayerData;
import mu.xeterios.tag.tag.players.PlayerDataHandler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public final class Main extends JavaPlugin {

    private Tag tag;

    @Getter private PlayerDataHandler playerDataHandler;
    private PlayerHandler playerHandler;
    private Config config;

    public File customConfigFile;
    public FileConfiguration customConfig;

    private static final Logger log = Logger.getLogger("Minecraft");

    @Override
    public void onLoad() {
        // Plugin startup logic
        ConfigurationSerialization.registerClass(Map.class, "Map");
        ConfigurationSerialization.registerClass(PlayerData.class, "PlayerData");
    }


    @Override
    public void onEnable() {
        // Plugin startup logic
        createCustomConfig();
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        this.config = new Config(this);
        this.playerDataHandler = new PlayerDataHandler(config);
        this.playerHandler = new PlayerHandler(config, playerDataHandler);
        this.config.SetPlayerDataHandler(playerDataHandler);
        this.tag = new Tag(this, playerDataHandler);
        Bukkit.getPluginManager().registerEvents(playerHandler, Main.getPlugin(Main.class));
        CommandHandler handler = new CommandHandler(this, config);
        this.getCommand("tag").setExecutor(handler);
        this.getCommand("profile").setExecutor(handler);
        config.SaveMaps();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        this.config.SavePlayer(playerDataHandler.getAllPlayerData());
        this.tag.Stop();
        HandlerList.unregisterAll();
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

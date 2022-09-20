package mu.xeterios.tag.tag.powerup;

import lombok.Getter;
import mu.xeterios.tag.Main;
import mu.xeterios.tag.config.Config;
import mu.xeterios.tag.config.Region;
import mu.xeterios.tag.tag.PowerupHandler;
import mu.xeterios.tag.tag.Tag;
import mu.xeterios.tag.tag.powerup.powerups.Powerup;
import mu.xeterios.tag.tag.powerup.powerups.Shuffle;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Light;
import org.bukkit.entity.*;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class PowerupSpawner {

    private final Tag tag;
    private final Region region;
    private final PowerupHandler handler;
    private final PowerupFactory factory;

    @Getter private Timer powerUpTimer;

    public PowerupSpawner(PowerupHandler handler, Tag tag){
        this.factory = new PowerupFactory();
        this.handler = handler;
        this.tag = tag;
        this.region = new Region(tag.getMap());
    }

    public void RunTimer(int delay) {
        if (this.powerUpTimer != null){
            this.powerUpTimer.cancel();
        }
        this.powerUpTimer = new Timer();
        TimerTask task = new PowerupTimer(handler, tag, delay);
        powerUpTimer.schedule(task, 0, 1000);
    }

    public void Spawn(){
        Random rnd = new Random();
        double itemToSpawnNext = rnd.nextDouble() * 100;
        Powerup powerup = factory.GetPowerup(itemToSpawnNext);
        Location location = region.getRandomLocation();
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
            Item item = location.getWorld().dropItem(location, powerup.GetItem());
            item.setVelocity(new Vector(0, 0, 0));
            item.setGravity(false);
        });
        handler.getPowerupLocations().add(location);
        handler.getPowerups().put(location, powerup);
        //DEBUG
        /*for (Player p : tag.allPlayers){
            p.sendMessage(tag.config.pluginPrefix + "A " + powerup.getClass().getSimpleName() + " powerup spawned at: " + ChatColor.RESET + location.getX() + " " + location.getY() + " " + location.getZ() + ChatColor.translateAlternateColorCodes('&', tag.config.pluginColor) + "!");
        }*/

        // Spawn firework
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
            Location fireworkLocation = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());
            fireworkLocation.add(0, 1, 0);
            Firework fw = (Firework) location.getWorld().spawnEntity(fireworkLocation, EntityType.FIREWORK);
            FireworkMeta fwm = fw.getFireworkMeta();
            fwm.setPower(3);
            fwm.addEffect(FireworkEffect.builder().withColor(powerup.GetPowerupColor()).flicker(true).build());
            fw.setFireworkMeta(fwm);
            Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), fw::detonate, 28L);
        });

        // Spawn particles
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(Main.getPlugin(Main.class), () -> {
            Particle.DustOptions options = new Particle.DustOptions(powerup.GetPowerupColor(), 2);
            location.getWorld().spawnParticle(Particle.REDSTONE, new Location(location.getWorld(), location.getX(), location.getY()-0.6, location.getZ()), 1, 1, 1, 1, options);
            location.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, new Location(location.getWorld(), location.getX(), location.getY()-0.6, location.getZ()), 1, 1, 1, 1, 0);
        }, 0, (long) 3.5);

        handler.getEffects().put(location, task);

        // Spawn hologram
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
            ArmorStand hologram = (ArmorStand) location.getWorld().spawnEntity(new Location(location.getWorld(), location.getX(), location.getY()-1.25, location.getZ()), EntityType.ARMOR_STAND);
            hologram.setGravity(false);
            hologram.setCanPickupItems(false);
            hologram.setCanMove(false);
            hologram.setVisible(false);
            hologram.setCustomName(powerup.GetHologramName());
            hologram.setCustomNameVisible(true);
        });

        // Send message in case of shuffle
        if (powerup instanceof Shuffle){
            tag.getPlayerManager().SendMessage("$pluginPrefix&5A &lSHUFFLE &5has spawned!");
            for (Player player : tag.getPlayerManager().GetAllPlayers()){
                player.playSound(player, Sound.ITEM_TRIDENT_THUNDER, 10, 2);
            }
        }
    }
}

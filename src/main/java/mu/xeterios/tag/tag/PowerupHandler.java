package mu.xeterios.tag.tag;

import mu.xeterios.tag.Main;
import mu.xeterios.tag.tag.powerup.PowerupFactory;
import mu.xeterios.tag.tag.powerup.PowerupSpawner;
import mu.xeterios.tag.tag.powerup.powerups.Powerup;
import mu.xeterios.tag.tag.powerup.powerups.Sniper;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class PowerupHandler implements Listener {

    public PowerupSpawner spawner;
    public ArrayList<Location> powerupLocations;
    public Dictionary<Location, Powerup> powerups;
    public Map<Location, BukkitTask> effects;
    private final ArrayList<Projectile> arrows;
    private final Tag tag;
    private Player shooter;
    public boolean infraSightActive = false;

    public PowerupHandler(Tag tag){
        this.tag = tag;
        this.arrows = new ArrayList<>();
        this.powerupLocations = new ArrayList<>();
        this.powerups = new Hashtable<>();
        this.effects = new HashMap<>();
        spawner = new PowerupSpawner(this, tag);
        spawner.RunTimer(35);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), () -> {
            for (Projectile arrow : arrows){
                Location location = arrow.getLocation();

                Particle.DustOptions options = new Particle.DustOptions(Color.RED, 5);
                location.getWorld().spawnParticle(Particle.REDSTONE, location, 1, 0, 0, 0, options);
            }
        }, 0, 0);
    }

    @EventHandler
    public void PickupPowerup(EntityPickupItemEvent e){
        if (e.getEntity() instanceof Player) {
            PowerupFactory factory = new PowerupFactory();
            Powerup powerup = factory.GetPowerup(e.getItem().getItemStack().getType());
            powerup.Trigger((Player) e.getEntity(), this);
            Location location = e.getItem().getLocation();
            for (Location location2 : powerupLocations){
                if (location2.getX() == location.getX() && location2.getY() == location.getY() && location2.getZ() == location.getZ()){
                    effects.get(location2).cancel();
                    for (Entity entity : location2.getWorld().getNearbyEntities(location, 3, 3, 3)){
                        if (!(entity instanceof Player)){
                            entity.remove();
                            location2.getBlock().setType(Material.AIR);
                        }
                    }
                }
            }
            this.powerupLocations.remove(location);
            this.powerups.remove(location);
            e.getItem().remove();
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void ShootSniperBow(EntityShootBowEvent e){
        if (e.getEntity() instanceof Player){
            ((Player) e.getEntity()).getInventory().setItemInOffHand(new ItemStack(Material.AIR, 1));
            ((Player) e.getEntity()).getInventory().remove(Sniper.GetBow());
            this.arrows.add((Projectile) e.getProjectile());
            this.shooter = (Player) e.getEntity();
        }
    }

    @EventHandler
    public void ArrowHit(ProjectileHitEvent e){
        if (e.getEntity() instanceof Arrow){
            if (e.getHitEntity() instanceof Player){
                if (tag.eventsHandler.IsPlayerATagger(shooter) && !tag.eventsHandler.IsPlayerATagger((Player) e.getHitEntity())){
                    tag.eventsHandler.SwapTaggerRunner(shooter, (Player) e.getHitEntity());
                    shooter.playSound(shooter.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 2);
                    ((Player) e.getHitEntity()).playSound(e.getHitEntity().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 2);
                    for (Player p : tag.allPlayers){
                        p.sendMessage(tag.config.pluginPrefix + ChatColor.translateAlternateColorCodes('&', this.tag.config.pluginColor) + shooter.getName() + ChatColor.RESET + " has SNIPED "+ ChatColor.translateAlternateColorCodes('&', this.tag.config.pluginColor) + e.getHitEntity().getName() + ChatColor.RESET + " and is now tagged!");
                    }
                }
            }
            e.getEntity().getLocation();
            e.getEntity().remove();
            this.arrows.remove(e.getEntity());
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void PreventPotionBlocking(EntityPotionEffectEvent e){
    }

    @EventHandler
    public void PreventHologramInteraction(PlayerArmorStandManipulateEvent e){
        e.setCancelled(true);
    }

    @EventHandler
    public void PreventFallDamage(EntityDamageEvent e){
        if (e.getEntity() instanceof Player){
            if (e.getCause().equals(EntityDamageEvent.DamageCause.FALL)){
                e.setCancelled(true);
            }
        }
    }

    public void DespawnPowerups(){
        for (Location location : powerupLocations){
            for (Entity entity : location.getWorld().getNearbyEntities(location, 3, 3, 3)){
                if (!(entity instanceof Player)){
                    entity.remove();
                }
            }
        }
        for (BukkitTask task : effects.values()){
            task.cancel();
        }
    }
}

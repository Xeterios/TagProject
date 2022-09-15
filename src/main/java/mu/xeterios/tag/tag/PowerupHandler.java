package mu.xeterios.tag.tag;

import lombok.Getter;
import mu.xeterios.tag.Main;
import mu.xeterios.tag.tag.players.PlayerManager;
import mu.xeterios.tag.tag.players.PlayerType;
import mu.xeterios.tag.tag.players.TagPlayer;
import mu.xeterios.tag.tag.powerup.PowerupFactory;
import mu.xeterios.tag.tag.powerup.PowerupSpawner;
import mu.xeterios.tag.tag.powerup.powerups.InfraSight;
import mu.xeterios.tag.tag.powerup.powerups.Powerup;
import mu.xeterios.tag.tag.powerup.powerups.Shuffle;
import mu.xeterios.tag.tag.powerup.powerups.Sniper;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class PowerupHandler implements Listener {

    @Getter private final PowerupSpawner spawner;
    @Getter private final ArrayList<Location> powerupLocations;
    @Getter private final Dictionary<Location, Powerup> powerups;
    @Getter private final Map<Location, BukkitTask> effects;
    @Getter private final PlayerManager playerManager;

    private final ArrayList<Projectile> arrows;

    public PowerupHandler(Tag tag){
        this.playerManager = tag.getPlayerManager();
        this.arrows = new ArrayList<>();
        this.powerupLocations = new ArrayList<>();
        this.powerups = new Hashtable<>();
        this.effects = new HashMap<>();
        this.spawner = new PowerupSpawner(this, tag);
        this.spawner.RunTimer(35);

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
        LivingEntity livingEntity = e.getEntity();
        if (livingEntity instanceof Player) {
            PowerupFactory factory = new PowerupFactory();
            Powerup powerup = factory.GetPowerup(e.getItem().getItemStack().getType());
            powerup.Trigger((Player) livingEntity, this);
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
            if (powerup instanceof Shuffle){
                for (Player player : playerManager.GetAllPlayers()){
                    player.playSound(player, Sound.ENTITY_WITHER_SPAWN, 10, 2);
                }
            } else if (powerup instanceof InfraSight){
                for (Player player : playerManager.GetAllPlayers()){
                    player.playSound(player, Sound.BLOCK_BEACON_ACTIVATE, 10, 2);
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
        if (e.getEntity() instanceof Player shooter){
            shooter.getInventory().setItemInOffHand(new ItemStack(Material.AIR, 1));
            shooter.getInventory().remove(Sniper.GetBow());
            this.arrows.add((Projectile) e.getProjectile());
        }
    }

    @EventHandler
    public void ArrowHit(ProjectileHitEvent e){
        Projectile entity = e.getEntity();
        if (entity instanceof Arrow){
            if (e.getHitEntity() instanceof Player hit && entity.getShooter() instanceof Player shooter){
                if (playerManager.GetTagPlayer(shooter).getType().equals(PlayerType.TAGGER) && !playerManager.GetTagPlayer(hit).getType().equals(PlayerType.TAGGER)){
                    playerManager.SwapTagger(shooter, (Player) e.getHitEntity());

                    shooter.playSound(shooter.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 2);
                    hit.playSound(e.getHitEntity().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 2);

                    String message = "$pluginPrefix" + ChatColor.GREEN + shooter.getName() + ChatColor.RESET + " has SNIPED " + ChatColor.RED + hit.getName() + ChatColor.RESET + " and is now tagged!";
                    playerManager.SendMessage(message);

                    double distanceBetween = shooter.getLocation().distance(hit.getLocation());
                    int requiredDistance = 20;
                    if (distanceBetween >= requiredDistance){
                        String extra = "$pluginPrefix&bYou sniped &c" + hit.getName() + "&f from over &3&n" + requiredDistance + " meters&b and gained an extra point!";
                        playerManager.GetTagPlayer(shooter).AddBonusPoints(1);
                        playerManager.SendMessage(shooter, extra);
                    }

                }
            }
            this.arrows.remove(entity);
            entity.remove();
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

    @EventHandler
    public void OnPotionHit(PotionSplashEvent e){
        ArrayList<PotionEffectType> types = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        PotionMeta meta = e.getPotion().getPotionMeta();
        for(PotionEffect effects : meta.getCustomEffects()){
            types.add(effects.getType());
        }

        if (types.contains(PotionEffectType.BLINDNESS) && types.contains(PotionEffectType.SLOW)){
            sb.append(ChatColor.DARK_GRAY);
            sb.append(ChatColor.BOLD);
            sb.append("CHAINED");
        }

        ThrownPotion potion = e.getPotion();
        ProjectileSource source = potion.getShooter();
        if (source instanceof Player player){
            // Create message
            StringBuilder message = new StringBuilder();
            message.append("$pluginPrefix&fYou have &8&lCHAINED&r ");
            Collection<LivingEntity> affectedEntities = e.getAffectedEntities();
            if (affectedEntities.size() < 4){
                for(LivingEntity entity : e.getAffectedEntities()){
                    if (entity instanceof Player p){
                        TagPlayer tagPlayer = playerManager.GetTagPlayer(p);
                        PlayerType type = tagPlayer.getType();
                        String playerName = tagPlayer.getPlayer().getName();
                        if (type.equals(PlayerType.TAGGER)){
                            message.append("&c");
                            message.append(playerName);
                        } else if (type.equals(PlayerType.RUNNER)) {
                            message.append("&a");
                            message.append(playerName);
                        }
                        ArrayList<LivingEntity> affected = new ArrayList<>(){{addAll(e.getAffectedEntities());}};
                        int index = affected.indexOf(entity);
                        if (index != affected.size() - 1){
                            message.append("&f, ");
                        }
                    }
                }
            } else {
                message.append(affectedEntities.size());
                message.append(" players");
            }
            // Give effects and play title
            for(LivingEntity entity : e.getAffectedEntities()){
                if (entity instanceof Player p) {
                    p.sendTitle(sb.toString(), "", 10, 20, 10);
                    p.playSound(p.getLocation(), Sound.ENTITY_ALLAY_HURT, 15, 0);
                }
            }
            message.append("&f!");
            playerManager.SendMessage(player, message.toString());
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

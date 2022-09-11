package mu.xeterios.tag.tag;

import mu.xeterios.tag.commands.command.Start;
import mu.xeterios.tag.tag.timer.StartupTimer;
import mu.xeterios.tag.tag.timer.TimerHandler;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Score;

import java.util.ArrayList;
import java.util.Objects;

public class EventsHandler implements Listener {

    private final Tag tag;

    public EventsHandler(Tag tag){
        this.tag = tag;
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e){
        tag.runners.remove(e.getPlayer());
        tag.taggers.remove(e.getPlayer());
        tag.allPlayers.remove(e.getPlayer());
        tag.allPlayersAndSpectators.remove(e.getPlayer());
        tag.scoreboard.resetScores(e.getPlayer().getName());
        for (Player p : tag.allPlayersAndSpectators){
            p.sendMessage(tag.config.pluginPrefix + ChatColor.RESET + e.getPlayer().getName() + ChatColor.DARK_RED + " is eliminated, because they left the game.");
        }
        if (((tag.runners.size() == 0 || tag.taggers.size() == 0) && !(tag.handler.activeTask() instanceof StartupTimer)) || tag.allPlayers.size() == 0){
            for (Player p : tag.allPlayersAndSpectators){
                p.sendMessage(tag.config.pluginPrefix + "All taggers or runners left the game, so it was stopped.");
            }
            tag.Stop();
            tag.EliminatePlayer(e.getPlayer());
        }
        e.setQuitMessage("");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        if (tag.handler.activeTask() instanceof StartupTimer){
            tag.allPlayers.add(e.getPlayer());
            tag.allPlayersAndSpectators.add(e.getPlayer());
            e.getPlayer().teleport(tag.map.getSpawn());
        } else {
            if (tag.worlds.contains(e.getPlayer().getWorld())) {
                e.getPlayer().teleport(tag.map.getSpawn());
                e.getPlayer().setGameMode(GameMode.SPECTATOR);
                e.getPlayer().sendMessage(this.tag.config.pluginPrefix + ChatColor.translateAlternateColorCodes('&', this.tag.config.pluginColor) + "Tag is currently started, so you are a spectator now.");
            }
        }
    }

    @EventHandler
    public void DropItem(PlayerDropItemEvent e){
        e.setCancelled(true);
    }

    @EventHandler
    public void PreventInventoryInteract(InventoryInteractEvent e){
        e.setCancelled(true);
    }

    @EventHandler
    public void PreventInventoryItemMove(InventoryMoveItemEvent e){
        e.setCancelled(true);
    }

    @EventHandler
    public void PreventInventoryClicking(InventoryClickEvent e){
        e.setCancelled(true);
    }

    @EventHandler
    public void OnTag(EntityDamageByEntityEvent e){
        if (e.getDamager() instanceof Player damager && e.getEntity() instanceof Player receiver){
            if (IsPlayerATagger(damager) && !IsPlayerATagger(receiver)){
                SwapTaggerRunner(damager, receiver);
                for (Player p : tag.allPlayersAndSpectators){
                    p.sendMessage(tag.config.pluginPrefix + ChatColor.GREEN + damager.getName() + ChatColor.RESET + " has tagged "+ ChatColor.RED + receiver.getName() + ChatColor.RESET + "!");
                }
            } else {
                e.setCancelled(true);
            }
        }
        if (e.getDamager() instanceof Firework) {
            e.setCancelled(true);
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
        // CHAINS
        if (types.contains(PotionEffectType.BLINDNESS) && types.contains(PotionEffectType.SLOW)){
            sb.append(ChatColor.DARK_GRAY);
            sb.append(ChatColor.BOLD);
            sb.append("CHAINED");
        }

        for(LivingEntity entity : e.getAffectedEntities()){
            if (entity instanceof Player p){
                p.sendTitle(sb.toString(), "", 10, 20, 10);
                p.playSound(p.getLocation(), Sound.ENTITY_ALLAY_HURT, 15, 0);
            }
        }
    }

    public void SwapTaggerRunner(Player damager, Player receiver){
        tag.UnmakeTagger(damager);
        tag.taggers.remove(damager);
        tag.runners.add(damager);
        tag.MakeTagger(receiver);
        tag.runners.remove(receiver);
        tag.taggers.add(receiver);

        Score scoreOne = tag.objective.getScore(damager.getName());
        Score scoreTwo = tag.objective.getScore(receiver.getName());
        scoreTwo.setScore(scoreOne.getScore());
        this.tag.scoreboard.resetScores(damager.getName());

        if (tag.powerupHandler.infraSightActive){
            PotionEffect effect = new PotionEffect(PotionEffectType.GLOWING, Objects.requireNonNull(receiver.getPotionEffect(PotionEffectType.GLOWING)).getDuration(), 0, true, true, true);
            receiver.addPotionEffect(effect);
        }
    }

    public boolean IsPlayerATagger(Player p){
        for (Player player : tag.taggers){
            if (player == p){
                return true;
            }
        }
        return false;
    }
}

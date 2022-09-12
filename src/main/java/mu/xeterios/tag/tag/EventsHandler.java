package mu.xeterios.tag.tag;

import mu.xeterios.tag.tag.players.PlayerManager;
import mu.xeterios.tag.tag.players.PlayerType;
import mu.xeterios.tag.tag.players.TagPlayer;
import mu.xeterios.tag.tag.timer.StartupTimer;
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

import java.util.ArrayList;

public class EventsHandler implements Listener {

    private final Tag tag;
    private final PlayerManager playerManager;

    public EventsHandler(Tag tag){
        this.tag = tag;
        this.playerManager = tag.getPlayerManager();
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e){
        Player player = e.getPlayer();
        playerManager.RemovePlayer(player);
        tag.getScoreboard().resetScores(player.getName());
        playerManager.SendMessage("$pluginPrefix" + ChatColor.RESET + player.getName() + ChatColor.DARK_RED + " is eliminated, because they left the game.");
        playerManager.MakeRunner(player);
        boolean stopGame = false;
        StringBuilder message = new StringBuilder();
        message.append("$pluginPrefix");
        if ((playerManager.GetPlayers(PlayerType.RUNNER).size() == 0 || playerManager.GetPlayers(PlayerType.TAGGER).size() == 0) && !(tag.getHandler().activeTask() instanceof StartupTimer)){
            message.append("All taggers or runners left the game, so it was stopped.");
            stopGame = true;
        } else if (playerManager.GetPlayers(new PlayerType[]{ PlayerType.RUNNER, PlayerType.TAGGER }).size() < 2 && tag.getHandler().activeTask() instanceof StartupTimer) {
            message.append("There are not enough people to play tag, so it was stopped.");
            stopGame = true;
        }
        if (stopGame){
            playerManager.SendMessage(message.toString());
            tag.Stop();
        }
        e.setQuitMessage("");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        if (tag.getHandler().activeTask() instanceof StartupTimer){
            playerManager.AddPlayer(player);
            playerManager.ChangePlayerType(player, PlayerType.RUNNER);
            player.teleport(tag.getMap().getSpawn());
        } else {
            if (tag.getWorlds().contains(player.getWorld())) {
                playerManager.AddPlayer(player);
                playerManager.ChangePlayerType(player, PlayerType.SPECTATOR);
                player.teleport(tag.getMap().getSpawn());
                player.setGameMode(GameMode.SPECTATOR);
                String message = "$pluginPrefix" + "$pluginColor" + "Tag is currently started, so you are a spectator now.";
                playerManager.SendMessage(player, message);
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
            if (playerManager.GetTagPlayer(damager).getType().equals(PlayerType.TAGGER) && !playerManager.GetTagPlayer(receiver).getType().equals(PlayerType.TAGGER)){
                playerManager.SwapTagger(damager, receiver);
                playerManager.SendMessage("$pluginPrefix" + ChatColor.GREEN + damager.getName() + ChatColor.RESET + " has tagged " + ChatColor.RED + receiver.getName() + ChatColor.RESET + "!");
            } else {
                e.setCancelled(true);
            }
        }
        if (e.getDamager() instanceof Firework) {
            e.setCancelled(true);
        }
    }
}

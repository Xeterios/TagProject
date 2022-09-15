package mu.xeterios.tag.tag.inventories;

import mu.xeterios.tag.config.Config;
import mu.xeterios.tag.tag.players.PlayerData;
import mu.xeterios.tag.tag.players.PlayerDataHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ProfileInventory implements Listener {

    private final Inventory inventory;
    private final PlayerDataHandler playerDataHandler;
    private final PlayerData playerData;
    private final OfflinePlayer target;

    public ProfileInventory(Player player, OfflinePlayer target, Config config){
        this.target = target;
        this.playerDataHandler = config.getPlayerDataHandler();
        this.playerData = playerDataHandler.GetPlayer(target.getUniqueId());
        this.inventory = Bukkit.createInventory(player, 27, ChatColor.translateAlternateColorCodes('&', "&9" + target.getName() + "'s Profile"));

        SetupInventory();
        player.openInventory(inventory);
    }

    private void SetupInventory(){
        ItemStack playerHead = PlayerData.getPlayerHead(target, playerData, playerDataHandler, null);
        inventory.setItem(13, playerHead);
    }

    @EventHandler
    public void OnInventoryClick(InventoryClickEvent e){
        if (e.getView().getTopInventory().equals(inventory)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void OnInventoryClose(InventoryCloseEvent e){
        if (e.getView().getTopInventory().equals(inventory)) {
            HandlerList.unregisterAll(this);
        }
    }
}

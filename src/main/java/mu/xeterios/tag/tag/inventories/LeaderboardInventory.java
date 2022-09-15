package mu.xeterios.tag.tag.inventories;

import mu.xeterios.tag.config.Config;
import mu.xeterios.tag.tag.players.PlayerData;
import mu.xeterios.tag.tag.players.PlayerDataHandler;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardInventory implements Listener {

    private Inventory inventory;
    private final PlayerDataHandler playerDataHandler;
    private LeaderboardType sortingType;
    private int page;

    public LeaderboardInventory(Player player,  Config config){
        this.playerDataHandler = config.getPlayerDataHandler();
        this.page = 0;
        this.sortingType = LeaderboardType.Wins;

        OpenInventory(player);
    }

    private void OpenInventory(Player player){
        this.inventory = Bukkit.createInventory(player, 54, ChatColor.translateAlternateColorCodes('&', "&9&lLeaderboard" + " &3(Page " + (page + 1) + ")"));
        LoadInventory();
        player.openInventory(inventory);
    }

    private void LoadInventory(){
        ArrayList<PlayerData> data = SetupInventory();
        FillInventory(data);
    }

    private ArrayList<PlayerData> SetupInventory(){
        ArrayList<PlayerData> loadedData = new ArrayList<>();
        switch (sortingType){
            case Points -> loadedData = playerDataHandler.GetLeaderboard(LeaderboardType.Points);
            case Wins -> loadedData = playerDataHandler.GetLeaderboard(LeaderboardType.Wins);
            case Winstreak -> loadedData = playerDataHandler.GetLeaderboard(LeaderboardType.Winstreak);
        }

        // Fill player database for this page
        ArrayList<PlayerData> playerDataList = new ArrayList<>();
        for(int i = page * 36; i < (page + 1) * 36; i++){
            if (i < playerDataHandler.getAllPlayerData().size()){
                playerDataList.add(loadedData.get(i));
            }
        }
        // Set glass
        ItemStack glass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.displayName(Component.text(ChatColor.translateAlternateColorCodes('&', "&f")));
        glass.setItemMeta(glassMeta);
        for(int i = 36; i < 45; i++){
            inventory.setItem(i, glass);
        }
        // Set sorter item
        ItemStack sorter = new ItemStack(Material.SUNFLOWER, 1);
        ItemMeta sorterMeta = sorter.getItemMeta();
        String color = "";
        switch (sortingType){
            case Points -> color = "&b";
            case Wins -> color = "&6";
            case Winstreak -> color = "&a";
        }
        sorterMeta.displayName(Component.text(ChatColor.translateAlternateColorCodes('&', "&f&lSorting by: " + color + "&l" + sortingType)));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "&7Click to change the sorting method")));
        sorterMeta.lore(lore);
        sorter.setItemMeta(sorterMeta);
        inventory.setItem(49, sorter);

        // Set previous page item
        if (page > 0){
            ItemStack previousPage = new ItemStack(Material.PAPER, 1);
            ItemMeta previousPageMeta = previousPage.getItemMeta();
            previousPageMeta.displayName(Component.text(ChatColor.translateAlternateColorCodes('&', "&e&lPrevious Page")));
            previousPage.setItemMeta(previousPageMeta);
            inventory.setItem(48, previousPage);
        }

        // Set next page item
        if ((page + 1) * 36 < playerDataList.size()){
            ItemStack nextPage = new ItemStack(Material.PAPER, 1);
            ItemMeta nextPageMeta = nextPage.getItemMeta();
            nextPageMeta.displayName(Component.text(ChatColor.translateAlternateColorCodes('&', "&e&lNext Page")));
            nextPage.setItemMeta(nextPageMeta);
            inventory.setItem(50, nextPage);
        }

        return playerDataList;
    }

    private void FillInventory(ArrayList<PlayerData> playerDataList){
        for(int i = 0; i < playerDataList.size(); i++){
            PlayerData playerData = playerDataList.get(i);
            OfflinePlayer target = playerDataHandler.GetOfflinePlayer(playerData.getUuid());
            ItemStack playerHead = PlayerData.getPlayerHead(target, playerData, playerDataHandler, sortingType);
            inventory.setItem(i, playerHead);
        }
    }

    @EventHandler
    public void OnInventoryClick(InventoryClickEvent e){
        if (e.getView().getTopInventory().equals(inventory)) {
            Player player = (Player) e.getWhoClicked();
            ItemStack itemStack = e.getCurrentItem();
            if (itemStack != null){
                ItemMeta itemMeta = itemStack.getItemMeta();
                switch (itemStack.getType()){
                    case PAPER -> {
                        if (itemMeta.hasDisplayName()){
                            String itemName = itemMeta.getDisplayName();
                            if (itemName.contains("Next")){
                                page++;
                                OpenInventory(player);
                            } else if (itemName.contains("Previous")) {
                                if (page > 0){
                                    page--;
                                    OpenInventory(player);
                                }
                            }
                        }
                    }
                    case SUNFLOWER -> {
                        LeaderboardType[] types = LeaderboardType.values();
                        if (sortingType.ordinal() + 1 == types.length){
                            sortingType = types[0];
                        } else {
                            sortingType = types[sortingType.ordinal() + 1];
                        }
                        LoadInventory();
                    }
                }
            }
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

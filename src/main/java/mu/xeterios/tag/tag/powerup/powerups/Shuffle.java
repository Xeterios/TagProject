package mu.xeterios.tag.tag.powerup.powerups;

import mu.xeterios.tag.tag.PowerupHandler;
import mu.xeterios.tag.tag.players.PlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class Shuffle implements Powerup {

    @Override
    public ItemStack GetItem() {
        ItemStack item = new ItemStack(Material.BOOK, 1);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(itemMeta);
        return item;
    }

    @Override
    public Color GetPowerupColor() {
        return Color.PURPLE;
    }

    @Override
    public String GetHologramName() {
        return ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "SHUFFLE";
    }

    @Override
    public void Trigger(Player p, PowerupHandler handler) {
        PlayerManager playerManager = handler.getPlayerManager();
        playerManager.Shuffle();
    }
}
package mu.xeterios.tag.tag.powerup.powerups;

import mu.xeterios.tag.tag.PowerupHandler;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Sniper implements Powerup {

    @Override
    public ItemStack GetItem() {
        ItemStack item = new ItemStack(Material.BOW, 1);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(itemMeta);
        return item;
    }

    @Override
    public Color GetPowerupColor() {
        return Color.RED;
    }

    @Override
    public String GetHologramName() {
        return ChatColor.RED + "" + ChatColor.BOLD + "SNIPER";
    }

    @Override
    public void Trigger(Player p, PowerupHandler handler) {
        p.getInventory().setItemInOffHand(GetBow());
        p.getInventory().setItem(9, new ItemStack(Material.ARROW, 1));
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 2);
        p.sendTitle(ChatColor.RED + "SNIPER", ChatColor.GRAY + "acquired!", 0, 20, 10);
    }

    public static ItemStack GetBow(){
        ItemStack bow = new ItemStack(Material.BOW, 1);
        ItemMeta bowMeta = bow.getItemMeta();
        bowMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "SNIPER");
        bowMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        bowMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        bow.setItemMeta(bowMeta);
        return bow;
    }
}

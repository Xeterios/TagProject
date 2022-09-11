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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class Boost implements Powerup {

    @Override
    public ItemStack GetItem() {
        ItemStack item = new ItemStack(Material.SUGAR, 1);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(itemMeta);
        return item;
    }

    @Override
    public Color GetPowerupColor() {
        return Color.AQUA;
    }

    @Override
    public String GetHologramName() {
        return ChatColor.AQUA + "" + ChatColor.BOLD + "BOOST (2s)";
    }

    @Override
    public void Trigger(Player p, PowerupHandler handler) {
        PotionEffect effect = new PotionEffect(PotionEffectType.SPEED, 60, 2);
        p.addPotionEffect(effect);
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 2);
        p.sendTitle(ChatColor.AQUA + "BOOST", ChatColor.GRAY + "activated!", 0, 20, 10);
    }
}
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

import java.util.Objects;


public class Invisibility implements Powerup {

    @Override
    public ItemStack GetItem() {
        ItemStack item = new ItemStack(Material.FEATHER, 1);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(itemMeta);
        return item;
    }

    @Override
    public Color GetPowerupColor() {
        return Color.GRAY;
    }

    @Override
    public String GetHologramName() {
        return ChatColor.GRAY + "" + ChatColor.BOLD + "INVISIBILITY (3s)";
    }

    @Override
    public void Trigger(Player p, PowerupHandler handler) {
        int duration = 80;
        if (p.getPotionEffect(PotionEffectType.INVISIBILITY) != null){
            duration += Objects.requireNonNull(p.getPotionEffect(PotionEffectType.INVISIBILITY)).getDuration();
        }
        PotionEffect effect = new PotionEffect(PotionEffectType.INVISIBILITY, duration, 0);
        p.addPotionEffect(effect);
        p.playSound(p.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 10, 1);
        p.sendTitle(ChatColor.GRAY + "INVISIBILITY", ChatColor.GRAY + "activated!", 0, 20, 10);
    }
}
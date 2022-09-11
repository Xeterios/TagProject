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
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Chains implements Powerup {
    @Override
    public ItemStack GetItem() {
        ItemStack item = new ItemStack(Material.SPLASH_POTION, 1);
        PotionMeta itemMeta = (PotionMeta) item.getItemMeta();
        itemMeta.setColor(Color.BLACK);
        itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(itemMeta);
        return item;
    }

    @Override
    public Color GetPowerupColor() {
        return Color.fromRGB(85, 85, 85);
    }

    @Override
    public String GetHologramName() {
        return ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "CHAINS";
    }

    @Override
    public void Trigger(Player p, PowerupHandler handler) {
        p.getInventory().setItemInOffHand(GetPotion());
        if (p.getInventory().getItem(9) != null){
            p.getInventory().setItem(9, new ItemStack(Material.AIR, 1));
        }
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 2);
        p.sendTitle(ChatColor.DARK_GRAY + "CHAINS", ChatColor.GRAY + "acquired!", 0, 20, 10);
    }

    public static ItemStack GetPotion(){
        ItemStack item = new ItemStack(Material.SPLASH_POTION, 1);
        PotionMeta itemMeta = (PotionMeta) item.getItemMeta();
        itemMeta.addCustomEffect(new PotionEffect(PotionEffectType.SLOW, 100, 2), false);
        itemMeta.addCustomEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 2), false);
        itemMeta.setColor(Color.fromRGB(85, 85, 85));
        itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(itemMeta);
        return item;
    }
}

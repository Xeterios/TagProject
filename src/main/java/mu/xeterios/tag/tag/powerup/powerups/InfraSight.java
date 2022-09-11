package mu.xeterios.tag.tag.powerup.powerups;

import mu.xeterios.tag.Main;
import mu.xeterios.tag.tag.PowerupHandler;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;

public class InfraSight implements Powerup {

    @Override
    public ItemStack GetItem() {
        ItemStack item = new ItemStack(Material.GLOWSTONE_DUST, 1);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(itemMeta);
        return item;
    }

    @Override
    public Color GetPowerupColor() {
        return Color.YELLOW;
    }

    @Override
    public String GetHologramName() {
        return ChatColor.YELLOW + "" + ChatColor.BOLD + "INFRA SIGHT";
    }

    @Override
    public void Trigger(Player player, PowerupHandler handler) {
        PotionEffect effect = new PotionEffect(PotionEffectType.GLOWING, 100, 0, true, true, true);
        Team runners = Main.getPlugin(Main.class).getTag().scoreboard.getTeam("Runners");
        Team taggers = Main.getPlugin(Main.class).getTag().scoreboard.getTeam("Taggers");
        assert runners != null;
        assert taggers != null;
        if (runners.getEntries().contains(player.getName())){
            for (String s : taggers.getEntries()){
                Player p = Bukkit.getPlayer(s);
                assert p != null;
                p.addPotionEffect(effect);
            }
        } else {
            if (taggers.getEntries().contains(player.getName())) {
                for (String s : runners.getEntries()){
                    Player p = Bukkit.getPlayer(s);
                    assert p != null;
                    p.addPotionEffect(effect);
                }
            }
        }
        handler.infraSightActive = true;
        Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), () -> handler.infraSightActive = false, 60L);
        player.sendTitle(ChatColor.YELLOW + "INFRA SIGHT", ChatColor.GRAY + "activated!", 0, 20, 10);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 2);
    }
}

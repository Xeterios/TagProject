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

import java.util.Objects;

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
        Team runners = Main.getPlugin(Main.class).getTag().getScoreboard().getTeam("Runners");
        Team taggers = Main.getPlugin(Main.class).getTag().getScoreboard().getTeam("Taggers");
        assert runners != null;
        assert taggers != null;

        if (runners.getEntries().contains(player.getName())){
            int duration = GetDuration(taggers);
            GiveEffect(taggers, duration);
        } else if (taggers.getEntries().contains(player.getName())) {
            int duration = GetDuration(runners);
            GiveEffect(runners, duration);
        }
        player.sendTitle(ChatColor.YELLOW + "INFRA SIGHT", ChatColor.GRAY + "activated!", 0, 20, 10);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 2);
    }

    private int GetDuration(Team team){
        for (String s : team.getEntries()){
            Player p = Bukkit.getPlayer(s);
            assert p != null;
            if (p.getPotionEffect(PotionEffectType.GLOWING) != null){
                return 100 + Objects.requireNonNull(p.getPotionEffect(PotionEffectType.GLOWING)).getDuration();
            }
        }
        return 100;
    }

    private void GiveEffect(Team team, int duration){
        PotionEffect effect = new PotionEffect(PotionEffectType.GLOWING, duration, 0, true, true, true);

        for (String s : team.getEntries()){
            Player p = Bukkit.getPlayer(s);
            assert p != null;
            p.addPotionEffect(effect);
        }
    }
}

package mu.xeterios.tag.tag.powerup.powerups;

import mu.xeterios.tag.tag.PowerupHandler;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface Powerup {

    ItemStack GetItem();
    Color GetPowerupColor();
    String GetHologramName();
    void Trigger(Player p, PowerupHandler handler);
}

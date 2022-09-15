package mu.xeterios.tag.tag.timer;

import mu.xeterios.tag.tag.players.TagPlayer;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.TimerTask;

public class InfraSightTimer extends TimerTask {

    private final ArrayList<TagPlayer> subjectedPlayers;
    private int i = 5;

    public InfraSightTimer(ArrayList<TagPlayer> players){
        this.subjectedPlayers = players;
    }

    @Override
    public void run() {
        if (i == 0) {
            PotionEffect effect = new PotionEffect(PotionEffectType.GLOWING, i, 0, true, true, true);
            for(TagPlayer player : subjectedPlayers){
                player.getPlayer().addPotionEffect(effect);
            }
        }
        i--;
    }
}

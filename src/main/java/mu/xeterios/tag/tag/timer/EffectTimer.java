package mu.xeterios.tag.tag.timer;

import mu.xeterios.tag.Main;
import mu.xeterios.tag.tag.players.PlayerManager;
import mu.xeterios.tag.tag.players.PlayerType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.Timer;
import java.util.TimerTask;

public class EffectTimer extends TimerTask {

    private final PlayerManager playerManager;
    private final PlayerType type;
    private final int period;
    private final PotionEffect effect;
    private int i;

    public EffectTimer(PlayerManager playerManager, PotionEffect effect, int period, PlayerType type){
        this.playerManager = playerManager;
        this.type = type;
        this.effect = effect;
        this.period = period;
        this.i = effect.getDuration() / 20 * (period / 1000);
    }

    @Override
    public void run() {
        if (i == 0){
            if (type.equals(PlayerType.TAGGER)){
                playerManager.getEffectTimer().cancel();
                playerManager.getEffectTimer().purge();
                playerManager.setEffectTimer(new Timer());
            } else {
                playerManager.getEffectTimer2().cancel();
                playerManager.getEffectTimer2().purge();
                playerManager.setEffectTimer2(new Timer());
            }
        } else {
            PotionEffect effect = new PotionEffect(this.effect.getType(), (int) (i * 20 / (period * 0.02)) * 20, this.effect.getAmplifier(), this.effect.isAmbient(), this.effect.hasParticles(), this.effect.hasIcon());
            for(Player player : playerManager.GetPlayers(type)){
                Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), () -> player.addPotionEffect(effect));
            }
            i--;
        }
    }
}

package mu.xeterios.tag.tag.powerup;

import mu.xeterios.tag.Main;
import mu.xeterios.tag.tag.PowerupHandler;
import mu.xeterios.tag.tag.Tag;
import org.bukkit.Bukkit;

import java.util.TimerTask;

public class PowerupTimer extends TimerTask {

    private final PowerupHandler handler;
    private final Tag tag;
    private int i;

    public PowerupTimer(PowerupHandler handler, Tag tag, int delay){
        this.handler = handler;
        this.tag = tag;
        this.i = delay;
    }

    @Override
    public void run() {
        PowerupSpawner spawner = handler.getSpawner();
        if (tag.isStarted()){
            if (i == 0){
                Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), spawner::Spawn);
                Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), spawner::Spawn);
                Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), spawner::Spawn);
                spawner.RunTimer(15);
            } else {
                i--;
            }
        } else {
            spawner.getPowerUpTimer().cancel();
        }
    }
}

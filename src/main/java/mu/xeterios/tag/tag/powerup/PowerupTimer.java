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
        if (tag.started){
            if (i == 0){
                Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), handler.spawner::Spawn);
                Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), handler.spawner::Spawn);
                Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), handler.spawner::Spawn);
                handler.spawner.RunTimer(15);
            } else {
                i--;
            }
        } else {
            handler.spawner.powerUpTimer.cancel();
        }
    }
}

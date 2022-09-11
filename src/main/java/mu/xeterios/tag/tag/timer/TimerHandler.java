package mu.xeterios.tag.tag.timer;

import mu.xeterios.tag.config.Map;
import mu.xeterios.tag.tag.Tag;

import java.util.Timer;
import java.util.TimerTask;

public class TimerHandler {

    private final Tag tag;
    private Timer timer;
    private TimerTask activeTask;

    public TimerHandler(Tag tag) {
        this.tag = tag;
        this.timer = new Timer();
    }

    public TimerTask GetTimer(TimerType type, Map map) {
        return switch (type) {
            case STARTUP -> new StartupTimer(this, tag, map);
            case GAME -> new GameTimer(this, tag, map);
            case NEXTROUND -> new NextRoundTimer(this, tag, map);
        };
    }

    public void RunTimer(TimerType type, Map map) {
        this.timer = new Timer();
        if (GetTimer(type, map) != null) {
            activeTask = GetTimer(type, map);
            timer.schedule(activeTask, 0, 1000);
        }
    }

    public void StopTimer() {
        this.timer.cancel();
        this.timer.purge();
        this.timer = new Timer();
    }

    public TimerTask activeTask(){
        return activeTask;
    }
}

package mu.xeterios.tag.tag.powerup;

import mu.xeterios.tag.Main;
import mu.xeterios.tag.tag.powerup.powerups.*;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class PowerupFactory {

    private final TreeMap<Double, String> chanceMap;

    public PowerupFactory(){
        this.chanceMap = new TreeMap<>();
        List<Double> keys = new ArrayList<>(Main.getPlugin(Main.class).getTag().getConfig().getPowerupChances().keySet());
        List<String> values = new ArrayList<>(Main.getPlugin(Main.class).getTag().getConfig().getPowerupChances().values());
        for (int i = 0; i < keys.size(); i++){
            if (i > 0){
                if (!(keys.get(i) == 0)) {
                    chanceMap.put(keys.get(i) + keys.get(i - 1), values.get(i));
                }
            } else {
                if (!(keys.get(i) == 0)) {
                    chanceMap.put(keys.get(i), values.get(i));
                }
            }
        }
    }

    public Powerup GetPowerup(double i){
        String selected;
        if (chanceMap.ceilingKey(i) != null){
            selected = chanceMap.get(chanceMap.ceilingKey(i));
        } else {
            selected = chanceMap.get(chanceMap.floorKey(i));
        }
        return switch (selected) {
            case "boost" -> new Boost();
            case "sniper" -> new Sniper();
            case "infrasight" -> new InfraSight();
            case "chains" -> new Chains();
            case "invisibility" -> new Invisibility();
            case "shuffle" -> new Shuffle();
            default -> null;
        };
    }

    public Powerup GetPowerup(Material material){
        return switch (material) {
            case SUGAR -> new Boost();
            case BOW -> new Sniper();
            case GLOWSTONE_DUST -> new InfraSight();
            case SPLASH_POTION -> new Chains();
            case FEATHER -> new Invisibility();
            case BOOK -> new Shuffle();
            default -> null;
        };
    }
}

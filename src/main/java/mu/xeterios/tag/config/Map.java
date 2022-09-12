package mu.xeterios.tag.config;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

@SerializableAs("Map")
public class Map implements ConfigurationSerializable {

    @Getter @Setter private String name;
    @Getter @Setter private Location spawn;
    @Getter private Location min;
    @Getter private Location max;

    public Map() {

    }

    public Map(String name, Location spawn, Location min, Location max) {
        this.name = name;
        this.spawn = spawn;
        this.min = min;
        this.max = max;
    }

    public void setRegions(Location pos1, Location pos2) {
        this.min = pos1;
        this.max = pos2;
    }

    @Override
    public java.util.@NotNull Map<String, Object> serialize() {
        java.util.Map<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("spawn", spawn);
        result.put("min", min);
        result.put("max", max);
        return result;
    }

    public static Map deserialize(java.util.Map<String, Object> args) {
        String name = "";
        Location spawn = new Location(null, 0, 0, 0);
        Location min = new Location(null, 0, 0, 0);
        Location max = new Location(null, 0, 0, 0);
        if (args.containsKey("name")) {
            name = (String) args.get("name");
        }
        if (args.containsKey("spawn")) {
            spawn = (Location) args.get("spawn");
        }
        if (args.containsKey("min")) {
            min = (Location) args.get("min");
        }
        if (args.containsKey("max")) {
            max = (Location) args.get("max");
        }
        return new Map(name, spawn, min, max);
    }
}

package mu.xeterios.tag.config;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public class Region {

    private final Map map;

    public Region(Map map) {
        this.map = map;
    }

    public Location getRandomLocation(){
        Location min = map.getMin();
        Location max = map.getMax();
        int x = (int) (min.getX() + (int) (Math.random() * ((max.getX() - min.getX()) + 1)));
        int y = (int) (min.getY() + (int) (Math.random() * ((max.getY() - min.getY()) + 1)));
        int z = (int) (min.getZ() + (int) (Math.random() * ((max.getZ() - min.getZ()) + 1)));

        Location toReturn = new Location(map.getSpawn().getWorld(), x, y,z);
        World world = toReturn.getWorld();
        boolean valid = false;
        int attempt = 1;
        while (!valid){
            if (world.getBlockAt((int) toReturn.getX(), (int) toReturn.getY()-1, (int) toReturn.getZ()).isEmpty()){
                toReturn.setY(toReturn.getY()-1);
            } else if (!world.getBlockAt((int) toReturn.getX(), (int) toReturn.getY(), (int) toReturn.getZ()).isEmpty()){
                toReturn.setY(toReturn.getY()+1);
            }
            if (!world.getBlockAt((int) toReturn.getX(), (int) toReturn.getY()-1, (int) toReturn.getZ()).isEmpty() && world.getBlockAt((int) toReturn.getX(), (int) (toReturn.getY()), (int) toReturn.getZ()).isEmpty()){
                valid = true;
            }
            attempt++;
            if (attempt == 15 || world.getBlockAt(toReturn).getType().equals(Material.BARRIER) || toReturn.getY() > 255 || toReturn.getY() < 0){
                int x2 = (int) (min.getX() + (int) (Math.random() * ((max.getX() - min.getX()) + 1)));
                int y2 = (int) (min.getY() + (int) (Math.random() * ((max.getY() - min.getY()) + 1)));
                int z2 = (int) (min.getZ() + (int) (Math.random() * ((max.getZ() - min.getZ()) + 1)));
                toReturn = new Location(map.getSpawn().getWorld(), x2, y2, z2);
                attempt = 1;
            }
        }
        toReturn.add(0.5, 0.5, 0.5);
        return toReturn;
    }
}

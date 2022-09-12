package mu.xeterios.tag.tag.players;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

public class TagPlayer {

    @Getter @Setter private PlayerType type;
    @Getter private final Player player;
    @Getter private int points;
    @Getter private int bonusPoints;

    public TagPlayer(Player player) {
        this.player = player;
        this.points = 0;
        this.bonusPoints = 0;
    }

    public void AddPoints(int amount){
        this.points += amount;
    }

    public void AddBonusPoints(int amount){
        this.bonusPoints += amount;
    }
}
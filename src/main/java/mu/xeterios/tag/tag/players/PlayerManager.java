package mu.xeterios.tag.tag.players;

import lombok.Getter;
import lombok.Setter;
import mu.xeterios.tag.Main;
import mu.xeterios.tag.tag.Tag;
import mu.xeterios.tag.tag.timer.EffectTimer;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Score;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.logging.Level;

public class PlayerManager {

    private final Tag tag;
    @Getter @Setter private Timer effectTimer;
    @Getter @Setter private Timer effectTimer2;

    private final ArrayList<TagPlayer> players;

    public PlayerManager(Tag tag){
        this.tag = tag;
        this.players = new ArrayList<>();
        this.effectTimer = new Timer();
        this.effectTimer2 = new Timer();
    }

    public void AddPlayer(Player player){
        TagPlayer tagPlayer = new TagPlayer(player);
        this.players.add(tagPlayer);
    }

    public void RemovePlayer(Player player){
        TagPlayer tagPlayer = GetTagPlayer(player);
        this.players.remove(tagPlayer);
    }

    public void MakeTagger(Player player){
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 1, false, false));
        player.removePotionEffect(PotionEffectType.GLOWING);

        ItemStack glass = new ItemStack(Material.RED_STAINED_GLASS, 1);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "TAGGER!!!");
        glass.setItemMeta(glassMeta);

        for (int j = 0; j < 9; j++){
            player.getInventory().setItem(j, glass);
        }
    }

    public void MakeRunner(Player player){
        player.removePotionEffect(PotionEffectType.SPEED);
        player.removePotionEffect(PotionEffectType.GLOWING);

        ItemStack air = new ItemStack(Material.AIR, 1);
        for (int j = 0; j < 9; j++){
            player.getInventory().setItem(j, air);
        }
    }

    public void EliminatePlayer(Player player){
        MakeRunner(player);
        ChangePlayerType(player, PlayerType.ELIMINATED);
        player.setGameMode(GameMode.SPECTATOR);

        tag.getPlayerDataHandler().GetPlayer(player).resetWinStreakCount();

        String message = "$pluginPrefix" + ChatColor.RED + player.getName() + ChatColor.DARK_RED + " is eliminated.";
        SendMessage(message);

        tag.getMap().getSpawn().getWorld().spawnParticle(Particle.FIREWORKS_SPARK, player.getLocation().add(0, 1, 0), 50, 0.5, 0.5, 0.5, 0.1);
        tag.getMap().getSpawn().getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 3, 1);

        if (GetPlayers(PlayerType.RUNNER).size() == 1 || tag.getRound() == 10){
            tag.Stop();
            for(Player p : GetAllPlayers()){
                TagPlayer tagPlayer = GetTagPlayer(p);
                PlayerData playerData = tag.getPlayerDataHandler().GetPlayer(p);
                playerData.addPoints(tagPlayer.getPoints() + tagPlayer.getBonusPoints());
                if (tagPlayer.getType().equals(PlayerType.RUNNER)){
                    playerData.addWin();
                }
                tag.getConfig().SavePlayer(playerData);
                Main.getPlugin(Main.class).getLogger().info(p.getName() + " now has " + playerData.getTotalPoints() + " points and has " + playerData.getTotalWins() + " wins");
            }
        }
    }

    public void SwapTagger(Player tagger, Player runner){
        MakeRunner(tagger);
        MakeTagger(runner);

        ChangePlayerType(tagger, PlayerType.RUNNER);
        ChangePlayerType(runner, PlayerType.TAGGER);

        Score scoreOne = tag.getObjective().getScore(tagger.getName());
        Score scoreTwo = tag.getObjective().getScore(runner.getName());
        scoreTwo.setScore(scoreOne.getScore());
        this.tag.getScoreboard().resetScores(tagger.getName());
    }

    public void ChangePlayerType(Player player, PlayerType type){
        TagPlayer tagPlayer = GetTagPlayer(player);
        tagPlayer.setType(type);

        switch (type) {
            case RUNNER -> {
                Objects.requireNonNull(tag.getScoreboard().getTeam("Taggers")).removeEntry(player.getName());
                Objects.requireNonNull(tag.getScoreboard().getTeam("Runners")).addEntry(player.getName());
            }
            case TAGGER -> {
                Objects.requireNonNull(tag.getScoreboard().getTeam("Runners")).removeEntry(player.getName());
                Objects.requireNonNull(tag.getScoreboard().getTeam("Taggers")).addEntry(player.getName());
            }
            case ELIMINATED, SPECTATOR -> {
                Objects.requireNonNull(tag.getScoreboard().getTeam("Runners")).removeEntry(player.getName());
                Objects.requireNonNull(tag.getScoreboard().getTeam("Taggers")).removeEntry(player.getName());
            }
        }
    }

    public void GiveEffect(PlayerType playerType, PotionEffect effect){
        int period = 1000;
        EffectTimer effectTask = new EffectTimer(this, effect, period, playerType);
        if (playerType.equals(PlayerType.TAGGER)){
            effectTimer.schedule(effectTask, 0, period);
        } else {
            effectTimer2.schedule(effectTask, 0, period);
        }
    }

    public void Shuffle(){
        // Reset everyone
        for (Player player : GetPlayers(new PlayerType[]{ PlayerType.RUNNER, PlayerType.TAGGER })){
            MakeRunner(player);
            ChangePlayerType(player, PlayerType.RUNNER);
        }
        tag.SelectTaggers();
        SendMessage("$pluginPrefix&dThe taggers have been &5&lSHUFFLED!");
    }

    public void ClearPlayers(){
        players.clear();
    }

    public Player GetPlayer(String name){
        Player toReturn = null;
        for(TagPlayer tagPlayer : players){
            Player player = tagPlayer.getPlayer();
            if (player.getName().equals(name)){
                toReturn = player;
            }
        }
        return toReturn;
    }

    public ArrayList<Player> GetAllPlayers(){
        ArrayList<Player> toReturn = new ArrayList<>();
        for(TagPlayer tagPlayer : players){
            toReturn.add(tagPlayer.getPlayer());
        }
        return toReturn;
    }

    public ArrayList<Player> GetPlayers(PlayerType type){
        ArrayList<Player> toReturn = new ArrayList<>();
        for(TagPlayer tagPlayer : players){
            if (tagPlayer.getType().equals(type)){
                toReturn.add(tagPlayer.getPlayer());
            }
        }
        return toReturn;
    }

    public ArrayList<Player> GetPlayers(PlayerType[] types){
        ArrayList<Player> toReturn = new ArrayList<>();
        for(TagPlayer tagPlayer : players){
            for (PlayerType type : types){
                if (tagPlayer.getType().equals(type)){
                    toReturn.add(tagPlayer.getPlayer());
                }
            }
        }
        return toReturn;
    }

    public TagPlayer GetTagPlayer(Player toFind){
        TagPlayer toReturn = null;
        for(TagPlayer tagPlayer : players){
            Player player = tagPlayer.getPlayer();
            if (player.equals(toFind)){
                toReturn = tagPlayer;
            }
        }
        return toReturn;
    }

    public void SendMessage(String message){
        String formattedMessage = FormatMessage(message);
        for(Player player : GetAllPlayers()){
            String uniqueMessage = Individualize(player, formattedMessage);
            player.sendMessage(uniqueMessage);
        }
    }

    public void SendMessage(Player player, String message){
        String formattedMessage = FormatMessage(message);
        String uniqueMessage = Individualize(player, formattedMessage);
        player.sendMessage(uniqueMessage);
    }

    public void SendMessage(PlayerType type, String message){
        String formattedMessage = FormatMessage(message);
        for(Player player : GetPlayers(type)){
            String uniqueMessage = Individualize(player, formattedMessage);
            player.sendMessage(uniqueMessage);
        }
    }

    public void SendMessage(PlayerType[] types, String message){
        String formattedMessage = FormatMessage(message);
        for(PlayerType type : types){
            for(Player player : GetPlayers(type)){
                String uniqueMessage = Individualize(player, formattedMessage);
                player.sendMessage(uniqueMessage);
            }
        }
    }

    private String FormatMessage(String message){
        message = message.replace("$pluginPrefix", tag.getConfig().getPluginPrefix());
        message = message.replace("$pluginColor", tag.getConfig().getPluginColor());
        message = ChatColor.translateAlternateColorCodes('&', message);
        return message;
    }

    private String Individualize(Player player, String message){
        return message.replace(player.getName(), ChatColor.YELLOW + player.getName());
    }

    public void SendConsoleMessage(String message){
        Main.getPlugin(Main.class).getLogger().log(Level.INFO, message);
    }

    public void SendConsoleMessage(Level logLevel, String message){
        Main.getPlugin(Main.class).getLogger().log(logLevel, message);
    }
}

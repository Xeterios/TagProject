package mu.xeterios.tag.commands.command;

public class CmdFactory {

    public Cmd GetCommand(String label, String[] args){
        Cmd cmd = new Default();
        switch (label){
            case "tag" -> {
                if (args.length > 0) {
                    switch (args[0]) {
                        case "setspawn" -> cmd = new SetSpawn(args);
                        case "editregion" -> cmd = new EditRegion(args);
                        case "createmap" -> cmd = new CreateMap(args);
                        case "maps" -> cmd = new Maps();
                        case "profile" -> cmd = new Profile(args);
                        case "leaderboard" -> cmd = new Leaderboard();
                        case "start" -> cmd = new Start(args);
                        case "stop" -> cmd = new Stop();
                        case "reload" -> cmd = new Reload();
                        case "about" -> cmd = new About();
                    }
                }
            }
            case "profile" -> cmd = new Profile(args);
            case "leaderboard" -> cmd = new Leaderboard();
        }
        return cmd;
    }
}

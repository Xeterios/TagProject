package mu.xeterios.tag.commands.command;

public class CmdFactory {

    public Cmd GetCommand(String[] args){
        return switch (args[0]) {
            case "setspawn" -> new SetSpawn(args);
            case "editregion" -> new EditRegion(args);
            case "createmap" -> new CreateMap(args);
            case "maps" -> new Maps();
            case "profile" -> new Profile(args);
            case "start" -> new Start(args);
            case "stop" -> new Stop();
            case "reload" -> new Reload();
            case "tag" -> new Default();
            case "about" -> new About();
            default -> null;
        };
    }
}

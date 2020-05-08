package best.reich.ingros.commands;

import best.reich.ingros.IngrosWare;
import best.reich.ingros.util.logging.Logger;
import me.xenforu.kelo.command.Command;
import me.xenforu.kelo.command.annotation.CommandManifest;

@CommandManifest(label = "Friend", description = "friend players", handles = {"f", "fr"})
public class FriendCommand extends Command {

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {Logger.printMessage("Too little arguments!",true);return;}
        switch (args[1]) {
            case "add":
            case "a":
            case "Add":
            case "Ad":
            case "ad":
                if (args.length > 2) {
                    if (IngrosWare.INSTANCE.friendManager.isFriend(args[2])) {
                        Logger.printMessage(args[2] + " is already your friend.",true);
                        return;
                    }
                    if (args.length < 4) {
                        Logger.printMessage("Added " + args[2] + " to your friends list without an alias.",true);
                        IngrosWare.INSTANCE.friendManager.addFriend(args[2]);
                    } else {
                        Logger.printMessage("Added " + args[2] + " to your friends list with the alias " + args[3] + ".",true);
                        IngrosWare.INSTANCE.friendManager.addFriendWithAlias(args[2], args[3]);
                    }
                }
                break;
            case "del":
            case "delete":
            case "d":
            case "rem":
            case "remove":
            case "r":
                if (args.length > 2) {
                    if (!IngrosWare.INSTANCE.friendManager.isFriend(args[2])) {
                        Logger.printMessage(args[2] + " is not your friend.",true);
                        return;
                    }
                    if (IngrosWare.INSTANCE.friendManager.isFriend(args[2])) {
                        Logger.printMessage("Removed " + args[2] + " from your friends list.",true);
                        IngrosWare.INSTANCE.friendManager.removeFriend(args[2]);
                    }
                }
                break;
            case "c":
            case "clear":
                if (IngrosWare.INSTANCE.friendManager.isEmpty()) {
                    Logger.printMessage("Your friends list is already empty.",true);
                    return;
                }
                Logger.printMessage("Your have cleared your friends list. Friends removed: " + IngrosWare.INSTANCE.friendManager.size(),true);
                IngrosWare.INSTANCE.friendManager.clear();
                break;
            case "list":
            case "l":
                if (IngrosWare.INSTANCE.friendManager.isEmpty()) {
                    Logger.printMessage("Your friends list is empty.",true);
                    return;
                }
                Logger.printMessage("Your current friends are: ",true);
                IngrosWare.INSTANCE.friendManager.getList().forEach(friend ->
                        Logger.printMessage("Username: " + friend.getName() + (friend.getAlias() != null ? (" - Alias: " + friend.getAlias()) : ""),true));
                break;
        }
    }
}
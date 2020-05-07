package best.reich.ingros.commands;

import best.reich.ingros.IngrosWare;
import best.reich.ingros.mixin.accessors.IMinecraft;
import best.reich.ingros.util.logging.Logger;
import best.reich.ingros.util.thealtening.TheAltening;
import best.reich.ingros.util.thealtening.domain.AlteningAlt;
import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.xenforu.kelo.command.Command;
import me.xenforu.kelo.command.annotation.CommandManifest;
import net.minecraft.util.Session;

import java.io.IOException;
import java.util.Objects;

@CommandManifest(label = "Alt", description = "logs in to an account", handles = {"a", "alts"})
public class AltCommand extends Command {

    @Override
    public void execute(String[] args) {
        if (args.length > 3) {
            if (args[1].toLowerCase().contains("altening")) {
                IngrosWare.INSTANCE.switchToTheAltening();
                try {
                    final TheAltening theAltening = new TheAltening(args[2]);
                    AlteningAlt account = theAltening.generateAccount(theAltening.getUser());
                    run(Objects.requireNonNull(account).getToken().replaceAll(" ", ""), "geraldBFigley");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                IngrosWare.INSTANCE.switchToMojang();
                run(args[2], args[3]);
            }
        } else Logger.printMessage("Not enough arguments!",true);
    }

    private Session createSession(String username, String password) {
        YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(java.net.Proxy.NO_PROXY, "");
        YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) service.createUserAuthentication(Agent.MINECRAFT);
        auth.setUsername(username);
        auth.setPassword(password);
        try {
            auth.logIn();

            return new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), "mojang");
        } catch (AuthenticationException localAuthenticationException) {
            localAuthenticationException.printStackTrace();
        }
        return null;
    }

    public void run(String username, String password) {
        Logger.printMessage(ChatFormatting.AQUA + "Logging in...",true);
        Session auth = createSession(username, password);
        if (auth == null) {
            Logger.printMessage(ChatFormatting.RED + "Login failed!",true);
        } else {
            Logger.printMessage(ChatFormatting.GREEN + "Logged in. (" + auth.getUsername() + ")",true);
            ((IMinecraft) mc).setSession(auth);
        }
    }
}

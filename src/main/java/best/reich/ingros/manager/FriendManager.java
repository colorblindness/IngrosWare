package best.reich.ingros.manager;

import best.reich.ingros.IngrosWare;
import best.reich.ingros.friend.Friend;

import com.google.common.reflect.TypeToken;
import me.xenforu.kelo.manager.impl.ListManager;
import me.xenforu.kelo.traits.Gsonable;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * made by Xen for IngrosWare
 * at 1/3/2020
 **/
public class FriendManager extends ListManager<Friend> implements Gsonable {
    private File friendFile;

    @Override
    public void load() {
        friendFile = new File(IngrosWare.INSTANCE.path + File.separator + "friends.json");
        try {
            if (!friendFile.exists()) {
                friendFile.createNewFile();
                return;
            }
            loadFile();
        } catch (IOException ignored) {
        }
    }

    @Override
    public void unload() {
        saveFile();
    }

    private void loadFile() {
        if (!friendFile.exists()) {
            return;
        }
        try (FileReader inFile = new FileReader(friendFile)) {
            setList(GSON.fromJson(inFile, new TypeToken<ArrayList<Friend>>() {
            }.getType()));

            if (getList() == null)
                setList(new ArrayList<>());

        } catch (Exception ignored) {
        }
    }

    private void saveFile() {
        if (friendFile.exists()) {
            try (PrintWriter writer = new PrintWriter(friendFile)) {
                writer.print(GSON.toJson(getList()));
            } catch (Exception ignored) {
            }
        }
    }

    public void addFriend(String name) {
        add(new Friend(name));
    }

    public void addFriendWithAlias(String name, String alias) {
        add(new Friend(name, alias));
    }

    public boolean isFriend(String ign) {
        return getFriend(ign) != null;
    }

    public Friend getFriend(String ign) {
        for (Friend friend : getList()) {
            if (friend.getName().equalsIgnoreCase(ign)) {
                return friend;
            }
        }
        return null;
    }

    public void removeFriend(String name) {
        Friend f = getFriend(name);
        if (f != null) {
            remove(f);
        }
    }
}

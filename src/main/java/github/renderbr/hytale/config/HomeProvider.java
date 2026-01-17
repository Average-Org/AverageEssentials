package github.renderbr.hytale.config;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import github.renderbr.hytale.config.obj.NicknameConfiguration;
import util.ConfigObjectProvider;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;

public class HomeProvider extends ConfigObjectProvider<NicknameConfiguration> {
    public HomeProvider() {
        super("homes.json", NicknameConfiguration.class);
    }

    public void setUserNickname(String uuid, String nickname) {
        this.config.nicknames.put(uuid, nickname);
        this.syncSave();
    }

    public boolean hasNickname(String uuid) {
        return this.config.nicknames.containsKey(uuid);
    }

    public String getUserNickname(String uuid) {
        return this.config.nicknames.get(uuid);
    }

    public void applyNickname(String uuid) throws NoSuchFieldException, IllegalAccessException {
        var userNickname = getUserNickname(uuid);
        var player = Universe.get().getPlayer(UUID.fromString(uuid));

        if(player == null || !player.isValid()){
            return;
        }

        // access private list 'players' on Universe via reflection
        Field playersField = Universe.class.getDeclaredField("players");
        playersField.setAccessible(true);

        Object value = playersField.get(Universe.get());
        Map<UUID, PlayerRef> players = (Map<UUID, PlayerRef>) value;

        // update player's username via uuid
        var playerRef = players.get(UUID.fromString(uuid));

        // access username field via reflection
        Field usernameField = PlayerRef.class.getDeclaredField("username");
        usernameField.setAccessible(true);

        usernameField.set(playerRef, userNickname);
    }
}

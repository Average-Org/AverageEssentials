package github.renderbr.hytale.config;

import com.hypixel.hytale.server.core.universe.Universe;
import github.renderbr.hytale.config.obj.NicknameConfiguration;
import util.ConfigObjectProvider;
import util.ReflectionUtils;
import util.UniverseUtils;

import java.util.UUID;

public class NicknameProvider extends ConfigObjectProvider<NicknameConfiguration> {
    private static final String NICKNAMES_FILE = "nicknames.json";

    public NicknameProvider() {
        super(NICKNAMES_FILE, NicknameConfiguration.class);
    }

    public void setUserNickname(String uuid, String nickname) {
        this.getConfig().nicknames.put(uuid, nickname);
        this.syncSave();
    }

    public boolean hasNickname(String uuid) {
        return this.getConfig().nicknames.containsKey(uuid);
    }

    public String getUserNickname(String uuid) {
        return this.getConfig().nicknames.get(uuid);
    }

    public void applyNickname(String uuid) throws NoSuchFieldException, IllegalAccessException {
        var userNickname = getUserNickname(uuid);
        var player = Universe.get().getPlayer(UUID.fromString(uuid));

        if (player == null || !player.isValid()) {
            return;
        }

        var players = UniverseUtils.getPlayerRefs();
        var playerRef = players.get(UUID.fromString(uuid));
        ReflectionUtils.setFieldValue(playerRef, "username", userNickname);
    }
}

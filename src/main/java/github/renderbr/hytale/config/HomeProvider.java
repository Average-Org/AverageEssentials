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
}

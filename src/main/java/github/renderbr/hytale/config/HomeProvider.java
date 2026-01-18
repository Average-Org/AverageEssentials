package github.renderbr.hytale.config;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import github.renderbr.hytale.config.obj.HomesConfiguration;
import github.renderbr.hytale.config.obj.NicknameConfiguration;
import util.ConfigObjectProvider;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;

public class HomeProvider extends ConfigObjectProvider<HomesConfiguration> {
    public HomeProvider() {
        super("homes.json", HomesConfiguration.class);
    }
}

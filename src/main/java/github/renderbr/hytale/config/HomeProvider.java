package github.renderbr.hytale.config;

import github.renderbr.hytale.config.obj.HomesConfiguration;
import util.ConfigObjectProvider;

public class HomeProvider extends ConfigObjectProvider<HomesConfiguration> {
    private static final String HOME_FILE = "homes.json";

    public HomeProvider() {
        super(HOME_FILE, HomesConfiguration.class);
    }
}

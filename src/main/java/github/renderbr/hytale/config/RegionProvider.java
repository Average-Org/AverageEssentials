package github.renderbr.hytale.config;

import github.renderbr.hytale.config.obj.HomesConfiguration;
import github.renderbr.hytale.config.obj.RegionConfiguration;
import util.ConfigObjectProvider;

public class RegionProvider extends ConfigObjectProvider<RegionConfiguration> {
    public RegionProvider() {
        super("regions.json", RegionConfiguration.class);
    }
}

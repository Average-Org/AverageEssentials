package github.renderbr.hytale.config;

import github.renderbr.hytale.config.obj.RegionConfiguration;
import util.ConfigObjectProvider;

public class RegionProvider extends ConfigObjectProvider<RegionConfiguration> {
    private static final String REGIONS_FILE = "regions.json";

    public RegionProvider() {
        super(REGIONS_FILE, RegionConfiguration.class);
    }
}

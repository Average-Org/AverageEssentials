package github.renderbr.hytale.registries;

import github.renderbr.hytale.config.*;

public class ProviderRegistry {
    public static GroupManagerProvider groupManagerProvider;
    public static InformationalMessageProvider informationalMessageProvider;
    public static ChatFilterConfigurationProvider chatFilterConfigurationProvider;
    public static NicknameProvider nicknameProvider;
    public static HomeProvider homeProvider;
    public static RegionProvider regionProvider;

    public static void registerProviders(){
        groupManagerProvider = new GroupManagerProvider();
        informationalMessageProvider = new InformationalMessageProvider();
        chatFilterConfigurationProvider = new ChatFilterConfigurationProvider();
        nicknameProvider = new NicknameProvider();
        homeProvider = new HomeProvider();
        regionProvider = new RegionProvider();
    }
}

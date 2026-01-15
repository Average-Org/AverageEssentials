package github.renderbr.hytale.registries;

import github.renderbr.hytale.config.GroupManagerProvider;
import github.renderbr.hytale.config.InformationalMessageProvider;

public class ProviderRegistry {
    public static GroupManagerProvider groupManagerProvider;
    public static InformationalMessageProvider informationalMessageProvider;

    public static void registerProviders(){
        groupManagerProvider = new GroupManagerProvider();
        informationalMessageProvider = new InformationalMessageProvider();
    }
}

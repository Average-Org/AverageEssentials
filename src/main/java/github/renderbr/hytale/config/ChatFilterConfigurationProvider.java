package github.renderbr.hytale.config;

import github.renderbr.hytale.config.obj.ChatFilterConfiguration;
import util.ConfigObjectProvider;

public final class ChatFilterConfigurationProvider extends ConfigObjectProvider<ChatFilterConfiguration> {
    public ChatFilterConfigurationProvider() {
        super("chat_filter_config.json", ChatFilterConfiguration.class);
    }
}

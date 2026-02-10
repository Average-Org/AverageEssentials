package github.renderbr.hytale.config;

import github.renderbr.hytale.config.obj.ChatFilterConfiguration;
import util.ConfigObjectProvider;

public final class ChatFilterConfigurationProvider extends ConfigObjectProvider<ChatFilterConfiguration> {
    private static final String CHAT_FILTER_CONFIG = "chat_filter_config.json";

    public ChatFilterConfigurationProvider() {
         super(CHAT_FILTER_CONFIG, ChatFilterConfiguration.class);    }
}

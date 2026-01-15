package github.renderbr.hytale.registries;

import com.hypixel.hytale.event.EventRegistry;
import github.renderbr.hytale.listeners.ChatListener;
import github.renderbr.hytale.listeners.PlayerJoinListener;

public class ListenerRegistry {
    public static void registerListeners(EventRegistry eventRegistry) throws NoSuchFieldException, IllegalAccessException {
        ChatListener.registerChatListeners(eventRegistry);
        PlayerJoinListener.register(eventRegistry);
    }
}

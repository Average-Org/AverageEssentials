package github.renderbr.hytale.registries;

import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import github.renderbr.hytale.listeners.ChatListener;
import github.renderbr.hytale.listeners.PlayerInteractionListener;
import github.renderbr.hytale.listeners.PlayerJoinListener;
import github.renderbr.hytale.listeners.PlayerMoveListener;

public class ListenerRegistry {
    public static void registerListeners(EventRegistry eventRegistry, ComponentRegistryProxy<EntityStore> entityStore) throws NoSuchFieldException, IllegalAccessException {
        ChatListener.registerChatListeners(eventRegistry);
        PlayerJoinListener.register(eventRegistry);
        PlayerMoveListener.register();
        PlayerInteractionListener.register(entityStore);
    }
}

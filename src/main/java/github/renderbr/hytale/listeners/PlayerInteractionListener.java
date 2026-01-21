package github.renderbr.hytale.listeners;

import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import github.renderbr.hytale.listeners.ecs.player.BreakBlockEventHandler;
import github.renderbr.hytale.listeners.ecs.player.PlaceBlockEventHandler;
import github.renderbr.hytale.listeners.ecs.player.UseBlockEventHandler;

public class PlayerInteractionListener {
    public static void register(ComponentRegistryProxy<EntityStore> eventRegistry){
        eventRegistry.registerSystem(new BreakBlockEventHandler());
        eventRegistry.registerSystem(new PlaceBlockEventHandler());
        eventRegistry.registerSystem(new UseBlockEventHandler());

    }
}

package github.renderbr.hytale.listeners;

import com.hypixel.hytale.event.EventPriority;
import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import github.renderbr.hytale.registries.ProviderRegistry;

public class ChatListener {

    public static void registerChatListeners(EventRegistry eventRegistry) {
        eventRegistry.registerGlobal(EventPriority.EARLY, PlayerChatEvent.class, ChatListener::onPlayerChat);
    }

    public static void onPlayerChat(PlayerChatEvent event) {
        PlayerRef sender = event.getSender();
        var groups = PermissionsModule.get().getGroupsForUser(sender.getUuid());

        // get highest weighted group
        var highestWeightedGroup = ProviderRegistry.groupManagerProvider.getHighestWeightedGroup(groups);

        var prefix = ProviderRegistry.groupManagerProvider.getGroupPrefix(highestWeightedGroup.first());

        event.setFormatter((playerRef, message) -> Message.join(
                prefix,
                Message.raw(playerRef.getUsername()),
                Message.raw(": "),
                Message.raw(message)));
    }
}

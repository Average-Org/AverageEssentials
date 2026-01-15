package github.renderbr.hytale.listeners;

import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import github.renderbr.hytale.registries.ProviderRegistry;
import util.ColorUtils;

public class PlayerJoinListener {
    public static void register(EventRegistry eventRegistry) {
        eventRegistry.registerGlobal(PlayerReadyEvent.class, PlayerJoinListener::onPlayerJoin);
    }

    public static void onPlayerJoin(PlayerReadyEvent event) {
        if(!ProviderRegistry.informationalMessageProvider.config.welcomeMessage.isBlank()){
            var player = event.getPlayer();
            var welcomeMessage = util.ColorUtils.parseColorCodes(ProviderRegistry.informationalMessageProvider.config.welcomeMessage
                    .replace("{player}", player.getDisplayName()));

            player.sendMessage(welcomeMessage);
        }
    }
}

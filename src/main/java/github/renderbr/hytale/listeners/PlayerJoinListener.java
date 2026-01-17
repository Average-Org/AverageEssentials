package github.renderbr.hytale.listeners;

import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import github.renderbr.hytale.registries.ProviderRegistry;
import util.ColorUtils;

public class PlayerJoinListener {
    public static void register(EventRegistry eventRegistry) {
        eventRegistry.registerGlobal(PlayerReadyEvent.class, PlayerJoinListener::onPlayerJoin);
        eventRegistry.registerGlobal(PlayerConnectEvent.class, PlayerJoinListener::onPlayerConnect);
    }

    public static void onPlayerJoin(PlayerReadyEvent event) {
        if (!ProviderRegistry.informationalMessageProvider.config.welcomeMessage.isBlank()) {
            var player = event.getPlayer();
            var welcomeMessage = ColorUtils.parseColorCodes(ProviderRegistry.informationalMessageProvider.config.welcomeMessage
                    .replace("{player}", player.getDisplayName()));

            player.sendMessage(welcomeMessage);
        }
    }

    public static void onPlayerConnect(PlayerConnectEvent event) {
        if (ProviderRegistry.nicknameProvider.hasNickname(event.getPlayerRef().getUuid().toString())) {
            try {
                ProviderRegistry.nicknameProvider.applyNickname(event.getPlayerRef().getUuid().toString());
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

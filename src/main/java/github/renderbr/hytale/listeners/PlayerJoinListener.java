package github.renderbr.hytale.listeners;

import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import github.renderbr.hytale.db.models.regions.PlayerRegionCommandData;
import github.renderbr.hytale.db.models.regions.service.RegionService;
import github.renderbr.hytale.registries.ProviderRegistry;
import github.renderbr.hytale.service.RegionBoundaryService;
import util.ColorUtils;

import java.sql.SQLException;

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

        PlayerRegionCommandData playerSettings = null;
        try {
            playerSettings = RegionService.getInstance().getRegionCommandData(event.getPlayerRef().getUuid().toString());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (playerSettings != null && playerSettings.viewRegionBorders) {
            RegionBoundaryService.getInstance().addTrackedPlayer(event.getPlayerRef().getUuid());
        }
    }
}

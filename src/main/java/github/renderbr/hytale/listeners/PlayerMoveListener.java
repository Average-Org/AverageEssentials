package github.renderbr.hytale.listeners;

import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.protocol.DebugShape;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.Vector3f;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolGeneralAction;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolSelectionUpdate;
import com.hypixel.hytale.protocol.packets.player.ClientMovement;
import com.hypixel.hytale.protocol.packets.player.DisplayDebug;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerEvent;
import com.hypixel.hytale.server.core.io.PacketHandler;
import com.hypixel.hytale.server.core.io.adapter.PacketAdapters;
import com.hypixel.hytale.server.core.io.handlers.game.GamePacketHandler;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.ParticleUtil;
import com.hypixel.hytale.server.core.util.NotificationUtil;
import github.renderbr.hytale.db.models.regions.PlayerRegionGroup;
import github.renderbr.hytale.db.models.regions.service.RegionService;
import util.ColorUtils;

import java.sql.SQLException;
import java.util.Objects;

public class PlayerMoveListener {


    public static void register() {
        PacketAdapters.registerInbound((PacketHandler handler, Packet packet) -> {
            var handlerName = handler.getClass().getSimpleName();
            var packetName = packet.getClass().getSimpleName();
            var regionService = RegionService.getInstance();

            if ("ClientMovement".equals(packetName)
                    && packet instanceof ClientMovement movementPacket && handler instanceof GamePacketHandler gameHandler) {
                var player = gameHandler.getPlayerRef();

                try {
                    // is this player in a region?
                    var regions = regionService.getIntersectionRegionsFromPosition(player.getTransform().getPosition(), player.getWorldUuid().toString());

                    if (!regions.isEmpty()) {
                        var region = regions.getFirst().regionGroup;

                        // is the region already in
                        if (!regionService.playersInRegions.containsKey(player.getUuid().toString())
                                || !Objects.equals(regionService.playersInRegions.get(player.getUuid().toString()).getId(), region.getId())) {
                            // notify player
                            if (region.welcomeMessage != null && !region.welcomeMessage.isEmpty()) {
                                player.sendMessage(ColorUtils.parseColorCodes(region.welcomeMessage));
                            }

                            // set player region
                            regionService.playersInRegions.put(player.getUuid().toString(), region);
                        }

                    } else {
                        // did the user leave a region?
                        var region = regionService.playersInRegions.remove(player.getUuid().toString());

                        if (region != null && region.leaveMessage != null && !region.leaveMessage.isEmpty()) {
                            player.sendMessage(ColorUtils.parseColorCodes(region.leaveMessage));
                        }
                    }

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}

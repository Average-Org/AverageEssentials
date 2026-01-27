package github.renderbr.hytale.service;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.DebugShape;
import com.hypixel.hytale.protocol.packets.player.ClearDebugShapes;
import com.hypixel.hytale.protocol.packets.player.DisplayDebug;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import github.renderbr.hytale.db.models.regions.PlayerRegionGroup;
import github.renderbr.hytale.db.models.regions.service.RegionService;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RegionBoundaryService {
    private final ScheduledExecutorService scheduler;
    private static RegionBoundaryService instance;
    private final List<UUID> trackedPlayers = new java.util.concurrent.CopyOnWriteArrayList<>();
    private final HytaleLogger logger = HytaleLogger.forEnclosingClass();

    public RegionBoundaryService() {
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void addTrackedPlayer(UUID playerRef) {
        if (this.trackedPlayers.contains(playerRef)) return;

        this.trackedPlayers.add(playerRef);
    }

    public void removeTrackedPlayer(UUID playerRef) {
        if (!this.trackedPlayers.contains(playerRef)) return;

        this.trackedPlayers.remove(playerRef);
    }

    public static RegionBoundaryService getInstance() {
        if (instance == null) {
            instance = new RegionBoundaryService();
        }

        return instance;
    }

    public void start() {
        this.scheduler.scheduleAtFixedRate(this::updateAllSelections, 500L, 500L, TimeUnit.MILLISECONDS);
    }

    public void updateAllSelections() {
        // loop through all players and send their selections
        trackedPlayers.forEach(playerUuid -> {
            try {
                var player = Universe.get().getPlayer(playerUuid);
                if (player == null) return;

                tryRenderRegionsForPlayer(player);
            } catch(Exception e){
                logger.atSevere().withCause(e).log("Failed to render regions for player %s".formatted(playerUuid));
            }
        });
    }

    public static void tryRenderRegionsForPlayer(PlayerRef playerRef) throws SQLException {

        if(!playerRef.isValid()){
            return;
        }

        if(playerRef.getWorldUuid() == null){
            return;
        }

        // get regions within 6 chunks
        var regions = RegionService.getInstance().getRegionsInRange(
                playerRef.getTransform().getPosition(),
                192,
                playerRef.getWorldUuid().toString()
        );

        for (var region : regions) {
            // send packet to render region
            if (region.regionGroup.boundaryColor == null) {
                region.regionGroup.setBoundaryColor(PlayerRegionGroup.getRandomHexColor());
                RegionService.getInstance().updateRegionGroup(region.regionGroup);
            }

            DisplayDebug displayDebug = new DisplayDebug(DebugShape.Cube, region.getMatrix(), PlayerRegionGroup.hexToVector3f(region.regionGroup.boundaryColor), 1.0f, false, null);
            playerRef.getPacketHandler().write(displayDebug);
        }
    }

    public void stop() {
        this.scheduler.shutdown();

        for (var uuid : this.trackedPlayers) {
            try {
                var playerRef = Universe.get().getPlayer(uuid);
                if (playerRef == null) continue;

                playerRef.getPacketHandler().write(new ClearDebugShapes());
            } catch (Exception var4) {
                logger.atSevere().withCause(var4).log("Failed to clear debug shapes for player %s".formatted(uuid));
            }
        }

        this.trackedPlayers.clear();
    }
}

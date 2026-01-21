package github.renderbr.hytale.db.models.regions.service;

import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.universe.Universe;
import com.j256.ormlite.dao.Dao;
import github.renderbr.hytale.AverageEssentials;
import github.renderbr.hytale.db.models.regions.PlayerRegionChunk;
import github.renderbr.hytale.db.models.regions.PlayerRegionCommandData;
import github.renderbr.hytale.db.models.regions.PlayerRegionGroup;
import github.renderbr.hytale.db.models.regions.PlayerRegionGroupShare;
import github.renderbr.hytale.db.models.regions.param.RegionZone;
import github.renderbr.hytale.registries.ProviderRegistry;

import javax.annotation.Nullable;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class RegionService {
    private static RegionService instance;

    public Map<String, PlayerRegionGroup> playersInRegions = new ConcurrentHashMap<>();

    public static RegionService getInstance() {
        if (instance == null) instance = new RegionService();
        return instance;
    }

    public Dao<PlayerRegionGroup, Long> getRegionGroupTable() {
        return AverageEssentials.databaseService.getTable(PlayerRegionGroup.class);
    }

    public Dao<PlayerRegionChunk, Long> getRegionChunkTable() {
        return AverageEssentials.databaseService.getTable(PlayerRegionChunk.class);
    }

    public Dao<PlayerRegionCommandData, Long> getRegionCommandDataTable() {
        return AverageEssentials.databaseService.getTable(PlayerRegionCommandData.class);
    }

    @Nullable
    public PlayerRegionGroup getRegionGroup(String playerUuid, String groupName) throws SQLException {
        return getRegionGroupTable().queryBuilder()
                .where().eq("playerUuid", playerUuid)
                .and().eq("groupName", groupName).queryForFirst();
    }

    @Nullable
    public PlayerRegionGroup getRegionGroup(String groupName) throws SQLException {
        return getRegionGroupTable().queryBuilder()
                .where().eq("groupName", groupName).queryForFirst();
    }

    public PlayerRegionGroup createRegionGroup(String playerUuid, String groupName) throws SQLException {
        PlayerRegionGroup regionGroup = new PlayerRegionGroup();
        regionGroup.playerUuid = playerUuid;
        regionGroup.groupName = groupName;
        getRegionGroupTable().create(regionGroup);
        return regionGroup;
    }

    public boolean canCreateRegion(String playerUuid) throws SQLException {
        return Objects.requireNonNull(getRegions(playerUuid)).size() < ProviderRegistry.regionProvider.config.defaultMaxRegions
                || PermissionsModule.get().hasPermission(UUID.fromString(playerUuid), "averageessentials.regions.unlimited");
    }

    public void createRegionChunk(PlayerRegionChunk regionChunk) throws SQLException {
        getRegionChunkTable().create(regionChunk);
    }

    public List<PlayerRegionChunk> getIntersectingRegionsFromRect(RegionZone zone, String worldUuid) throws SQLException {
        return getRegionChunkTable().queryBuilder()
                .where()
                .le("firstCornerX", zone.secondCornerX).and().ge("secondCornerX", zone.firstCornerX).and()
                .le("firstCornerY", zone.secondCornerY).and().ge("secondCornerY", zone.firstCornerY).and()
                .le("firstCornerZ", zone.secondCornerZ).and().ge("secondCornerZ", zone.firstCornerZ).and()
                .eq("worldUuid", worldUuid).query();
    }

    public List<PlayerRegionChunk> getIntersectionRegionsFromPosition(Vector3d pos, String worldUuid) throws SQLException {
        return getIntersectingRegionsFromRect(RegionZone.getFromPosition(pos, 4), worldUuid);
    }

    public PlayerRegionChunk getRegionChunkAtPosition(Vector3d pos, String worldUuid) throws SQLException {
        return getRegionChunkTable().queryBuilder()
                .where()
                .le("firstCornerX", (int) pos.x).and().ge("secondCornerX", (int) pos.x).and()
                .le("firstCornerY", (int) pos.y).and().ge("secondCornerY", (int) pos.y).and()
                .le("firstCornerZ", (int) pos.z).and().ge("secondCornerZ", (int) pos.z).and()
                .eq("worldUuid", worldUuid).queryForFirst();
    }

    public List<PlayerRegionChunk> getRegionsInRange(Vector3d pos, int radius, String worldUuid) throws SQLException {
        return getIntersectingRegionsFromRect(RegionZone.getFromPosition(pos, radius), worldUuid);
    }

    @Nullable
    public PlayerRegionCommandData getRegionCommandData(String playerUuid) throws SQLException {
        var data = getRegionCommandDataTable().queryBuilder()
                .where().eq("playerUuid", playerUuid).queryForFirst();

        if (data == null) {
            data = new PlayerRegionCommandData();
            data.playerUuid = playerUuid;
            getRegionCommandDataTable().create(data);
        }

        return data;
    }

    @Nullable
    public PlayerRegionGroup getSelectedRegionGroup(String playerUuid) throws SQLException {
        return Objects.requireNonNull(getRegionCommandData(playerUuid)).currentSelectedRegion;
    }

    public void setSelectedRegionGroup(String playerUuid, PlayerRegionGroup regionGroup) throws SQLException {
        var data = getRegionCommandData(playerUuid);
        if (data == null) return;

        data.currentSelectedRegion = regionGroup;

        getRegionCommandDataTable().update(data);
    }

    @Nullable
    public List<PlayerRegionGroup> getRegions(String playerUuid) throws SQLException {
        return getRegionGroupTable().queryBuilder().where().eq("playerUuid", playerUuid).query();
    }

    public List<PlayerRegionGroup> getRegions() throws SQLException {
        return getRegionGroupTable().queryBuilder().query();
    }

    public List<PlayerRegionChunk> getChunksFromRegion(PlayerRegionGroup regionGroup) throws SQLException {
        return getRegionChunkTable().queryBuilder().where().eq("regionGroup_id", regionGroup.getId()).query();
    }

    public boolean canInteract(PlayerRegionGroup region, String interactingPlayerUuid, boolean isBlockBreak, boolean isBlockPlace, boolean isInteraction, boolean isPvP) throws SQLException {
        // Check if region is owned by the interacting player
        if (region.playerUuid.equals(interactingPlayerUuid)) {
            return true;
        }

        // Check shared players
        var sharedPlayers = getSharedPlayersForRegion(region);
        if (sharedPlayers.contains(interactingPlayerUuid)) {
            return true;
        }

        // Check specific interaction flags
        if (isBlockBreak && !region.allowBlockBreak) return false;
        if (isBlockPlace && !region.allowBlockPlace) return false;
        if (isInteraction && !region.allowInteraction) return false;
        if (isPvP && !region.pvpEnabled) return false;

        return false;
    }

    public List<String> getSharedPlayersForRegion(PlayerRegionGroup region) throws SQLException {
        return AverageEssentials.databaseService.getTable(PlayerRegionGroupShare.class).queryBuilder()
                .where().eq("regionGroup_id", region.getId())
                .query().stream()
                .map(share -> share.playerUuid)
                .collect(Collectors.toList());
    }

    public PlayerRegionGroupShare getShareForRegionAndPlayer(PlayerRegionGroup region, String playerUuid) throws SQLException {
        return AverageEssentials.databaseService.getTable(PlayerRegionGroupShare.class).queryBuilder()
                .where().eq("regionGroup_id", region.getId())
                .and().eq("playerUuid", playerUuid)
                .queryForFirst();
    }

    public void shareRegionWithPlayer(PlayerRegionGroup region, String playerUuidToShare) throws SQLException {
        // Implement sharing logic, likely creating a record in PlayerRegionGroupShare
        // This is a placeholder implementation
        PlayerRegionGroupShare share = new PlayerRegionGroupShare();
        share.regionGroup = region;
        share.playerUuid = playerUuidToShare;

        AverageEssentials.databaseService.getTable(PlayerRegionGroupShare.class).create(share);
    }

    public void unshareRegionFromPlayer(PlayerRegionGroup region, String playerUuidToUnshare) throws SQLException {
        // Remove share record for this player and region
        var share = getShareForRegionAndPlayer(region, playerUuidToUnshare);
        if (share != null) {
            AverageEssentials.databaseService.getTable(PlayerRegionGroupShare.class).delete(share);
        }
    }

    @Nullable
    public PlayerRegionChunk getRegionChunkAtPosition(RegionZone zone, String worldUuid, String playerUuid) throws SQLException {
        return getRegionChunkTable().queryBuilder()
                .where()
                .le("firstCornerX", zone.secondCornerX).and().ge("secondCornerX", zone.firstCornerX)
                .and().le("firstCornerY", zone.secondCornerY).and().ge("secondCornerY", zone.firstCornerY)
                .and().le("firstCornerZ", zone.secondCornerZ).and().ge("secondCornerZ", zone.firstCornerZ)
                .and().eq("worldUuid", worldUuid)
                .and().eq("playerUuid", playerUuid)
                .queryForFirst();
    }

    public void deleteRegionChunk(PlayerRegionChunk chunk) throws SQLException {
        // Update region's current claimed blocks before deleting
        var regionGroup = chunk.regionGroup;
        regionGroup.currentClaimedBlocks -= calculateClaimedBlocks(chunk);
        getRegionGroupTable().update(regionGroup);

        // Delete the chunk
        getRegionChunkTable().delete(chunk);
    }

    public void deleteRegionGroup(PlayerRegionGroup region) throws SQLException {
        // Delete all chunks in the region first
        var chunks = getChunksFromRegion(region);
        for (var chunk : chunks) {
            getRegionChunkTable().delete(chunk);
        }

        // Delete the region group
        getRegionGroupTable().delete(region);
    }

    public void updateRegionGroup(PlayerRegionGroup region) throws SQLException {
        getRegionGroupTable().update(region);
    }

    private int calculateClaimedBlocks(PlayerRegionChunk chunk) {
        // Calculate total blocks in the region chunk
        int xSize = Math.abs(chunk.secondCornerX - chunk.firstCornerX) + 1;
        int ySize = Math.abs(chunk.secondCornerY - chunk.firstCornerY) + 1;
        int zSize = Math.abs(chunk.secondCornerZ - chunk.firstCornerZ) + 1;
        return xSize * ySize * zSize;
    }

    public List<PlayerRegionGroup> getAllRegions() throws SQLException {
        return getRegionGroupTable().queryForAll();
    }

    public void triggerRegionMessages(PlayerRegionGroup region, String playerUuid, boolean entering) {
        try {
            var player = Universe.get().getPlayer(UUID.fromString(playerUuid));
            if (player == null) return;

            String message = entering ? region.welcomeMessage : region.leaveMessage;
            if (message != null && !message.isEmpty()) {
                player.sendMessage(Message.raw(message));
            }
        } catch (Exception ignored) {
        }
    }
}

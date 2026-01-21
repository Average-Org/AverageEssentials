package github.renderbr.hytale.listeners.ecs.player;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.PlaceBlockEvent;
import com.hypixel.hytale.server.core.event.events.ecs.UseBlockEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import github.renderbr.hytale.db.models.regions.service.RegionService;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.sql.SQLException;

public class UseBlockEventHandler extends EntityEventSystem<EntityStore, UseBlockEvent.Pre> {

    public UseBlockEventHandler() {
        super(UseBlockEvent.Pre.class);
    }


    @Override
    public void handle(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl UseBlockEvent.Pre useBlockEvent) {
        var ref = archetypeChunk.getReferenceTo(i);
        var player = store.getComponent(ref, Player.getComponentType());
        var playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        if (player == null || playerRef == null) {
            return;
        }

        if(player.hasPermission("averageessentials.region.bypass")){
            return;
        }

        try {
            var region = RegionService.getInstance()
                    .getRegionChunkAtPosition(useBlockEvent.getTargetBlock().toVector3d(), playerRef.getWorldUuid().toString());

            if(region == null){
                return;
            }

            if (!RegionService.getInstance().canInteract(region.regionGroup, playerRef.getUuid().toString(), false, false, true, false)) {
                // cancel event
                player.sendMessage(Message.translation("averageessentials.region.usingdenied"));
                useBlockEvent.setCancelled(true);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return PlayerRef.getComponentType();
    }
}

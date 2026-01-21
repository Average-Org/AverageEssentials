
package github.renderbr.hytale;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import github.renderbr.hytale.db.models.PlayerHome;
import github.renderbr.hytale.db.models.regions.PlayerRegionChunk;
import github.renderbr.hytale.db.models.regions.PlayerRegionCommandData;
import github.renderbr.hytale.db.models.regions.PlayerRegionGroup;
import github.renderbr.hytale.db.models.regions.PlayerRegionGroupShare;
import github.renderbr.hytale.registries.CommandRegistry;
import github.renderbr.hytale.registries.ListenerRegistry;
import github.renderbr.hytale.registries.ProviderRegistry;
import github.renderbr.hytale.service.RegionBoundaryService;
import models.db.DatabaseService;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import util.DbUtils;
import util.PathUtils;

import java.sql.SQLException;

public class AverageEssentials extends JavaPlugin {

    public static DatabaseService databaseService;

    public AverageEssentials(@NonNullDecl JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        PathUtils.setModDirectoryName("AverageEssentials");

        try {
            databaseService = DbUtils.initializeDatabase("average-essentials");
            databaseService.addTable(PlayerHome.class);
            databaseService.addTable(PlayerRegionChunk.class);
            databaseService.addTable(PlayerRegionGroup.class);
            databaseService.addTable(PlayerRegionGroupShare.class);
            databaseService.addTable(PlayerRegionCommandData.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        CommandRegistry.registerCommands(this.getCommandRegistry());
        ProviderRegistry.registerProviders();

        try {
            ListenerRegistry.registerListeners(this.getEventRegistry(), this.getEntityStoreRegistry());
            RegionBoundaryService.getInstance().start();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void shutdown() {
        RegionBoundaryService.getInstance().stop();
        super.shutdown();
    }
}
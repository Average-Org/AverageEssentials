
package github.renderbr.hytale;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import github.renderbr.hytale.db.models.PlayerHome;
import github.renderbr.hytale.registries.CommandRegistry;
import github.renderbr.hytale.registries.ListenerRegistry;
import github.renderbr.hytale.registries.ProviderRegistry;
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        CommandRegistry.registerCommands(this.getCommandRegistry());
        ProviderRegistry.registerProviders();

        try {
            ListenerRegistry.registerListeners(this.getEventRegistry());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
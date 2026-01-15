
package github.renderbr.hytale;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import github.renderbr.hytale.registries.CommandRegistry;
import github.renderbr.hytale.registries.ListenerRegistry;
import github.renderbr.hytale.registries.ProviderRegistry;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class AverageEssentials extends JavaPlugin {

    public AverageEssentials(@NonNullDecl JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        CommandRegistry.registerCommands(this.getCommandRegistry());
        ProviderRegistry.registerProviders();
        ListenerRegistry.registerListeners(this.getEventRegistry());
    }
}
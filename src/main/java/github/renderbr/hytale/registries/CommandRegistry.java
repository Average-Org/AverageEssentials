package github.renderbr.hytale.registries;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import github.renderbr.hytale.commands.*;

import java.util.List;

public class CommandRegistry {
    private static com.hypixel.hytale.server.core.command.system.CommandRegistry registry;

    public static final List<AbstractCommandCollection> REGISTERED_COMMANDS_COLLECTIONS = List.of(
            new AlteredPluginCommand(),
            new GroupManagerCommand(),
            new HomeCommand(),
            new RegionCommand()
    );

    public static final List<CommandBase> REGISTERED_COMMANDS = List.of(
            new NicknameCommand(),
            new BroadcastCommand()
    );


    public static void registerCommands(com.hypixel.hytale.server.core.command.system.CommandRegistry registry) {
        CommandRegistry.registry = registry;
        REGISTERED_COMMANDS_COLLECTIONS.forEach(registry::registerCommand);
        REGISTERED_COMMANDS.forEach(registry::registerCommand);
    }

    public static com.hypixel.hytale.server.core.command.system.CommandRegistry getHytaleCommandRegistry() {
        return CommandRegistry.registry;
    }
}

package github.renderbr.hytale.registries;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import github.renderbr.hytale.commands.AlteredPluginCommand;
import github.renderbr.hytale.commands.GroupManagerCommand;

import java.util.ArrayList;
import java.util.List;

public class CommandRegistry {
    private static com.hypixel.hytale.server.core.command.system.CommandRegistry registry;

    public static final List<AbstractCommandCollection> REGISTERED_COMMANDS = List.of(
            new AlteredPluginCommand(),
            new GroupManagerCommand()
    );


    public static void registerCommands(com.hypixel.hytale.server.core.command.system.CommandRegistry registry) {
        CommandRegistry.registry = registry;
        REGISTERED_COMMANDS.forEach(registry::registerCommand);
    }

    public static com.hypixel.hytale.server.core.command.system.CommandRegistry getHytaleCommandRegistry(){
        return CommandRegistry.registry;
    }
}

package github.renderbr.hytale.commands;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.playerdata.DefaultPlayerStorageProvider;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.concurrent.ExecutionException;

public class HomeCommand extends CommandBase {
    public HomeCommand(@NonNullDecl String name, @NonNullDecl String description) {
        super("home", "server.commands.averageessentials.home.desc");
    }

    @Override
    protected void executeSync(@NonNullDecl CommandContext commandContext) {

    }
}

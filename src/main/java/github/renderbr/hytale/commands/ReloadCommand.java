package github.renderbr.hytale.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import github.renderbr.hytale.registries.ProviderRegistry;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class ReloadCommand extends CommandBase {
    public ReloadCommand() {
        super("avreload", "server.commands.averageessentials.reload.desc");
        this.addAliases("avr", "reloadav");
    }

    @Override
    protected void executeSync(@NonNullDecl CommandContext commandContext) {
        ProviderRegistry.reloadAll();
        commandContext.sendMessage(Message.translation("server.commands.averageessentials.reload.success"));
    }
}

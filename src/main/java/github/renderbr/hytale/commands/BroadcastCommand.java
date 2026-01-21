package github.renderbr.hytale.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.universe.Universe;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import util.ColorUtils;

public class BroadcastCommand extends CommandBase {
    private final RequiredArg<String> messageArg;

    public BroadcastCommand() {
        super("broadcast", "server.commands.averageessentials.broadcast.desc");
        this.addAliases("bc", "announce");
        messageArg = this.withRequiredArg("message", "server.commands.averageessentials.broadcast.arg.desc", com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes.STRING);
    }

    @Override
    protected void executeSync(@NonNullDecl CommandContext commandContext) {
        String message = this.messageArg.get(commandContext);

        Universe.get().getPlayers().forEach((player) -> {
            player.sendMessage(ColorUtils.parseColorCodes(message));
        });

        commandContext.sendMessage(Message.translation("server.commands.averageessentials.broadcast.success"));
    }
}

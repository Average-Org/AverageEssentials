package github.renderbr.hytale.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import util.ColorUtils;

public class BasicOutputCommand extends CommandBase {
    Message messageToSend;

    public BasicOutputCommand(@NonNullDecl String name, @NonNullDecl String description) {
        super(name, util.ColorUtils.parseColorCodes(description).getAnsiMessage());
        this.messageToSend = ColorUtils.parseColorCodes(description);
    }

    @Override
    protected void executeSync(@NonNullDecl CommandContext commandContext) {
        commandContext.sendMessage(messageToSend);
    }
}

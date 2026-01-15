package github.renderbr.hytale.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import github.renderbr.hytale.util.ColorUtils;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class BasicOutputCommand extends CommandBase {
    Message messageToSend;

    public BasicOutputCommand(@NonNullDecl String name, @NonNullDecl String description) {
        var messageToSend = ColorUtils.parseColorCodes(description);
        super(name, messageToSend.getAnsiMessage());

        this.messageToSend = messageToSend;
    }

    @Override
    protected void executeSync(@NonNullDecl CommandContext commandContext) {
        commandContext.sendMessage(messageToSend);
    }
}

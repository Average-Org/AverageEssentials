package github.renderbr.hytale.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import github.renderbr.hytale.util.ColorUtils;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class BasicOutputCommand extends CommandBase {
    Message messageToSend;

    public BasicOutputCommand(@NonNullDecl String name, @NonNullDecl String description) {
        super(name, ColorUtils.parseColorCodes(description).getAnsiMessage());
        this.messageToSend = ColorUtils.parseColorCodes(description);
    }

    @Override
    protected void executeSync(@NonNullDecl CommandContext commandContext) {
        commandContext.sendMessage(messageToSend);
    }
}

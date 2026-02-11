package github.renderbr.hytale.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.NameMatching;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.universe.Universe;
import github.renderbr.hytale.registries.ProviderRegistry;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.security.Provider;

public class NicknameCommand extends CommandBase {
    public RequiredArg<String> nicknameOrPlayerArg;
    public OptionalArg<String> nicknameIfPlayerArg;

    public NicknameCommand() {
        super("nickname", "server.commands.averageessentials.nickname.desc");
        this.addAliases("nick");

        nicknameOrPlayerArg = this.withRequiredArg("nickname|player", "server.commands.averageessentials.nickname.arg.desc", com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes.STRING);
        nicknameIfPlayerArg = this.withOptionalArg("nickname", "server.commands.averageessentials.nickname.arg.other.desc", com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes.STRING);
    }

    @Override
    protected void executeSync(@NonNullDecl CommandContext commandContext) {
        String targetName = nicknameOrPlayerArg.get(commandContext);
        String optionalNickname = nicknameIfPlayerArg.get(commandContext);

        if (optionalNickname != null) {
            handleOtherPlayerNickname(commandContext, targetName, optionalNickname);
        } else {
            handleOwnNickname(commandContext, targetName);
        }
    }

    private void handleOtherPlayerNickname(CommandContext context, String targetName, String nickname) {
        var player = Universe.get().getPlayerByUsername(targetName, NameMatching.EXACT_IGNORE_CASE);
        if (player == null || !player.isValid()) {
            context.sendMessage(Message.translation("server.commands.averageessentials.nickname.playernotfound").param("player", targetName));
            return;
        }

        boolean isClear = nickname.equalsIgnoreCase("clear");
        String finalNickname = isClear ? player.getUsername() : nickname;
        
        updateAndApplyNickname(player.getUuid().toString(), finalNickname);
        
        String translationKey = isClear ? "server.commands.averageessentials.nickname.clearedother" : "server.commands.averageessentials.nickname.changedother";
        var msg = Message.translation(translationKey).param("player", player.getUsername());
        if (!isClear) msg.param("nickname", nickname);
        
        context.sendMessage(msg);
    }

    private void handleOwnNickname(CommandContext context, String nickname) {
        var player = Universe.get().getPlayer(context.sender().getUuid());
        if (player == null || !player.isValid()) {
            context.sendMessage(Message.translation("server.commands.averageessentials.nickname.playernotfound").param("player", "you"));
            return;
        }

        boolean isClear = nickname.equalsIgnoreCase("clear");
        String finalNickname = isClear ? player.getUsername() : nickname;

        updateAndApplyNickname(player.getUuid().toString(), finalNickname);

        String translationKey = isClear ? "server.commands.averageessentials.nickname.cleared" : "server.commands.averageessentials.nickname.changed";
        var msg = Message.translation(translationKey);
        if (!isClear) msg.param("nickname", nickname);

        context.sendMessage(msg);
    }

    private void updateAndApplyNickname(String uuid, String nickname) {
        ProviderRegistry.nicknameProvider.setUserNickname(uuid, nickname);
        try {
            ProviderRegistry.nicknameProvider.applyNickname(uuid);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}

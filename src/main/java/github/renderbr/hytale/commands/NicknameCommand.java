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
        String nicknameOrPlayerName = nicknameOrPlayerArg.get(commandContext);
        String otherNickname = nicknameIfPlayerArg.get(commandContext);

        // If trying to set another user's nickname
        if (otherNickname != null) {
            var player = Universe.get().getPlayerByUsername(nicknameOrPlayerName, NameMatching.EXACT_IGNORE_CASE);

            if (player == null || !player.isValid()) {
                commandContext.sendMessage(Message.translation("server.commands.averageessentials.nickname.playernotfound").param("player", nicknameOrPlayerName));
                return;
            }

            if (otherNickname.equalsIgnoreCase("clear")) {
                ProviderRegistry.nicknameProvider.setUserNickname(player.getUuid().toString(), player.getUsername());
                try {
                    ProviderRegistry.nicknameProvider.applyNickname(player.getUuid().toString());
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                commandContext.sendMessage(Message.translation("server.commands.averageessentials.nickname.clearedother").param("player", player.getUsername()));
                return;
            }

            ProviderRegistry.nicknameProvider.setUserNickname(player.getUuid().toString(), otherNickname);
            try {
                ProviderRegistry.nicknameProvider.applyNickname(player.getUuid().toString());
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            commandContext.sendMessage(Message.translation("server.commands.averageessentials.nickname.changedother").param("player", player.getUsername()).param("nickname", otherNickname));
            return;
        }

        // If trying to set own nickname
        var player = Universe.get().getPlayer(commandContext.sender().getUuid());

        if (player == null || !player.isValid()) {
            commandContext.sendMessage(Message.translation("server.commands.averageessentials.nickname.playernotfound").param("player", nicknameOrPlayerName));
            return;
        }

        if (nicknameOrPlayerName.equalsIgnoreCase("clear")) {
            ProviderRegistry.nicknameProvider.setUserNickname(player.getUuid().toString(), player.getUsername());
            try {
                ProviderRegistry.nicknameProvider.applyNickname(player.getUuid().toString());
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            commandContext.sendMessage(Message.translation("server.commands.averageessentials.nickname.cleared"));
            return;
        }

        ProviderRegistry.nicknameProvider.setUserNickname(player.getUuid().toString(), nicknameOrPlayerName);
        try {
            ProviderRegistry.nicknameProvider.applyNickname(player.getUuid().toString());
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        commandContext.sendMessage(Message.translation("server.commands.averageessentials.nickname.changed").param("nickname", nicknameOrPlayerName));
    }
}

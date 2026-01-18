package github.renderbr.hytale.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.playerdata.DefaultPlayerStorageProvider;
import github.renderbr.hytale.AverageEssentials;
import github.renderbr.hytale.db.models.PlayerHome;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.concurrent.ExecutionException;

public class HomeCommand extends AbstractCommandCollection {

    public HomeCommand(@NonNullDecl String name, @NonNullDecl String description) {
        super("home", "server.commands.averageessentials.home.desc");
        super.addSubCommand(new ListSubCommand());
    }

    protected static class ListSubCommand extends CommandBase {
        public ListSubCommand() {
            super("list", "server.commands.averageessentials.home.list.desc");
        }

        @Override
        protected void executeSync(@NonNullDecl CommandContext commandContext) {
            var playerUuid = commandContext.sender().getUuid();
            try {
                var homeTable = AverageEssentials.databaseService.getTable(PlayerHome.class);
                var playerHomes = homeTable.queryForEq("playerUuid", playerUuid.toString());

                if (playerHomes.isEmpty()) {
                    commandContext.sendMessage(Message.translation("server.commands.averageessentials.home.list.nohomes.msg"));
                    return;
                }

                StringBuilder homeListBuilder = new StringBuilder();

                homeListBuilder.append(Message.translation("server.commands.averageessentials.home.list.header"));

                for (PlayerHome home : playerHomes) {
                    homeListBuilder.append("\n- ").append(home.homeName);
                }

                commandContext.sendMessage(Message.raw(homeListBuilder.toString()));
            } catch (Exception e) {
                e.printStackTrace();
                commandContext.sendMessage(Message.translation("server.averageessentials.err.somethingwentwrong"));
            }
        }
    }
}

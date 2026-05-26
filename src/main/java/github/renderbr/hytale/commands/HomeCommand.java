package github.renderbr.hytale.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.permissions.provider.HytalePermissionsProvider;
import com.hypixel.hytale.server.core.universe.Universe;
import github.renderbr.hytale.AverageEssentials;
import github.renderbr.hytale.db.models.PlayerHome;
import github.renderbr.hytale.registries.ProviderRegistry;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.sql.SQLException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomeCommand extends AbstractCommandCollection {

    public HomeCommand() {
        super("home", "server.commands.averageessentials.home.desc");
        super.addSubCommand(new TpCommand());
        super.addSubCommand(new ListSubCommand());
        super.addSubCommand(new SetSubCommand());
        super.addSubCommand(new DeleteCommand());
    }

    protected static class DeleteCommand extends CommandBase {
        public RequiredArg<String> homeNameArg;

        public DeleteCommand() {
            super("delete", "server.commands.averageessentials.home.delete.desc");
            homeNameArg = this.withRequiredArg("homeName", "server.commands.averageessentials.home.delete.arg.desc", com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes.STRING);
        }

        @Override
        protected void executeSync(@NonNullDecl CommandContext commandContext) {
            var playerUuid = commandContext.sender().getUuid();
            var homeName = homeNameArg.get(commandContext);

            // get home
            var homeProvider = AverageEssentials.databaseService.getTable(PlayerHome.class);
            try {
                var homeQuery = homeProvider.queryBuilder()
                        .where().eq("playerUUID", playerUuid).and().eq("homeName", homeName);

                var home = homeQuery.queryForFirst();

                if (home == null) {
                    commandContext.sendMessage(Message.translation("server.commands.averageessentials.home.delete.notfound").param("home", homeName));
                    return;
                }

                homeProvider.delete(home);
                AverageEssentials.databaseService.save();
                commandContext.sendMessage(Message.translation("server.commands.averageessentials.home.delete.success").param("home", homeName));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected static class TpCommand extends CommandBase {
        public RequiredArg<String> homeNameArg;

        public TpCommand() {
            super("tp", "server.commands.averageessentials.home.tp.desc");
            homeNameArg = this.withRequiredArg("homeName", "server.commands.averageessentials.home.tp.arg.desc", com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes.STRING);
        }

        @Override
        protected void executeSync(@NonNullDecl CommandContext commandContext) {
            var playerUuid = commandContext.sender().getUuid();
            var homeName = homeNameArg.get(commandContext);

            // get home from db
            var homeProvider = AverageEssentials.databaseService.getTable(PlayerHome.class);
            try {
                var homeQuery = homeProvider.queryBuilder()
                        .where().eq("playerUUID", playerUuid).and().eq("homeName", homeName);

                var home = homeQuery.queryForFirst();

                if (home == null) {
                    commandContext.sendMessage(Message.translation("server.commands.averageessentials.home.tp.notfound").param("home", homeName));
                    return;
                }

                var player = Universe.get().getPlayer(playerUuid);
                var currentWorld = Universe.get().getWorld(player.getWorldUuid());

                var world = Universe.get().getWorld(UUID.fromString(home.worldUuid));

                if (player == null) {
                    return;
                }

                if (world == null) {
                    commandContext.sendMessage(Message.translation("server.commands.averageessentials.home.tp.worldnotfound").param("home", homeName));
                    return;
                }

                assert currentWorld != null;
                currentWorld.execute(() -> {
                    if (player.getReference() == null) return;

                    var store = player.getReference().getStore();
                    var tp = new Teleport(world, home.getPosition(), home.getHeadRotation());
                    store.addComponent(player.getReference(), Teleport.getComponentType(), tp);
                });

                commandContext.sendMessage(Message.translation("server.commands.averageessentials.home.tp.success").param("home", homeName));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
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

                Message homeList = Message.translation("server.commands.averageessentials.home.list.header");
                for (PlayerHome home : playerHomes) {
                    homeList = Message.join(homeList, Message.raw("\n- "), Message.raw(home.homeName));
                }

                commandContext.sendMessage(homeList);
            } catch (Exception e) {
                commandContext.sendMessage(Message.translation("server.averageessentials.err.somethingwentwrong"));
            }
        }
    }

    protected static class SetSubCommand extends CommandBase {
        public RequiredArg<String> homeNameArg;

        public SetSubCommand() {
            super("set", "server.commands.averageessentials.home.set.desc");
            homeNameArg = this.withRequiredArg("homeName", "server.commands.averageessentials.home.set.arg.desc", com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes.STRING);
        }

        @Override
        protected void executeSync(@NonNullDecl CommandContext commandContext) {
            var playerUuid = commandContext.sender().getUuid();
            var homeName = homeNameArg.get(commandContext);

            var player = Universe.get().getPlayer(playerUuid);
            if (player == null) return;

            var pos = player.getTransform().getPosition();
            var homeProvider = AverageEssentials.databaseService.getTable(PlayerHome.class);

            try {
                var home = homeProvider.queryBuilder()
                        .where().eq("playerUUID", playerUuid.toString())
                        .and()
                        .eq("homeName", homeName)
                        .queryForFirst();

                if (home != null) {
                    home.setPosition(pos);
                    home.setHeadRotation(player.getHeadRotation());
                    home.worldUuid = player.getWorldUuid().toString();

                    homeProvider.update(home);
                    AverageEssentials.databaseService.save();
                    commandContext.sendMessage(Message.translation("server.commands.averageessentials.home.set.success").param("home", homeName));
                    return;
                }

                // Check home limit
                int homeCount = homeProvider.queryForEq("playerUUID", playerUuid.toString()).size();
                int maxHomes = getPlayerMaxHomes(playerUuid);

                if (homeCount >= maxHomes) {
                    commandContext.sendMessage(Message.translation("server.commands.averageessentials.home.set.maxhomes").param("maxHomes", String.valueOf(maxHomes)));
                    return;
                }

                var newHome = new PlayerHome();
                newHome.playerUuid = playerUuid.toString();
                newHome.homeName = homeName;
                newHome.worldUuid = player.getWorldUuid().toString();
                newHome.setHeadRotation(player.getHeadRotation());
                newHome.setPosition(pos);

                homeProvider.create(newHome);
                AverageEssentials.databaseService.save();
                commandContext.sendMessage(Message.translation("server.commands.averageessentials.home.set.success").param("home", homeName));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        private int getPlayerMaxHomes(UUID playerUuid) {
            var userGroups = PermissionsModule.get().getGroupsForUser(playerUuid);
            if (userGroups.contains(HytalePermissionsProvider.OP_GROUP)) {
                return Integer.MAX_VALUE;
            }

            int defaultMaxHomes = ProviderRegistry.homeProvider.getConfig().defaultMaxHomes;
            var userPermissions = PermissionsModule.get().getFirstPermissionProvider().getUserPermissions(playerUuid);

            Pattern limitPattern = Pattern.compile("averageessentials\\.homes\\.limit\\.(\\d+)");
            return userPermissions.stream()
                    .map(limitPattern::matcher)
                    .filter(Matcher::matches)
                    .mapToInt(m -> Integer.parseInt(m.group(1)))
                    .max()
                    .orElse(defaultMaxHomes);
        }
    }
}

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
import java.util.Objects;
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

        public DeleteCommand(){
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

                if(home == null){
                    commandContext.sendMessage(Message.translation("server.commands.averageessentials.home.delete.notfound").param("home", homeName));
                    return;
                }

                homeProvider.delete(home);
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

                var world = Universe.get().getWorld(UUID.fromString(home.worldUuid));

                if (player == null) {
                    return;
                }

                if (world == null) {
                    commandContext.sendMessage(Message.translation("server.commands.averageessentials.home.tp.worldnotfound").param("home", homeName));
                    return;
                }

                world.execute(() -> {
                    if(player.getReference() == null) return;

                    var store = player.getReference().getStore();
                    var tp = new Teleport(home.getPosition(), home.getHeadRotation());
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

                Message homeListBuilder = Message.translation("server.commands.averageessentials.home.list.header");

                for (PlayerHome home : playerHomes) {
                    homeListBuilder = Message.join(homeListBuilder, Message.raw("\n- "), Message.raw(home.homeName));
                }

                commandContext.sendMessage(homeListBuilder);
            } catch (Exception e) {
                e.printStackTrace();
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

            // get position of playyer
            var player = Universe.get().getPlayer(playerUuid);
            var pos = Objects.requireNonNull(player).getTransform().getPosition();

            // check if a home with the same name exists for the user
            var homeProvider = AverageEssentials.databaseService.getTable(PlayerHome.class);

            try {
                var homeQuery = homeProvider.queryBuilder()
                        .where().eq("playerUUID", playerUuid.toString())
                        .and()
                        .eq("homeName", homeName);

                var home = homeQuery.queryForFirst();

                if (home != null) {
                    // modify the position to new pos
                    home.setPosition(pos);
                    home.setHeadRotation(player.getHeadRotation());
                    home.worldUuid = player.getWorldUuid().toString();

                    homeProvider.update(home);
                    commandContext.sendMessage(Message.translation("server.commands.averageessentials.home.set.success").param("home", homeName));
                    return;
                }

                // create a new home
                var homeCount = homeProvider.queryForEq("playerUUID", playerUuid.toString()).size();

                var userGroups = PermissionsModule.get().getGroupsForUser(playerUuid).stream();
                var defaultMaxHomes = ProviderRegistry.homeProvider.config.defaultMaxHomes;

                var permissionsProvider = PermissionsModule.get().getFirstPermissionProvider();
                var userPermissions = permissionsProvider.getUserPermissions(playerUuid);

                Pattern limitPattern = Pattern.compile("averageessentials\\.homes\\.limit\\.(\\d+)");
                int userHomeAmountEntitlement = userPermissions.stream()
                        .map(limitPattern::matcher)
                        .filter(Matcher::matches)
                        .mapToInt(m -> Integer.parseInt(m.group(1)))
                        .max()
                        .orElse(defaultMaxHomes);

                if(userGroups.anyMatch(g -> g.equals(HytalePermissionsProvider.OP_GROUP))){
                    userHomeAmountEntitlement = Integer.MAX_VALUE;
                }

                if(homeCount >= userHomeAmountEntitlement){
                    commandContext.sendMessage(Message.translation("server.commands.averageessentials.home.set.maxhomes").param("maxHomes", String.valueOf(userHomeAmountEntitlement)));
                    return;
                }

                var newHome = new PlayerHome();
                newHome.playerUuid = playerUuid.toString();
                newHome.homeName = homeName;
                newHome.worldUuid = player.getWorldUuid().toString();
                newHome.setHeadRotation(player.getHeadRotation());
                newHome.setPosition(pos);

                homeProvider.create(newHome);
                commandContext.sendMessage(Message.translation("server.commands.averageessentials.home.set.success").param("home", homeName));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }
    }
}

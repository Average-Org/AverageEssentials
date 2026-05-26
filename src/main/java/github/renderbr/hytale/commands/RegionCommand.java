package github.renderbr.hytale.commands;

import com.hypixel.hytale.builtin.adventure.farming.states.TilledSoilBlock;
import com.hypixel.hytale.builtin.buildertools.commands.SetCommand;
import com.hypixel.hytale.common.collection.Flag;
import com.hypixel.hytale.protocol.Hitbox;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.NameMatching;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.FlagArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.collision.BlockCollisionProvider;
import com.hypixel.hytale.server.core.modules.collision.BlockData;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.util.TargetUtil;
import com.nimbusds.jose.util.Pair;
import github.renderbr.hytale.AverageEssentials;
import github.renderbr.hytale.db.models.regions.PlayerRegionChunk;
import github.renderbr.hytale.db.models.regions.PlayerRegionGroup;
import github.renderbr.hytale.db.models.regions.param.RegionZone;
import github.renderbr.hytale.db.models.regions.service.RegionService;
import github.renderbr.hytale.service.RegionBoundaryService;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.joml.Vector3d;
import util.CommandUtils;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class RegionCommand extends AbstractCommandCollection {
    public RegionCommand() {
        super("region", "server.commands.averageessentials.region.desc");
        this.addAliases("argm");

        CommandUtils.addSubcommands(
                this,
                new ClaimCommand(),
                new UnclaimCommand(),
                new UnclaimAllCommand(),
                new ListCommand(),
                new CreateCommand(),
                new SelectCommand(),
                new ShareCommand(),
                new UnshareCommand(),
                new TeleportCommand(),
                new WelcomeMessageCommand(),
                new LeaveMessageCommand(),
                new DescriptionCommand(),
                new ColorCommand(),
                new FlagCommand(),
                new LimitCommand(),
                new RenameCommand(),
                new ViewCommand()
        );
    }

    protected static class FlagCommand extends CommandBase {
        private final RequiredArg<String> flagType;
        private final RequiredArg<Boolean> flagValue;

        public FlagCommand() {
            super("flag", "server.commands.averageessentials.region.flag.desc");
            flagType = this.withRequiredArg("type", "server.commands.averageessentials.region.flag.type.desc", com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes.STRING);
            flagValue = this.withRequiredArg("value", "server.commands.averageessentials.region.flag.value.desc", com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes.BOOLEAN);
        }

        @Override
        protected void executeSync(@NonNullDecl CommandContext commandContext) {
            var playerUuid = commandContext.sender().getUuid();
            var flagTypeStr = this.flagType.get(commandContext).toLowerCase();
            var flagValueBool = this.flagValue.get(commandContext);

            try {
                var region = RegionService.getInstance().getSelectedRegionGroup(playerUuid.toString());
                if (region == null) {
                    commandContext.sendMessage(Message.translation("server.commands.averageessentials.region.flag.noselected"));
                    return;
                }

                if (!updateFlag(region, flagTypeStr, flagValueBool)) {
                    commandContext.sendMessage(Message.translation("server.commands.averageessentials.region.flag.invalidflag"));
                    return;
                }

                RegionService.getInstance().updateRegionGroup(region);
                commandContext.sendMessage(Message.translation("server.commands.averageessentials.region.flag.success")
                        .param("flag", flagTypeStr)
                        .param("value", flagValueBool));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        private boolean updateFlag(PlayerRegionGroup region, String type, boolean value) {
            switch (type) {
                case "blockbreak" -> region.allowBlockBreak = value;
                case "blockplace" -> region.allowBlockPlace = value;
                case "interaction" -> region.allowInteraction = value;
                case "pvp" -> region.pvpEnabled = value;
                default -> { return false; }
            }
            return true;
        }
    }

    protected static class ViewCommand extends CommandBase {
        public ViewCommand() {
            super("view", "server.commands.averageessentials.region.view.desc");
        }

        @Override
        protected void executeSync(@NonNullDecl CommandContext commandContext) {
            var playerUuid = commandContext.sender().getUuid();
            try {
                var commandData = RegionService.getInstance().getRegionCommandData(playerUuid.toString());
                if (commandData == null) {
                    commandContext.sendMessage(Message.translation("server.commands.averageessentials.region.view.nocommanddata"));
                    return;
                }

                commandData.viewRegionBorders = !commandData.viewRegionBorders;
                RegionService.getInstance().getRegionCommandDataTable().update(commandData);

                if (commandData.viewRegionBorders) {
                    commandContext.sendMessage(Message.translation("server.commands.averageessentials.region.view.viewing"));
                    RegionBoundaryService.getInstance().addTrackedPlayer(playerUuid);

                } else {
                    commandContext.sendMessage(Message.translation("server.commands.averageessentials.region.view.notviewing"));
                    RegionBoundaryService.getInstance().removeTrackedPlayer(playerUuid);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected static class LimitCommand extends CommandBase {
        private final RequiredArg<Integer> maxBlocks;

        public LimitCommand() {
            super("limit", "server.commands.averageessentials.region.limit.desc");
            maxBlocks = this.withRequiredArg("blocks", "server.commands.averageessentials.region.limit.blocks.desc", com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes.INTEGER);
        }

        @Override
        protected void executeSync(@NonNullDecl CommandContext commandContext) {
            var playerUuid = commandContext.sender().getUuid();
            var maxBlocksInt = this.maxBlocks.get(commandContext);

            try {
                var region = RegionService.getInstance().getSelectedRegionGroup(playerUuid.toString());

                if (region == null) {
                    commandContext.sendMessage(Message.translation("server.commands.averageessentials.region.limit.noselected"));
                    return;
                }

                region.setMaxClaimBlocks(maxBlocksInt);
                RegionService.getInstance().updateRegionGroup(region);
                commandContext.sendMessage(Message.translation("server.commands.averageessentials.region.limit.success").param("blocks", maxBlocksInt));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected static class RenameCommand extends CommandBase {
        private final RequiredArg<String> newName;

        public RenameCommand() {
            super("rename", "server.commands.averageessentials.region.rename.desc");
            newName = this.withRequiredArg("name", "server.commands.averageessentials.region.rename.name.desc", com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes.STRING);
        }

        @Override
        protected void executeSync(@NonNullDecl CommandContext commandContext) {
            var playerUuid = commandContext.sender().getUuid();
            var newNameStr = this.newName.get(commandContext);

            try {
                var region = RegionService.getInstance().getSelectedRegionGroup(playerUuid.toString());

                if (region == null) {
                    commandContext.sendMessage(Message.translation("server.commands.averageessentials.region.rename.noselected"));
                    return;
                }

                // Check if new name already exists
                var existingRegion = RegionService.getInstance().getRegionGroup(playerUuid.toString(), newNameStr);
                if (existingRegion != null) {
                    commandContext.sendMessage(Message.translation("server.commands.averageessentials.region.rename.exists"));
                    return;
                }

                var oldName = region.groupName;
                region.groupName = newNameStr;
                RegionService.getInstance().updateRegionGroup(region);
                commandContext.sendMessage(Message.translation("server.commands.averageessentials.region.rename.success")
                        .param("oldName", oldName)
                        .param("newName", newNameStr));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected static class StatsCommand extends CommandBase {
        public StatsCommand() {
            super("stats", "server.commands.averageessentials.region.stats.desc");
        }

        @Override
        protected void executeSync(@NonNullDecl CommandContext commandContext) {
            var playerUuid = commandContext.sender().getUuid();

            try {
                var region = RegionService.getInstance().getSelectedRegionGroup(playerUuid.toString());

                if (region == null) {
                    commandContext.sendMessage(Message.translation("server.commands.averageessentials.region.stats.noselected"));
                    return;
                }

                var chunks = RegionService.getInstance().getChunksFromRegion(region);

                Message na = Message.translation("server.averageessentials.region.na");
                Message statsMessage = Message.translation("server.commands.averageessentials.region.stats.header")
                        .param("name", region.groupName);

                statsMessage = Message.join(statsMessage, Message.raw("\n"), Message.translation("server.commands.averageessentials.region.stats.entry.description")
                        .param("description", region.description != null ? region.description : na.getAnsiMessage()));

                statsMessage = Message.join(statsMessage, Message.raw("\n"), Message.translation("server.commands.averageessentials.region.stats.entry.blocks")
                        .param("currentBlocks", region.currentClaimedBlocks)
                        .param("maxBlocks", region.maxClaimBlocks));

                statsMessage = Message.join(statsMessage, Message.raw("\n"), Message.translation("server.commands.averageessentials.region.stats.entry.chunks")
                        .param("chunkCount", chunks.size()));

                statsMessage = Message.join(statsMessage, Message.raw("\n"), Message.translation("server.commands.averageessentials.region.stats.entry.flags")
                        .param("blockBreak", region.allowBlockBreak)
                        .param("blockPlace", region.allowBlockPlace)
                        .param("interaction", region.allowInteraction)
                        .param("pvp", region.pvpEnabled));

                statsMessage = Message.join(statsMessage, Message.raw("\n"), Message.translation("server.commands.averageessentials.region.stats.entry.welcomemsg")
                        .param("welcomeMsg", region.welcomeMessage != null ? region.welcomeMessage : na.getAnsiMessage()));

                statsMessage = Message.join(statsMessage, Message.raw("\n"), Message.translation("server.commands.averageessentials.region.stats.entry.leavemsg")
                        .param("leaveMsg", region.leaveMessage != null ? region.leaveMessage : na.getAnsiMessage()));

                commandContext.sendMessage(statsMessage);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected static class UnclaimCommand extends CommandBase {
        public UnclaimCommand() {
            super("unclaim", "server.commands.averageessentials.region.unclaim.desc");
        }

        @Override
        protected void executeSync(@NonNullDecl CommandContext commandContext) {
            var playerUuid = commandContext.sender().getUuid();
            var player = Universe.get().getPlayer(playerUuid);
            var pos = player.getTransform().getPosition();
            var regionZone = RegionZone.getFromPosition(pos);

            try {
                var chunk = RegionService.getInstance().getRegionChunkAtPosition(regionZone, player.getWorldUuid().toString(), playerUuid.toString());

                if (chunk == null) {
                    commandContext.sendMessage(Message.translation("server.commands.averageessentials.region.unclaim.notfound"));
                    return;
                }

                RegionService.getInstance().deleteRegionChunk(chunk);
                commandContext.sendMessage(Message.translation("server.commands.averageessentials.region.unclaim.success"));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected static class UnclaimAllCommand extends CommandBase {
        public UnclaimAllCommand() {
            super("unclaimall", "server.commands.averageessentials.region.unclaimall.desc");
        }

        @Override
        protected void executeSync(@NonNullDecl CommandContext commandContext) {
            var playerUuid = commandContext.sender().getUuid();

            try {
                var region = RegionService.getInstance().getSelectedRegionGroup(playerUuid.toString());

                if (region == null) {
                    commandContext.sendMessage(Message.translation("server.commands.averageessentials.region.unclaimall.noselected"));
                    return;
                }

                RegionService.getInstance().deleteRegionGroup(region);
                commandContext.sendMessage(Message.translation("server.commands.averageessentials.region.unclaimall.success"));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected static class ShareCommand extends CommandBase {
        private final RequiredArg<String> playerToShare;

        public ShareCommand() {
            super("share", "server.commands.averageessentials.region.share.desc");
            playerToShare = this.withRequiredArg("player", "server.commands.averageessentials.region.share.player.desc", com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes.STRING);
        }

        @Override
        protected void executeSync(@NonNullDecl CommandContext commandContext) {
            var playerUuid = commandContext.sender().getUuid();
            var playerToShareName = this.playerToShare.get(commandContext);

            try {
                var region = RegionService.getInstance().getSelectedRegionGroup(playerUuid.toString());

                if (region == null) {
                    commandContext.sendMessage(Message.translation("server.commands.averageessentials.region.share.noselected"));
                    return;
                }

                var targetPlayer = Universe.get().getPlayerByUsername(playerToShareName, NameMatching.STARTS_WITH_IGNORE_CASE);

                if (targetPlayer == null) {
                    commandContext.sendMessage(Message.translation("server.commands.averageessentials.region.share.playernotfound"));
                    return;
                }

                RegionService.getInstance().shareRegionWithPlayer(region, targetPlayer.getUuid().toString());
                commandContext.sendMessage(Message.translation("server.commands.averageessentials.region.share.success").param("player", playerToShareName));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected static class UnshareCommand extends CommandBase {
        private final RequiredArg<String> playerToUnshare;

        public UnshareCommand() {
            super("unshare", "server.commands.averageessentials.region.unshare.desc");
            playerToUnshare = this.withRequiredArg("player", "server.commands.averageessentials.region.unshare.player.desc", com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes.STRING);
        }

        @Override
        protected void executeSync(@NonNullDecl CommandContext commandContext) {
            var playerUuid = commandContext.sender().getUuid();
            var playerToUnshareName = this.playerToUnshare.get(commandContext);

            try {
                var region = RegionService.getInstance().getSelectedRegionGroup(playerUuid.toString());

                if (region == null) {
                    commandContext.sendMessage(Message.translation("server.commands.averageessentials.region.unshare.noselected"));
                    return;
                }

                var targetPlayer = Universe.get().getPlayerByUsername(playerToUnshareName, NameMatching.STARTS_WITH_IGNORE_CASE);

                if (targetPlayer == null) {
                    commandContext.sendMessage(Message.translation("server.commands.averageessentials.region.unshare.playernotfound"));
                    return;
                }

                RegionService.getInstance().unshareRegionFromPlayer(region, targetPlayer.getUuid().toString());
                commandContext.sendMessage(Message.translation("server.commands.averageessentials.region.unshare.success").param("player", playerToUnshareName));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected static class TeleportCommand extends CommandBase {
        private final RequiredArg<String> regionName;

        public TeleportCommand() {
            super("tp", "server.commands.averageessentials.region.tp.desc");
            regionName = this.withRequiredArg("regionName", "server.commands.averageessentials.region.tp.name.desc", com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes.STRING);
        }

        @Override
        protected void executeSync(@NonNullDecl CommandContext commandContext) {
            var playerUuid = commandContext.sender().getUuid();
            var regionNameArg = this.regionName.get(commandContext);

            try {
                var region = RegionService.getInstance().getRegionGroup(playerUuid.toString(), regionNameArg);
                if (region == null) {
                    commandContext.sendMessage(Message.translation("server.commands.averageessentials.region.tp.notfound"));
                    return;
                }

                var regionChunks = RegionService.getInstance().getChunksFromRegion(region);
                if (regionChunks.isEmpty()) {
                    commandContext.sendMessage(Message.translation("server.commands.averageessentials.region.tp.nochunks"));
                    return;
                }

                var player = Universe.get().getPlayer(playerUuid);
                if (player == null) return;

                var firstChunk = regionChunks.getFirst();
                var world = Universe.get().getWorld(UUID.fromString(firstChunk.worldUuid));
                if (world == null) return;

                Vector3d spawnPos = calculateSpawnPosition(world, firstChunk);
                
                var currentWorld = Universe.get().getWorld(player.getWorldUuid());
                if (currentWorld == null) return;

                currentWorld.execute(() -> {
                    if (player.getReference() == null) return;

                    var store = player.getReference().getStore();
                    var tp = new Teleport(world, spawnPos, player.getTransform().getRotation());
                    store.addComponent(player.getReference(), Teleport.getComponentType(), tp);
                });

                commandContext.sendMessage(Message.translation("server.commands.averageessentials.region.tp.success").param("region", regionNameArg));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        private Vector3d calculateSpawnPosition(com.hypixel.hytale.server.core.universe.world.World world, github.renderbr.hytale.db.models.regions.PlayerRegionChunk chunk) {
            int centerX = (chunk.firstCornerX + chunk.secondCornerX) / 2;
            int centerZ = (chunk.firstCornerZ + chunk.secondCornerZ) / 2;
            int finalY = chunk.secondCornerY;

            for (int y = chunk.secondCornerY; y >= chunk.firstCornerY; y--) {
                if (isSpawnable(world, centerX, y, centerZ)) {
                    finalY = y + 1;
                    break;
                }
            }
            return new Vector3d(centerX, finalY, centerZ);
        }

        private boolean isSpawnable(com.hypixel.hytale.server.core.universe.world.World world, int x, int y, int z) {
            var currentBlock = world.getBlock(x, y, z);
            var firstAbove = world.getBlock(x, y + 1, z);
            var secondAbove = world.getBlock(x, y + 2, z);
            return currentBlock != 0 && firstAbove == 0 && secondAbove == 0;
        }
    }

    protected static class WelcomeMessageCommand extends CommandBase {
        private final RequiredArg<String> message;

        public WelcomeMessageCommand() {
            super("welcomemsg", "server.commands.averageessentials.region.welcomemsg.desc");
            message = this.withRequiredArg("message", "server.commands.averageessentials.region.welcomemsg.message.desc", com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes.STRING);
        }

        @Override
        protected void executeSync(@NonNullDecl CommandContext commandContext) {
            var playerUuid = commandContext.sender().getUuid();
            var messageText = this.message.get(commandContext);

            try {
                var region = RegionService.getInstance().getSelectedRegionGroup(playerUuid.toString());

                if (region == null) {
                    commandContext.sendMessage(Message.translation("server.commands.averageessentials.region.welcomemsg.noselected"));
                    return;
                }

                region.setWelcomeMessage(messageText);
                RegionService.getInstance().updateRegionGroup(region);
                commandContext.sendMessage(Message.translation("server.commands.averageessentials.region.welcomemsg.success"));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected static class LeaveMessageCommand extends CommandBase {
        private final RequiredArg<String> message;

        public LeaveMessageCommand() {
            super("leavemsg", "server.commands.averageessentials.region.leavemsg.desc");
            message = this.withRequiredArg("message", "server.commands.averageessentials.region.leavemsg.message.desc", com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes.STRING);
        }

        @Override
        protected void executeSync(@NonNullDecl CommandContext commandContext) {
            var playerUuid = commandContext.sender().getUuid();
            var messageText = this.message.get(commandContext);

            try {
                var region = RegionService.getInstance().getSelectedRegionGroup(playerUuid.toString());

                if (region == null) {
                    commandContext.sendMessage(Message.translation("server.commands.averageessentials.region.leavemsg.noselected"));
                    return;
                }

                region.setLeaveMessage(messageText);
                RegionService.getInstance().updateRegionGroup(region);
                commandContext.sendMessage(Message.translation("server.commands.averageessentials.region.leavemsg.success"));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected static class DescriptionCommand extends CommandBase {
        private final RequiredArg<String> description;

        public DescriptionCommand() {
            super("desc", "server.commands.averageessentials.region.desc.desc");
            description = this.withRequiredArg("description", "server.commands.averageessentials.region.desc.description.desc", com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes.STRING);
        }

        @Override
        protected void executeSync(@NonNullDecl CommandContext commandContext) {
            var playerUuid = commandContext.sender().getUuid();
            var descriptionText = this.description.get(commandContext);

            try {
                var region = RegionService.getInstance().getSelectedRegionGroup(playerUuid.toString());

                if (region == null) {
                    commandContext.sendMessage(Message.translation("server.commands.averageessentials.region.desc.noselected"));
                    return;
                }

                region.updateDescription(descriptionText);
                RegionService.getInstance().updateRegionGroup(region);
                commandContext.sendMessage(Message.translation("server.commands.averageessentials.region.desc.success"));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected static class ColorCommand extends CommandBase {
        private final RequiredArg<String> color;

        public ColorCommand() {
            super("color", "server.commands.averageessentials.region.color.desc");
            color = this.withRequiredArg("color", "server.commands.averageessentials.region.color.color.desc", com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes.STRING);
        }

        @Override
        protected void executeSync(@NonNullDecl CommandContext commandContext) {
            var playerUuid = commandContext.sender().getUuid();
            var colorText = this.color.get(commandContext);

            try {
                var region = RegionService.getInstance().getSelectedRegionGroup(playerUuid.toString());

                if (region == null) {
                    commandContext.sendMessage(Message.translation("server.commands.averageessentials.region.color.noselected"));
                    return;
                }

                region.setBoundaryColor(colorText);
                RegionService.getInstance().updateRegionGroup(region);
                commandContext.sendMessage(Message.translation("server.commands.averageessentials.region.color.success"));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected static class MapCommand extends CommandBase {
        private final OptionalArg<Integer> page;
        private final int PAGE_SIZE = 10;

        public MapCommand() {
            super("map", "server.commands.averageessentials.region.map.desc");
            page = this.withOptionalArg("page", "server.commands.averageessentials.region.map.page.desc", com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes.INTEGER);
        }

        @Override
        protected void executeSync(@NonNullDecl CommandContext commandContext) {
            var currentPage = commandContext.get(this.page);

            try {
                var allRegions = RegionService.getInstance().getAllRegions();
                var pages = allRegions.size() / PAGE_SIZE + (allRegions.size() % PAGE_SIZE == 0 ? 0 : 1);

                if (currentPage == null || currentPage > pages) {
                    currentPage = 1;
                }

                Message rootMessage = Message.translation("server.commands.averageessentials.region.map.header").param("currentPage", currentPage).param("totalPages", pages);

                if (allRegions.isEmpty()) {
                    rootMessage = Message.join(rootMessage, Message.raw("\n"), Message.translation("server.commands.averageessentials.region.map.noregions"));
                } else {
                    for (int i = (currentPage - 1) * PAGE_SIZE; i < Math.min(currentPage * PAGE_SIZE, allRegions.size()); i++) {
                        var rg = allRegions.get(i);
                        var chunkCount = RegionService.getInstance().getChunksFromRegion(rg).size();
                        rootMessage = Message.join(rootMessage, Message.raw("\n"), Message.translation("server.commands.averageessentials.region.map.entry")
                                .param("region", rg.groupName)
                                .param("owner", Universe.get().getPlayer(UUID.fromString(rg.playerUuid)).getUsername())
                                .param("chunks", chunkCount));
                    }
                }

                commandContext.sendMessage(rootMessage);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected static class ListCommand extends CommandBase {
        private final OptionalArg<Integer> page;
        private final FlagArg listAll;
        private final int PAGE_SIZE = 10;

        public ListCommand() {
            super("list", "server.commands.averageessentials.region.list.desc");
            page = this.withOptionalArg("page", "server.commands.averageessentials.region.list.page.desc", com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes.INTEGER);
            listAll = this.withFlagArg("all", "server.commands.averageessentials.region.list.listall");
        }

        @Override
        protected void executeSync(@NonNullDecl CommandContext commandContext) {
            var playerUuid = commandContext.sender().getUuid();

            var listAllFlag = commandContext.get(this.listAll);
            if (listAllFlag && !PermissionsModule.get().hasPermission(playerUuid, "averageessentials.region.admin")) {
                commandContext.sendMessage(Message.translation("server.commands.averageessentials.region.list.nopermission"));
                return;
            }

            try {
                List<PlayerRegionGroup> regions;

                if (listAllFlag) {
                    regions = RegionService.getInstance().getAllRegions();
                } else {
                    regions = RegionService.getInstance().getRegions(playerUuid.toString());
                }

                // region.groupName
                var chunkCounts = regions.stream().collect(Collectors.toMap(
                        rg -> rg.groupName,
                        rg -> {
                            try {
                                return RegionService.getInstance().getChunksFromRegion(rg).size();
                            } catch (SQLException e) {
                                return 0;
                            }
                        }
                ));

                var pages = regions.size() / PAGE_SIZE + (regions.size() % PAGE_SIZE == 0 ? 0 : 1);
                var currentPage = commandContext.get(this.page);
                if (currentPage == null || currentPage > pages) {
                    currentPage = 1;
                }

                Message rootMessage = Message.translation("server.commands.averageessentials.region.list.header").param("currentPage", currentPage).param("totalPages", pages);
                if (regions.isEmpty()) {
                    rootMessage = Message.join(rootMessage, Message.raw("\n"), Message.translation("server.commands.averageessentials.region.list.noregions"));
                } else {
                    for (int i = (currentPage - 1) * PAGE_SIZE; i < Math.min(currentPage * PAGE_SIZE, regions.size()); i++) {
                        var rg = regions.get(i);
                        var chunkCount = chunkCounts.getOrDefault(rg.groupName, 0);
                        rootMessage = Message.join(rootMessage, Message.raw("\n"), Message.translation("server.commands.averageessentials.region.list.entry")
                                .param("region", rg.groupName)
                                .param("chunks", chunkCount));
                    }
                }

                commandContext.sendMessage(rootMessage);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected static class SelectCommand extends CommandBase {
        private final RequiredArg<String> regionName;

        public SelectCommand() {
            super("select", "server.commands.averageessentials.region.target.desc");
            this.regionName = this.withRequiredArg("regionName", "server.commands.averageessentials.region.target.arg.desc", com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes.STRING);
        }

        @Override
        protected void executeSync(@NonNullDecl CommandContext commandContext) {
            var regionName = this.regionName.get(commandContext);

            // find region
            var playerUuid = commandContext.sender().getUuid();

            try {
                PlayerRegionGroup region;

                if (PermissionsModule.get().hasPermission(playerUuid, "averageessentials.region.admin")) {
                    region = RegionService.getInstance().getRegionGroup(regionName);
                } else {
                    region = RegionService.getInstance().getRegionGroup(playerUuid.toString(), regionName);
                }

                if (region == null) {
                    commandContext.sendMessage(Message.translation("server.commands.averageessentials.region.target.notfound"));
                    return;
                }

                var player = Universe.get().getPlayer(playerUuid);
                if (player == null) {
                    return;
                }

                RegionService.getInstance().setSelectedRegionGroup(playerUuid.toString(), region);
                commandContext.sendMessage(Message.translation("server.commands.averageessentials.region.target.success").param("region", regionName));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected static class CreateCommand extends CommandBase {
        public final RequiredArg<String> regionName;

        public CreateCommand() {
            super("create", "server.commands.averageessentials.region.create.desc");
            regionName = this.withRequiredArg("regionName", "server.commands.averageessentials.region.create.arg.desc", com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes.STRING);
        }

        @Override
        protected void executeSync(@NonNullDecl CommandContext commandContext) {
            String regionName = this.regionName.get(commandContext);

            // does this already exist?
            var playerUuid = commandContext.sender().getUuid();

            try {
                var region = RegionService.getInstance().getRegionGroup(playerUuid.toString(), regionName);

                if (region != null) {
                    commandContext.sendMessage(Message.translation("server.commands.averageessentials.region.create.alreadyexists"));
                    return;
                }

                if (!RegionService.getInstance().canCreateRegion(playerUuid.toString())) {
                    commandContext.sendMessage(Message.translation("server.commands.averageessentials.region.create.limitreached"));
                    return;
                }

                RegionService.getInstance().createRegionGroup(playerUuid.toString(), regionName);
                commandContext.sendMessage(Message.translation("server.commands.averageessentials.region.create.success").param("region", regionName));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected static class ClaimCommand extends CommandBase {
        public ClaimCommand() {
            super("claim", "server.commands.averageessentials.region.claim.desc");
        }

        @Override
        protected void executeSync(@NonNullDecl CommandContext commandContext) {
            var playerUuid = commandContext.sender().getUuid();
            var player = Universe.get().getPlayer(playerUuid);
            if (player == null) return;

            var pos = player.getTransform().getPosition();
            var worldUuid = player.getWorldUuid().toString();
            var regionZone = RegionZone.getFromPosition(pos);

            try {
                var selectedRegion = getOrCreateSelectedRegion(playerUuid.toString(), player.getUsername());
                if (selectedRegion == null) {
                    commandContext.sendMessage(Message.translation("server.commands.averageessentials.region.create.limitreached"));
                    return;
                }

                if (isIntersectingOtherRegions(regionZone, worldUuid, selectedRegion)) {
                    commandContext.sendMessage(Message.translation("server.commands.averageessentials.region.claim.failure"));
                    return;
                }

                int blockCount = calculateBlockCount(regionZone);
                if (!selectedRegion.canClaim(blockCount)) {
                    sendBlockLimitMessage(commandContext, selectedRegion, blockCount);
                    return;
                }

                claimRegionChunk(playerUuid.toString(), worldUuid, selectedRegion, regionZone, blockCount);

                commandContext.sendMessage(Message.translation("server.commands.averageessentials.region.claim.success")
                        .param("blocks", blockCount)
                        .param("remainingBlocks", selectedRegion.maxClaimBlocks - selectedRegion.currentClaimedBlocks));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        private PlayerRegionGroup getOrCreateSelectedRegion(String playerUuid, String username) throws SQLException {
            var regionService = RegionService.getInstance();
            var selectedRegion = regionService.getSelectedRegionGroup(playerUuid);
            
            if (selectedRegion != null) return selectedRegion;
            if (!regionService.canCreateRegion(playerUuid)) return null;

            selectedRegion = regionService.createRegionGroup(playerUuid, username + "-" + System.currentTimeMillis());
            regionService.setSelectedRegionGroup(playerUuid, selectedRegion);
            return selectedRegion;
        }

        private boolean isIntersectingOtherRegions(RegionZone zone, String worldUuid, PlayerRegionGroup currentRegion) throws SQLException {
            var intersecting = RegionService.getInstance().getIntersectingRegionsFromRect(zone, worldUuid);
            return !intersecting.isEmpty() && intersecting.stream()
                    .anyMatch(chunk -> !chunk.regionGroup.getId().equals(currentRegion.getId()));
        }

        private int calculateBlockCount(RegionZone zone) {
            return (Math.abs(zone.secondCornerX - zone.firstCornerX) + 1) *
                   (Math.abs(zone.secondCornerY - zone.firstCornerY) + 1) *
                   (Math.abs(zone.secondCornerZ - zone.firstCornerZ) + 1);
        }

        private void sendBlockLimitMessage(CommandContext context, PlayerRegionGroup region, int requested) {
            context.sendMessage(Message.translation("server.commands.averageessentials.region.claim.blocklimit")
                    .param("currentBlocks", region.currentClaimedBlocks)
                    .param("maxBlocks", region.maxClaimBlocks)
                    .param("requestedBlocks", requested).color(Color.RED.brighter()));
        }

        private void claimRegionChunk(String playerUuid, String worldUuid, PlayerRegionGroup region, RegionZone zone, int blocks) throws SQLException {
            PlayerRegionChunk chunk = new PlayerRegionChunk();
            chunk.chunkName = "Claim-" + System.currentTimeMillis();
            chunk.playerUuid = playerUuid;
            chunk.worldUuid = worldUuid;
            chunk.regionGroup = region;
            chunk.setZone(zone);
            
            RegionService.getInstance().createRegionChunk(chunk);
            region.updateClaimedBlocks(blocks);
            RegionService.getInstance().updateRegionGroup(region);
        }
    }
}

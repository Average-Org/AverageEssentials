package github.renderbr.hytale.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import github.renderbr.hytale.registries.ProviderRegistry;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class GroupManagerCommand extends AbstractCommandCollection {

    public GroupManagerCommand() {
        super("groupmanager", "server.commands.averageessentials.gm.desc");
        this.addAliases("gm", "agm", "groupman");
        this.addSubCommand(new PrefixSubCommand());
    }

    protected static class PrefixSubCommand extends CommandBase {
        private final RequiredArg<String> groupArg;
        private final RequiredArg<String> prefixArg;

        public PrefixSubCommand() {
            super("prefix", "server.commands.averageessentials.gm.prefix.desc");
            this.groupArg = this.withRequiredArg("group", "server.commands.perm.group.list.group.desc", ArgTypes.STRING);
            this.prefixArg = this.withRequiredArg("prefix", "server.commands.averageessentials.gm.prefix.prefix_arg.desc", ArgTypes.STRING);
        }

        @Override
        protected void executeSync(@NonNullDecl CommandContext commandContext) {
            String group = this.groupArg.get(commandContext);
            String prefix = this.prefixArg.get(commandContext);

            String prefixToApply = prefix;
            if (prefix.equals("-none")) {
                prefixToApply = "";
            }

            // get rid of these
            prefixToApply = prefixToApply.replace("'", "").replace("\"", "");

            ProviderRegistry.groupManagerProvider.setGroupPrefix(group, prefixToApply);
            commandContext.sendMessage(Message.translation("server.commands.averageessentials.gm.prefix.applied.msg").param("group", group).param("prefix", prefixToApply));
        }
    }
}

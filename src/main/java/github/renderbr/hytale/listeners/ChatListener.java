package github.renderbr.hytale.listeners;

import com.hypixel.hytale.event.EventPriority;
import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.server.core.modules.accesscontrol.AccessControlModule;
import com.hypixel.hytale.server.core.modules.accesscontrol.ban.InfiniteBan;
import com.hypixel.hytale.server.core.modules.accesscontrol.provider.HytaleBanProvider;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import github.renderbr.hytale.config.ChatFilterConfigurationProvider;
import github.renderbr.hytale.config.obj.ChatFilterType;
import github.renderbr.hytale.registries.ProviderRegistry;
import util.ColorUtils;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.regex.Pattern;

public class ChatListener {

    private static HytaleBanProvider banProvider;

    public static void registerChatListeners(EventRegistry eventRegistry) throws NoSuchFieldException, IllegalAccessException {

        // Allow bans to be accessible through reflection
        Field banProviderField = AccessControlModule.class.getDeclaredField("banProvider");
        banProviderField.setAccessible(true);

        java.lang.Object value = banProviderField.get(AccessControlModule.get());
        banProvider = (HytaleBanProvider) value;

        eventRegistry.registerGlobal(EventPriority.EARLY, PlayerChatEvent.class, ChatListener::onPlayerChat);
    }

    public static void onPlayerChat(PlayerChatEvent event) {
        var configProvider = ProviderRegistry.chatFilterConfigurationProvider;
        PlayerRef sender = event.getSender();

        if (handleChatFiltering(event, sender, configProvider)) {
            event.setCancelled(true);
            return;
        }

        String prefix = getPlayerPrefix(sender.getUuid());
        String displayName = getPlayerDisplayName(sender.getUuid(), sender.getUsername());
        
        boolean allowColor = configProvider.getConfig().allowUsersToUseChatColorCodes;
        boolean canEmbed = configProvider.getConfig().allowUsersToEmbedLinks || 
                         PermissionsModule.get().hasPermission(sender.getUuid(), "averageessentials.chat.embedlinks");

        event.setFormatter((player, message) -> Message.join(
                Message.raw(prefix),
                Message.raw(displayName),
                Message.raw(": "),
                allowColor ? ColorUtils.parseColorCodes(message, canEmbed) : Message.raw(message)));
    }

    static String getPlayerPrefix(java.util.UUID uuid) {
        var groups = new HashSet<>(PermissionsModule.get().getGroupsForUser(uuid));
        groups.add("Default");

        var groupManager = ProviderRegistry.groupManagerProvider;
        var highestGroup = groupManager.getHighestWeightedGroup(groups);
        
        return groupManager.getGroupPrefix(highestGroup.first()).getAnsiMessage();
    }

    static String getPlayerDisplayName(java.util.UUID uuid, String username) {
        var nicknameProvider = ProviderRegistry.nicknameProvider;
        if (nicknameProvider != null && nicknameProvider.hasNickname(uuid.toString())) {
            return nicknameProvider.getUserNickname(uuid.toString());
        }
        return username;
    }

    static boolean handleChatFiltering(PlayerChatEvent event, PlayerRef sender, ChatFilterConfigurationProvider configProvider) {
        String content = event.getContent();

        // 1. Check bannable terms
        var bannable = configProvider.getConfig().GetTermsAsRegexPatterns(ChatFilterType.BANNABLE);
        if (containsAny(content, bannable)) {
            banPlayerForBannedWord(sender);
            return true;
        }

        // 2. Check removable terms
        var removable = configProvider.getConfig().GetTermsAsRegexPatterns(ChatFilterType.REMOVABLE);
        if (containsAny(content, removable)) {
            sender.sendMessage(Message.translation("server.averageessentials.filter.restrictedword"));
            return true;
        }

        // 3. Censor terms
        var censorable = configProvider.getConfig().GetTermsAsRegexPatterns(ChatFilterType.CENSORABLE);
        for (var pattern : censorable) {
            content = pattern.matcher(content).replaceAll("****");
        }

        event.setContent(content);
        return false;
    }

    private static boolean containsAny(String content, ArrayList<Pattern> patterns) {
        for (var pattern : patterns) {
            if (pattern.matcher(content).find()) {
                return true;
            }
        }
        return false;
    }

    private static void banPlayerForBannedWord(PlayerRef sender) {
        InfiniteBan ban = new InfiniteBan(sender.getUuid(),
                sender.getWorldUuid(),
                Instant.now(),
                Message.translation("server.averageessentials.ban.forusingbannedword").getAnsiMessage());

        banProvider.modify(banMap -> {
            banMap.put(sender.getUuid(), ban);
            return true;
        });

        if (sender.isValid()) {
            sender.getPacketHandler().disconnect(
                Message.translation("server.averageessentials.ban.forusingbannedword").getAnsiMessage()
            );
        }
    }
}

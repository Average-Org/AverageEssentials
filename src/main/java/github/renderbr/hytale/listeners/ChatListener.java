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
import github.renderbr.hytale.config.obj.ChatFilterType;
import github.renderbr.hytale.registries.ProviderRegistry;
import util.ColorUtils;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.ArrayList;
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
        // regex for banned terms
        var chatFilterConfigurationProvider = ProviderRegistry.chatFilterConfigurationProvider;

        var bannableTerms = chatFilterConfigurationProvider.config.GetTermsAsRegexPatterns(ChatFilterType.BANNABLE);

        var chatContent = event.getContent();
        PlayerRef sender = event.getSender();

        if (handleBannableTerms(event, bannableTerms, chatContent, sender)) return;

        var removableTerms = chatFilterConfigurationProvider.config.GetTermsAsRegexPatterns(ChatFilterType.REMOVABLE);
        if (handleRemovableTerms(event, removableTerms, chatContent, sender)) return;

        var censorableTerms = chatFilterConfigurationProvider.config.GetTermsAsRegexPatterns(ChatFilterType.CENSORABLE);

        for (var regex : censorableTerms) {
            chatContent = regex.matcher(chatContent).replaceAll("****");
        }

        event.setContent(chatContent);

        var groups = PermissionsModule.get().getGroupsForUser(sender.getUuid());

        var groupManager = ProviderRegistry.groupManagerProvider;

        // get highest weighted group
        var highestWeightedGroup = groupManager.getHighestWeightedGroup(groups);
        var prefix = groupManager.getGroupPrefix(highestWeightedGroup.first());

        event.setFormatter((playerRef, message) -> Message.join(
                prefix,
                Message.raw(playerRef.getUsername()),
                Message.raw(": "),
                chatFilterConfigurationProvider.config.allowUsersToUseChatColorCodes ? ColorUtils.parseColorCodes(message) : Message.raw(message)));
    }

    private static boolean handleRemovableTerms(PlayerChatEvent event, ArrayList<Pattern> removableTerms, String chatContent, PlayerRef sender) {
        for (var regex : removableTerms) {
            if (regex.matcher(chatContent).find()) {
                sender.sendMessage(Message.translation("server.averageessentials.filter.restrictedword"));
                event.setCancelled(true);
                return true;
            }
        }
        return false;
    }

    private static boolean handleBannableTerms(PlayerChatEvent event, ArrayList<Pattern> bannableTerms, String chatContent, PlayerRef sender) {
        for (var regex : bannableTerms) {
            if (regex.matcher(chatContent).find()) {
                event.setCancelled(true);
                InfiniteBan ban = new InfiniteBan(sender.getUuid(),
                        sender.getWorldUuid(),
                        Instant.now(),
                        Message.translation("server.averageessentials.ban.forusingbannedword").getAnsiMessage());

                banProvider.modify((banMap) -> {
                    banMap.put(sender.getUuid(), ban);
                    return true;
                });

                if (sender.isValid()) {
                    // disconnect
                    var disconnectReason = ban.getDisconnectReason(sender.getUuid());

                    disconnectReason.whenComplete((_reason, disconnectEx) -> {
                        var disconnectReasonMsg = _reason;

                        if (disconnectEx != null) {
                            disconnectEx.printStackTrace();
                        }

                        if(disconnectReasonMsg.isEmpty()){
                            disconnectReasonMsg = Optional.of("You have been banned from the server.");
                        }

                        sender.getPacketHandler().disconnect(disconnectReasonMsg.get());
                    });
                }

                return true;
            }
        }
        return false;
    }
}

package github.renderbr.hytale.listeners;

import github.renderbr.hytale.config.NicknameProvider;
import github.renderbr.hytale.registries.ProviderRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class ChatListenerTest {

    private NicknameProvider nicknameProvider;
    private UUID playerUuid = UUID.randomUUID();

    @BeforeEach
    public void setUp() {
        nicknameProvider = Mockito.mock(NicknameProvider.class);
        ProviderRegistry.nicknameProvider = nicknameProvider;
    }

    @Test
    public void testGetPlayerDisplayNameWithNickname() {
        when(nicknameProvider.hasNickname(playerUuid.toString())).thenReturn(true);
        when(nicknameProvider.getUserNickname(playerUuid.toString())).thenReturn("CustomNick");

        String displayName = ChatListener.getPlayerDisplayName(playerUuid, "OriginalName");
        assertEquals("CustomNick", displayName);
    }

    @Test
    public void testGetPlayerDisplayNameNoNickname() {
        when(nicknameProvider.hasNickname(playerUuid.toString())).thenReturn(false);

        String displayName = ChatListener.getPlayerDisplayName(playerUuid, "OriginalName");
        assertEquals("OriginalName", displayName);
    }
}
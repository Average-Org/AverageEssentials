package github.renderbr.hytale.db.models.regions;

import com.hypixel.hytale.math.vector.Vector3f;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.concurrent.ThreadLocalRandom;

@DatabaseTable(tableName = "player_region_groups")
public class PlayerRegionGroup {
    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField(canBeNull = false)
    public String groupName;

    @DatabaseField(canBeNull = false)
    public String playerUuid;

    @DatabaseField
    public String description;

    @DatabaseField
    public String welcomeMessage;

    @DatabaseField
    public String leaveMessage;

    @DatabaseField
    public String boundaryColor = getRandomHexColor();

    public Long getId(){
        return id;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void setWelcomeMessage(String welcomeMessage) {
        this.welcomeMessage = welcomeMessage;
    }

    public void setLeaveMessage(String leaveMessage) {
        this.leaveMessage = leaveMessage;
    }

    public void setBoundaryColor(String boundaryColor) {
        this.boundaryColor = boundaryColor;
    }

    // Region flags for additional configurations
    @DatabaseField
    public boolean allowBlockBreak = false;

    @DatabaseField
    public boolean allowBlockPlace = false;

    @DatabaseField
    public boolean allowInteraction = false;

    @DatabaseField
    public boolean pvpEnabled = false;

    @DatabaseField
    public int maxClaimBlocks = 250000;

    @DatabaseField
    public int currentClaimedBlocks = 0;

    public boolean canClaim(int additionalBlocks) {
        return currentClaimedBlocks + additionalBlocks <= maxClaimBlocks;
    }

    public void updateClaimedBlocks(int blocks) {
        this.currentClaimedBlocks += blocks;
    }

    public void setMaxClaimBlocks(int maxClaimBlocks) {
        this.maxClaimBlocks = maxClaimBlocks;
    }

    public void setRegionFlags(boolean blockBreak, boolean blockPlace, boolean interaction, boolean pvp) {
        this.allowBlockBreak = blockBreak;
        this.allowBlockPlace = blockPlace;
        this.allowInteraction = interaction;
        this.pvpEnabled = pvp;
    }

    public static String getRandomHexColor() {
        return String.format("#%06X", ThreadLocalRandom.current().nextInt(0xFFFFFF + 1));
    }

    public static com.hypixel.hytale.protocol.Vector3f hexToVector3f(String hexColor) {
        String cleanHex = hexColor.replace("#", "").replace("0x", "");

        int color = Integer.parseInt(cleanHex, 16);

        float r = (float) ((color >> 16) & 0xFF);
        float g = (float) ((color >> 8) & 0xFF);
        float b = (float) (color & 0xFF);

        return new com.hypixel.hytale.protocol.Vector3f(r / 255.0f, g / 255.0f, b / 255.0f);
    }
}

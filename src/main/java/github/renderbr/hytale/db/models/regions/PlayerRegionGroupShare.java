package github.renderbr.hytale.db.models.regions;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "player_region_group_shares")
public class PlayerRegionGroupShare {
    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    public PlayerRegionGroup regionGroup;

    @DatabaseField(canBeNull = false)
    public String playerUuid;
}

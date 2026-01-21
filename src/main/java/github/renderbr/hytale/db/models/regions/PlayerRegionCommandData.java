package github.renderbr.hytale.db.models.regions;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "region_cmd_data")
public class PlayerRegionCommandData {
    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField(canBeNull = false)
    public String playerUuid;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    public PlayerRegionGroup currentSelectedRegion;

    @DatabaseField(canBeNull = false)
    public boolean viewRegionBorders = false;
}

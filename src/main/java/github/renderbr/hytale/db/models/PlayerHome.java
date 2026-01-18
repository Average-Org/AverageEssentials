package github.renderbr.hytale.db.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "player_homes")
public class PlayerHome {

    @DatabaseField(id = true, generatedId = true)
    private Long id;

    @DatabaseField(canBeNull = false)
    public String playerUuid;

    @DatabaseField(canBeNull = false)
    public String homeName;

    @DatabaseField(canBeNull = false)
    public int positionX;

    @DatabaseField(canBeNull = false)
    public int positionY;

    @DatabaseField(canBeNull = false)
    public int positionZ;

    public PlayerHome(){}
}

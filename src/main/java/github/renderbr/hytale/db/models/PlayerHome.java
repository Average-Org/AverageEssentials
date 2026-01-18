package github.renderbr.hytale.db.models;

import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "player_homes")
public class PlayerHome {

    @DatabaseField(generatedId = true)
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

    @DatabaseField(canBeNull = false)
    public String worldUuid;

    @DatabaseField(canBeNull = false)
    public int headRotX;

    @DatabaseField(canBeNull = false)
    public int headRotY;

    @DatabaseField(canBeNull = false)
    public int headRotZ;

    public void setPosition(Vector3d position){
        this.positionX = (int) position.getX();
        this.positionY = (int) position.getY();
        this.positionZ = (int) position.getZ();
    }

    public void setHeadRotation(Vector3f rotation){
        this.headRotX = (int) rotation.getX();
        this.headRotY = (int) rotation.getY();
        this.headRotZ = (int) rotation.getZ();
    }

    public Vector3d getPosition(){
        return new Vector3d(positionX, positionY, positionZ);
    }

    public Vector3f getHeadRotation(){
        return new Vector3f(headRotX, headRotY, headRotZ);
    }

    public PlayerHome(){}
}

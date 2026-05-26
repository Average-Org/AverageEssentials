package github.renderbr.hytale.db.models;

import com.hypixel.hytale.math.vector.Rotation3f;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.joml.Vector3d;
import org.joml.Vector3f;

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
        this.positionX = (int) position.x();
        this.positionY = (int) position.y();
        this.positionZ = (int) position.z();
    }

    public void setHeadRotation(Rotation3f rotation){
        this.headRotX = (int) rotation.x();
        this.headRotY = (int) rotation.y();
        this.headRotZ = (int) rotation.z();
    }

    public Vector3d getPosition(){
        return new Vector3d(positionX, positionY, positionZ);
    }

    public Rotation3f getHeadRotation(){
        return new Rotation3f(headRotX, headRotY, headRotZ);
    }

    public PlayerHome(){}
}

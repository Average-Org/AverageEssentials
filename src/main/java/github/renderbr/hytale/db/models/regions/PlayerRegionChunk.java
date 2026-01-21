package github.renderbr.hytale.db.models.regions;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import github.renderbr.hytale.db.models.regions.param.RegionZone;

@DatabaseTable(tableName = "player_region_chunks")
public class PlayerRegionChunk {

    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField(canBeNull = false)
    public String playerUuid;

    @DatabaseField(canBeNull = false)
    public String chunkName;

    @DatabaseField(canBeNull = false)
    public String worldUuid;

    @DatabaseField(canBeNull = false)
    public int firstCornerX;

    @DatabaseField(canBeNull = false)
    public int firstCornerY;

    @DatabaseField(canBeNull = false)
    public int firstCornerZ;

    @DatabaseField(canBeNull = false)
    public int secondCornerX;

    @DatabaseField(canBeNull = false)
    public int secondCornerY;

    @DatabaseField(canBeNull = false)
    public int secondCornerZ;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    public PlayerRegionGroup regionGroup;

    public PlayerRegionChunk(){}

    public void setFirstCorner(int x, int y, int z){
        this.firstCornerX = x;
        this.firstCornerY = y;
        this.firstCornerZ = z;
    }

    public void setSecondCorner(int x, int y, int z){
        this.secondCornerX = x;
        this.secondCornerY = y;
        this.secondCornerZ = z;
    }

    public void setZone(RegionZone zone){
        this.firstCornerX = zone.firstCornerX;
        this.firstCornerY = zone.firstCornerY;
        this.firstCornerZ = zone.firstCornerZ;
        this.secondCornerX = zone.secondCornerX;
        this.secondCornerY = zone.secondCornerY;
        this.secondCornerZ = zone.secondCornerZ;
    }

    public float[] getMatrix(){
        var width = secondCornerX - firstCornerX;
        var height = secondCornerY - firstCornerY;
        var depth = secondCornerZ - firstCornerZ;

        var centerX = (firstCornerX + secondCornerX) / 2;
        var centerY = (firstCornerY + secondCornerY) / 2;
        var centerZ = (firstCornerZ + secondCornerZ) / 2;

        float[] matrix = new float[16];
        matrix[0] = width;
        matrix[5] = height;
        matrix[10] = depth;
        matrix[15] = 1.0F;
        matrix[12] = centerX;
        matrix[13] = centerY;
        matrix[14] = centerZ;
        return matrix;
    }

}

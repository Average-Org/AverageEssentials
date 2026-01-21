package github.renderbr.hytale.db.models.regions.param;

import com.hypixel.hytale.math.vector.Vector3d;

public class RegionZone {
    public int firstCornerX;
    public int firstCornerY;
    public int firstCornerZ;

    public int secondCornerX;
    public int secondCornerY;
    public int secondCornerZ;

    public RegionZone(int firstCornerX, int firstCornerY, int firstCornerZ, int secondCornerX, int secondCornerY, int secondCornerZ){
        this.firstCornerX = firstCornerX;
        this.firstCornerY = firstCornerY;
        this.firstCornerZ = firstCornerZ;
        this.secondCornerX = secondCornerX;
        this.secondCornerY = secondCornerY;
        this.secondCornerZ = secondCornerZ;
    }

    public static RegionZone getFromPosition(Vector3d pos){
        return getFromPosition(pos, 15);
    }

    public static RegionZone getFromPosition(Vector3d pos, int radius){
        RegionZone zone = new RegionZone(0, 0, 0, 0, 0, 0);
        zone.firstCornerX = (int) pos.x - radius;
        zone.firstCornerY = (int) pos.y - radius;
        zone.firstCornerZ = (int) pos.z - radius;
        zone.secondCornerX = (int) pos.x + radius;
        zone.secondCornerY = (int) pos.y + radius;
        zone.secondCornerZ = (int) pos.z + radius;
        return zone;
    }
}

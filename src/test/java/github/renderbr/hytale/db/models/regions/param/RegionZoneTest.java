package github.renderbr.hytale.db.models.regions.param;

import org.joml.Vector3d;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RegionZoneTest {

    @Test
    public void testGetFromPositionDefaultRadius() {
        Vector3d pos = new Vector3d(100, 50, 100);
        RegionZone zone = RegionZone.getFromPosition(pos);
        
        assertEquals(100 - 15, zone.firstCornerX);
        assertEquals(50 - 15, zone.firstCornerY);
        assertEquals(100 - 15, zone.firstCornerZ);
        
        assertEquals(100 + 15, zone.secondCornerX);
        assertEquals(50 + 15, zone.secondCornerY);
        assertEquals(100 + 15, zone.secondCornerZ);
    }

    @Test
    public void testGetFromPositionCustomRadius() {
        Vector3d pos = new Vector3d(10, 20, 30);
        int radius = 5;
        RegionZone zone = RegionZone.getFromPosition(pos, radius);
        
        assertEquals(5, zone.firstCornerX);
        assertEquals(15, zone.firstCornerY);
        assertEquals(25, zone.firstCornerZ);
        
        assertEquals(15, zone.secondCornerX);
        assertEquals(25, zone.secondCornerY);
        assertEquals(35, zone.secondCornerZ);
    }
}

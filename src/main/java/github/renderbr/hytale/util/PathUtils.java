package github.renderbr.hytale.util;

import com.hypixel.hytale.server.core.util.io.BlockingDiskFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathUtils
{
    public static Path getPathForConfig(String configFile){
        return Paths.get("mods", "AverageEssentials", configFile);
    }

    public static void initializeAndEnsurePathing(Path path, BlockingDiskFile provider){
        try {
            // Ensure the folder 'mods/AverageEssentials' exists
            Files.createDirectories(path.getParent());

            // BlockingDiskFile usually has a 'load' or 'init' method
            // that checks if the file exists and calls read() or create()
            provider.syncLoad();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

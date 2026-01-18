package github.renderbr.hytale.config;

import com.google.gson.*;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.util.io.BlockingDiskFile;
import github.renderbr.hytale.config.obj.GroupConfigObject;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import util.ColorUtils;
import util.PathUtils;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class GroupManagerProvider extends BlockingDiskFile {
    @Nonnull
    private final Map<String, GroupConfigObject> groups = new Object2ObjectOpenHashMap<>();

    @Nonnull
    private static final Map<String, GroupConfigObject> DEFAULT_GROUPS = Map.of(
            "Default", new GroupConfigObject().setPrefix("[User] ").setWeight(0),
            "OP", new GroupConfigObject().setPrefix("[OP] ").setWeight(100)
    );

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();


    public static HashMap<String, Message> computedPrefixes = new HashMap<>();

    public GroupManagerProvider() {
        super(util.PathUtils.getPathForConfig("groups.json"));
        var path = PathUtils.getPathForConfig("groups.json");
        PathUtils.initializeAndEnsurePathing(path, this);
    }

    public Message computePrefix(@Nonnull String groupName) {
        GroupConfigObject group = this.groups.get(groupName);
        String rawPrefix = (group != null) ? group.getPrefix() : "";

        if (rawPrefix.isEmpty()) {
            return Message.empty();
        }

        // This handles the &4 -> Red, &b -> Aqua logic
        return ColorUtils.parseColorCodes(rawPrefix);
    }

    public Pair<String, GroupConfigObject> getHighestWeightedGroup(Set<String> groups) {
        int highestGroupId = Integer.MIN_VALUE;
        String highestGroupName = "";
        for (String groupName : groups) {
            GroupConfigObject group = this.groups.get(groupName);
            if (group != null && group.getWeight() > highestGroupId) {
                highestGroupId = group.getWeight();
                highestGroupName = groupName;
            }
        }

        return Pair.of(highestGroupName, this.groups.get(highestGroupName));
    }

    public Message getGroupPrefix(@Nonnull String groupName) {
        if (computedPrefixes.containsKey(groupName)) {
            return computedPrefixes.get(groupName);
        }

        computedPrefixes.put(groupName, computePrefix(groupName));
        return computedPrefixes.get(groupName);
    }

    public void setGroupPrefix(@Nonnull String groupName, @Nonnull String prefix) {
        this.fileLock.writeLock().lock();

        try {
            GroupConfigObject group = this.groups.get(groupName);
            if (group != null) {
                group.setPrefix(prefix);
            } else {
                this.groups.put(groupName, new GroupConfigObject().setPrefix(prefix));
            }

            if (prefix.isEmpty()) {
                computedPrefixes.put(groupName, Message.empty());
            } else {
                computedPrefixes.put(groupName, ColorUtils.parseColorCodes(prefix));
            }

            this.syncSave();
        } finally {
            this.fileLock.writeLock().unlock();
        }
    }

    @Override
    protected void read(BufferedReader bufferedReader) throws IOException {
        JsonObject root = JsonParser.parseReader(bufferedReader).getAsJsonObject();

        this.groups.clear();

        // Read Group Configs (The Prefixes)
        if (root.has("groups")) {
            JsonObject groups = root.getAsJsonObject("groups");
            for (Map.Entry<String, JsonElement> entry : groups.entrySet()) {
                GroupConfigObject config = GSON.fromJson(entry.getValue(), GroupConfigObject.class);
                this.groups.put(entry.getKey(), config);
            }
        }

        // Ensure Defaults exist if they weren't in the file
        DEFAULT_GROUPS.forEach(this.groups::putIfAbsent);
    }

    @Override
    protected void write(BufferedWriter bufferedWriter) throws IOException {
        JsonObject root = new JsonObject();

        root.add("groups", GSON.toJsonTree(this.groups));

        bufferedWriter.write(GSON.toJson(root));
    }

    @Override
    protected void create(@Nonnull BufferedWriter fileWriter) throws IOException {
        JsonObject root = new JsonObject();
        root.add("groups", GSON.toJsonTree(DEFAULT_GROUPS));
        fileWriter.write(GSON.toJson(root));
    }
}

package github.renderbr.hytale.config;

import com.google.gson.*;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.util.EventTitleUtil;
import com.hypixel.hytale.server.core.util.io.BlockingDiskFile;
import github.renderbr.hytale.commands.BasicOutputCommand;
import github.renderbr.hytale.config.obj.InformationalMessageConfiguration;
import github.renderbr.hytale.registries.CommandRegistry;
import github.renderbr.hytale.util.ColorUtils;
import github.renderbr.hytale.util.PathUtils;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public final class InformationalMessageProvider extends BlockingDiskFile {
    @Nonnull
    public InformationalMessageConfiguration config = new InformationalMessageConfiguration();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    ScheduledFuture<?> broadcastTimer;

    Queue<String> messageQueue = new java.util.concurrent.ConcurrentLinkedQueue<>();

    public InformationalMessageProvider() {
        super(PathUtils.getPathForConfig("messages.json"));
        var path = PathUtils.getPathForConfig("messages.json");
        PathUtils.initializeAndEnsurePathing(path, this);
        registerDynamicInformationMessageCommands();

        // start timer

        messageQueue.addAll(config.occasionalBroadcasts);
        broadcastTimer = scheduler.scheduleAtFixedRate(this::doBroadcast, config.broadcastFrequencyInSeconds, config.broadcastFrequencyInSeconds, TimeUnit.SECONDS);
    }

    private void doBroadcast(){
        if(messageQueue.isEmpty()){
            messageQueue.addAll(config.occasionalBroadcasts);
        }

        String message = messageQueue.poll();
        if(message != null && !message.isEmpty()){
            Universe.get().getPlayers().forEach(playerRef -> {
                if(!playerRef.isValid()){
                    return;
                }

                playerRef.sendMessage(ColorUtils.parseColorCodes(message));
            });
        }
    }

    @Override
    protected void read(BufferedReader bufferedReader) throws IOException {
        JsonObject root = JsonParser.parseReader(bufferedReader).getAsJsonObject();

        if (root.has("config")) {
            this.config = GSON.fromJson(root.get("config"), InformationalMessageConfiguration.class);
            registerDynamicInformationMessageCommands();

            if(broadcastTimer != null) {
                broadcastTimer.cancel(false);
                messageQueue.clear();

                messageQueue.addAll(config.occasionalBroadcasts);
                broadcastTimer = scheduler.scheduleAtFixedRate(this::doBroadcast, config.broadcastFrequencyInSeconds, config.broadcastFrequencyInSeconds, TimeUnit.SECONDS);
            }
        }
    }

    private void registerDynamicInformationMessageCommands() {
        var commandRegistry = CommandRegistry.getHytaleCommandRegistry();

        this.config.commandInfoMessages.forEach((key, message) -> {
            commandRegistry.registerCommand(new BasicOutputCommand(key, message));
        });
    }

    @Override
    protected void write(BufferedWriter bufferedWriter) throws IOException {
        JsonObject root = new JsonObject();

        root.add("config", GSON.toJsonTree(this.config));

        bufferedWriter.write(GSON.toJson(root));
    }

    @Override
    protected void create(@Nonnull BufferedWriter fileWriter) throws IOException {
        JsonObject root = new JsonObject();
        root.add("config", GSON.toJsonTree(this.config));
        fileWriter.write(GSON.toJson(root));
    }
}

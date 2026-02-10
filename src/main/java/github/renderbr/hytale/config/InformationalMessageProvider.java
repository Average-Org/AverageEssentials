package github.renderbr.hytale.config;

import com.google.gson.*;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.command.system.CommandRegistration;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.util.io.BlockingDiskFile;
import github.renderbr.hytale.commands.BasicOutputCommand;
import github.renderbr.hytale.config.obj.InformationalMessageConfiguration;
import github.renderbr.hytale.registries.CommandRegistry;
import util.ColorUtils;
import util.PathUtils;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public final class InformationalMessageProvider extends BlockingDiskFile {
    private static final String CONFIG_FILE = "messages.json";
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    @Nonnull
    public InformationalMessageConfiguration config = new InformationalMessageConfiguration();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    ScheduledFuture<?> broadcastTimer;

    Queue<String> messageQueue = new java.util.concurrent.ConcurrentLinkedQueue<>();
    List<CommandRegistration> registeredCommands = new ArrayList<>();

    public InformationalMessageProvider() {
        super(PathUtils.getPathForConfig(CONFIG_FILE));
        var path = PathUtils.getPathForConfig(CONFIG_FILE);
        PathUtils.initializeAndEnsurePathing(path, this);
        reload();
    }

    private void doBroadcast() {
        try {
            if (config.occasionalBroadcasts.isEmpty()) {
                return;
            }

            if (messageQueue.isEmpty()) {
                messageQueue.addAll(config.occasionalBroadcasts);
            }

            String message = messageQueue.poll();
            if (message != null && !message.isEmpty()) {
                Universe.get().getPlayers().forEach(playerRef -> {
                    if (!playerRef.isValid()) {
                        return;
                    }

                    playerRef.sendMessage(ColorUtils.parseColorCodes(message));
                });
            }
        } catch (Exception e) {
            LOGGER.atSevere().withCause(e).log("Failed to broadcast message");
        }
    }

    @Override
    protected void read(BufferedReader bufferedReader) {
        JsonObject root = JsonParser.parseReader(bufferedReader).getAsJsonObject();

        if (root.has("config")) {
            this.config = GSON.fromJson(root.get("config"), InformationalMessageConfiguration.class);
            reload();
        }
    }

    @Override
    public void syncLoad() {
        super.syncLoad();
        reload();
    }

    public void reload() {
        if (broadcastTimer != null) {
            broadcastTimer.cancel(false);
            messageQueue.clear();
        }

        registeredCommands.forEach(CommandRegistration::unregister);
        registeredCommands.clear();

        registerDynamicInformationMessageCommands();

        messageQueue.addAll(config.occasionalBroadcasts);
        startBroadcastTimer();
    }

    public void shutdown(){
        scheduler.shutdown();
        try {
            if(!scheduler.awaitTermination(1, TimeUnit.SECONDS)){
                scheduler.shutdownNow();
            }
        } catch(InterruptedException e){
            scheduler.shutdownNow();
        }
    }

    public void startBroadcastTimer() {
        broadcastTimer = scheduler.scheduleAtFixedRate(this::doBroadcast, config.broadcastFrequencyInSeconds, config.broadcastFrequencyInSeconds, TimeUnit.SECONDS);
    }

    private void registerDynamicInformationMessageCommands() {
        var commandRegistry = CommandRegistry.getHytaleCommandRegistry();

        this.config.commandInfoMessages.forEach((key, message) -> {
            var command = new BasicOutputCommand(key, message);
            registeredCommands.add(commandRegistry.registerCommand(command));
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

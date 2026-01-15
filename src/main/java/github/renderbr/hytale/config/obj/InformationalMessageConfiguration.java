package github.renderbr.hytale.config.obj;

import java.util.List;
import java.util.Map;

public class InformationalMessageConfiguration {
    public String welcomeMessage = "Welcome to the server!";

    public Integer broadcastFrequencyInSeconds = 300;

    public List<String> occasionalBroadcasts = List.of(
            "Did you know that this is the best server ever?",
            "Yeah, it's true. We didn't believe it either."
    );

    public Map<String, String> commandInfoMessages = Map.of(
            "discord", "Join our Discord server at https://discord.gg/8pU9zFGRDR"
    );
}

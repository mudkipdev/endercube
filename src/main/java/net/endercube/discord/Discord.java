package net.endercube.discord;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.endercube.discord.listener.AsyncPlayerConfiguration;
import net.endercube.discord.listener.PlayerDisconnect;
import net.endercube.discord.listener.PlayerParkourWorldRecord;
import net.endercube.utility.config.ConfigFile;
import net.minestom.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Discord {

    private static final ConfigFile config;
    public static Logger logger;
    public static WebhookClient webhookClient;

    static {
        logger = LoggerFactory.getLogger(Discord.class);
        config = new ConfigFile("net/endercube/discord", "The Discord bots settings");
    }

    public static void init() {
        logger.info("Initialising Discord integration");

        // Initialise a webhookClient
        WebhookClientBuilder builder = new WebhookClientBuilder(
                config.getOrSetDefault(config.getConfig().node("webhookURL"), "<The webhook's url>")
        );

        builder.setThreadFactory((job) -> {
            Thread thread = new Thread(job);
            thread.setName("Discord integration");
            thread.setDaemon(true);
            return thread;
        });
        builder.setWait(true);

        webhookClient = builder.build();

        // Register Events
        MinecraftServer.getGlobalEventHandler()
                .addListener(new PlayerDisconnect())
                .addListener(new PlayerParkourWorldRecord())
                .addListener(new AsyncPlayerConfiguration());

        // Say the server has started
        webhookClient.send(new WebhookMessageBuilder()
                .setUsername("Endercube") // use this username
                .setAvatarUrl("https://raw.githubusercontent.com/Ender-Cube/Branding/main/Logo/EndercubeSquare_1024x1024.png")
                .setContent("The server has started!")
                .build());

        // Say when the server has stopped
        Runtime.getRuntime().addShutdownHook((new Thread(() -> {
            webhookClient.send(new WebhookMessageBuilder()
                    .setUsername("Endercube") // use this username
                    .setAvatarUrl("https://raw.githubusercontent.com/Ender-Cube/Branding/main/Logo/EndercubeSquare_1024x1024.png") // use this avatar
                    .setContent("The server is shutting down :(")
                    .build());
        })));
    }
}
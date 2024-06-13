package net.endercube.discord.listener;

import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.endercube.discord.Discord;
import net.endercube.utility.EndercubePlayer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import org.jetbrains.annotations.NotNull;

public class AsyncPlayerConfiguration implements EventListener<AsyncPlayerConfigurationEvent> {
    @Override
    public @NotNull Class<AsyncPlayerConfigurationEvent> eventType() {
        return AsyncPlayerConfigurationEvent.class;
    }

    @Override
    public @NotNull Result run(@NotNull AsyncPlayerConfigurationEvent event) {
        EndercubePlayer player = (EndercubePlayer) event.getPlayer();
        Discord.webhookClient.send(new WebhookMessageBuilder()
                .setUsername(player.getUsername()) // use this username
                .setAvatarUrl("https://mc-heads.net/avatar/" + player.getUuid()) // use this avatar
                .setContent("I just joined the server! (" + (MinecraftServer.getConnectionManager().getOnlinePlayerCount() + 1) + ")")
                .build());
        return Result.SUCCESS;
    }
}

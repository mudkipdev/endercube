package net.endercube.discord.listener;

import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.endercube.discord.Discord;
import net.endercube.utility.EndercubePlayer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerDisconnect implements EventListener<PlayerDisconnectEvent> {
    @Override
    public @NotNull Class<PlayerDisconnectEvent> eventType() {
        return PlayerDisconnectEvent.class;
    }

    @Override
    public @NotNull Result run(@NotNull PlayerDisconnectEvent event) {
        EndercubePlayer player = (EndercubePlayer) event.getPlayer();
        Discord.webhookClient.send(new WebhookMessageBuilder()
                .setUsername(player.getUsername()) // use this username
                .setAvatarUrl("https://mc-heads.net/avatar/" + player.getUuid()) // use this avatar
                .setContent("I just left the server :( (" + MinecraftServer.getConnectionManager().getOnlinePlayerCount() + ")")
                .build());
        return Result.SUCCESS;
    }
}

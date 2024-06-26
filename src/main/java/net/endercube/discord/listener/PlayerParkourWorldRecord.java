package net.endercube.discord.listener;

import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.endercube.parkour.event.PlayerParkourWorldRecordEvent;
import net.endercube.utility.ComponentUtils;
import net.endercube.utility.EndercubePlayer;
import net.minestom.server.event.EventListener;
import org.jetbrains.annotations.NotNull;

import static net.endercube.discord.Discord.webhookClient;

public class PlayerParkourWorldRecord implements EventListener<PlayerParkourWorldRecordEvent> {
    @Override
    public @NotNull Class<PlayerParkourWorldRecordEvent> eventType() {
        return PlayerParkourWorldRecordEvent.class;
    }

    @Override
    public @NotNull Result run(@NotNull PlayerParkourWorldRecordEvent event) {
        EndercubePlayer player = (EndercubePlayer) event.getPlayer();
        // Send a test message
        webhookClient.send(new WebhookMessageBuilder()
                .setUsername(player.getUsername()) // use this username
                .setAvatarUrl("https://mc-heads.net/avatar/" + player.getUuid()) // use this avatar
                .setContent(player.getUsername() + " Just got a new world record of " + ComponentUtils.toHumanReadableTime(event.record()) + " on " + event.map() + " \uD83C\uDF89")
                .build());
        return Result.SUCCESS;
    }
}

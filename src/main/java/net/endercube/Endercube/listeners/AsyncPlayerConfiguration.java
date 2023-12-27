package net.endercube.Endercube.listeners;

import net.endercube.Common.EndercubeMinigame;
import net.endercube.Common.players.EndercubePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.ansi.ANSIComponentSerializer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.adventure.audience.Audiences;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.endercube.Endercube.Main.endercubeServer;
import static net.endercube.Endercube.Main.logger;

public class AsyncPlayerConfiguration implements EventListener<AsyncPlayerConfigurationEvent> {
    @Override
    public @NotNull Class<AsyncPlayerConfigurationEvent> eventType() {
        return AsyncPlayerConfigurationEvent.class;
    }

    @Override
    public @NotNull Result run(@NotNull AsyncPlayerConfigurationEvent event) {
        EndercubePlayer player = (EndercubePlayer) event.getPlayer();
        if (endercubeServer == null) {
            logger.warn(player.getUsername() + " Tried to log in before endercubeServer was initialised");
            player.kick("Please try again");
            return Result.EXCEPTION;
        }
        @Nullable EndercubeMinigame hubMinigame = endercubeServer.getMinigameByName("hub");


        if (hubMinigame == null) {
            logger.error("hub minigame does not exist. Please create a minigame called \"hub\"");
            MinecraftServer.stopCleanly();
            return Result.SUCCESS;
        }

        // Tell players, and the log, that someone joined
        Component playerJoinMessage = player
                .getName()
                .append(Component.text(" joined the server"))
                .color(NamedTextColor.YELLOW);

        Audiences.players().sendMessage(playerJoinMessage);
        logger.info(ANSIComponentSerializer.ansi().serialize(playerJoinMessage));

        // Set the spawning instance and position
        event.setSpawningInstance(hubMinigame.getInstances().get(0));

        // Set the respawnPoint
        Pos respawnPoint = hubMinigame.getInstances().get(0).getTag(Tag.Transient("spawnPos"));
        player.setRespawnPoint(respawnPoint);

        // Init the current minigame
        player.setCurrentMinigame("hub");

        player.setGameMode(GameMode.ADVENTURE);

        return Result.SUCCESS;
    }
}

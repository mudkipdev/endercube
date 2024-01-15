package net.endercube.spleef.activeGame;

import net.endercube.Common.EndercubeActiveGame;
import net.endercube.Common.events.MinigameStartEvent;
import net.endercube.Common.players.EndercubePlayer;
import net.endercube.spleef.activeGame.listeners.PlayerMove;
import net.endercube.spleef.activeGame.listeners.PlayerStartDigging;
import net.endercube.spleef.activeGame.listeners.RemoveEntityFromInstance;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import net.minestom.server.entity.GameMode;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.tag.Tag;
import net.minestom.server.timer.TaskSchedule;

import java.time.Duration;
import java.util.Set;

public class SpleefActiveGame extends EndercubeActiveGame {
    public SpleefActiveGame(Instance instance, Set<EndercubePlayer> players) {
        super(instance, players);

        // Init events
        this.getEventNode()
                .addListener(new PlayerMove())
                .addListener(new RemoveEntityFromInstance(instance));

        this.getActiveEventNode()
                .addListener(new PlayerStartDigging());

        // Add players to the instance + init hotbar
        players.forEach((player) -> {
            player.setInstance(instance, instance.getTag(Tag.Transient("spawnPos")));
            player.getInventory().setItemStack(0, ItemStack.builder(Material.GOLDEN_SHOVEL).build());
            player.setGameMode(GameMode.SURVIVAL);
        });

        // Init tags
        instance.setTag(Tag.Integer("nextStartingCountdown"), 10);
        instance.setTag(Tag.String("gameState"), "STARTING");

        // Countdown
        instance.scheduler().submitTask(() -> {
            for (EndercubePlayer player : players) {
                int secondsLeft = instance.getTag(Tag.Integer("nextStartingCountdown"));

                // If we've finished counting down
                if (secondsLeft == 0) {
                    // Show title
                    final Title.Times times = Title.Times.times(Duration.ofMillis(100), Duration.ofMillis(500), Duration.ofMillis(500));
                    final Title title = Title.title(Component.text("Go!").color(NamedTextColor.GOLD), Component.empty(), times);
                    player.showTitle(title);
                    player.playSound(Sound.sound(SoundEvent.BLOCK_NOTE_BLOCK_IMITATE_ENDER_DRAGON, Sound.Source.PLAYER, 1f, 1f));

                    // Call minigame start
                    getEventNode().call(new MinigameStartEvent(getInstance()));

                    // Stop schedule after that
                    return TaskSchedule.stop();
                }

                // Only send title on 10, 5, 3, 2, 1
                if (secondsLeft == 10 || secondsLeft <= 5) {
                    player.sendTitlePart(TitlePart.TITLE, Component.text(secondsLeft).color(NamedTextColor.GOLD));
                    player.playSound(Sound.sound(SoundEvent.BLOCK_NOTE_BLOCK_PLING, Sound.Source.PLAYER, 1f, 1f));
                }

                // Update tag
                instance.setTag(Tag.Integer("nextStartingCountdown"), secondsLeft - 1);
            }
            return TaskSchedule.seconds(1);
        });
    }

    @Override
    public void onPlayerLeave() {

    }
}

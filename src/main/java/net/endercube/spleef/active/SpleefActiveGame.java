package net.endercube.spleef.active;

import net.endercube.EndercubeActiveGame;
import net.endercube.event.MinigameStartEvent;
import net.endercube.spleef.active.listener.ActivePlayerMove;
import net.endercube.spleef.active.listener.InactivePlayerMove;
import net.endercube.spleef.active.listener.PlayerStartDigging;
import net.endercube.spleef.active.listener.RemoveEntityFromInstance;
import net.endercube.utility.EndercubePlayer;
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

                .addListener(new RemoveEntityFromInstance(instance));

        this.getActiveEventNode()
                .addListener(new ActivePlayerMove())
                .addListener(new PlayerStartDigging());

        this.getInactiveEventNode()
                .addListener(new InactivePlayerMove());

        // Add players to the instance + init hotbar
        players.forEach((player) -> {
            player.setInstance(instance, instance.getTag(Tag.Transient("spawnPos")));
            player.getInventory().setItemStack(0, ItemStack.builder(Material.GOLDEN_SHOVEL).build());
            player.setGameMode(GameMode.ADVENTURE);
            logger.debug("Setting " + player.getUsername() + "'s gamemode to adventure");
        });

        // Init tags
        instance.setTag(Tag.Integer("nextStartingCountdown"), 10);

        // Countdown
        instance.scheduler().submitTask(() -> {
            int secondsLeft = instance.getTag(Tag.Integer("nextStartingCountdown"));
            for (EndercubePlayer player : players) {
                switch (secondsLeft) {
                    case -1 -> {
                        return TaskSchedule.stop();
                    }
                    case 0 -> {
                        // Show title
                        final Title.Times times = Title.Times.times(Duration.ofMillis(100), Duration.ofMillis(500), Duration.ofMillis(500));
                        final Title title = Title.title(Component.text("Go!").color(NamedTextColor.GOLD), Component.empty(), times);
                        player.showTitle(title);
                        player.playSound(Sound.sound(SoundEvent.BLOCK_NOTE_BLOCK_IMITATE_ENDER_DRAGON, Sound.Source.PLAYER, 1f, 1f));

                        // The player can break blocks now
                        player.setGameMode(GameMode.SURVIVAL);

                        // Call minigame start
                        getEventNode().call(new MinigameStartEvent(getInstance()));
                    }
                    default -> {
                        player.sendTitlePart(TitlePart.TITLE, Component.text(secondsLeft).color(NamedTextColor.GOLD));
                        player.playSound(Sound.sound(SoundEvent.BLOCK_NOTE_BLOCK_PLING, Sound.Source.PLAYER, 1f, 1f));
                    }
                }
            }
            // Update tag
            instance.setTag(Tag.Integer("nextStartingCountdown"), secondsLeft - 1);
            return TaskSchedule.seconds(1);
        });
    }

    @Override
    public void onPlayerLeave() {

    }
}

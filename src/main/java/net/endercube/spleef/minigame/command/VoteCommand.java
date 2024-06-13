package net.endercube.spleef.minigame.command;

import net.endercube.spleef.minigame.inventory.MapVoteInventory;
import net.endercube.utility.EndercubePlayer;
import net.minestom.server.command.builder.Command;

public class VoteCommand extends Command {
    public VoteCommand() {
        super("vote");

        setDefaultExecutor(((sender, context) -> {
            EndercubePlayer player = (EndercubePlayer) sender;

            player.sendMessage("Opening inv");
            player.openInventory(MapVoteInventory.getInventory());
        }));
    }
}

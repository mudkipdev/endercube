package net.endercube;

import net.endercube.command.DiscordCommand;
import net.endercube.command.GenericRootCommand;
import net.endercube.command.GitHubCommand;
import net.endercube.command.PerformanceCommand;
import net.endercube.command.admin.BanCommand;
import net.endercube.command.admin.KickCommand;
import net.endercube.command.admin.ResetParkourTimeCommand;
import net.endercube.command.admin.UnbanCommand;
import net.endercube.discord.Discord;
import net.endercube.hub.HubMinigame;
import net.endercube.listener.AsyncPlayerConfiguration;
import net.endercube.listener.AsyncPlayerPreLogin;
import net.endercube.listener.PlayerDisconnect;
import net.endercube.listener.ServerTickMonitor;
import net.endercube.parkour.ParkourMinigame;
import net.endercube.spleef.minigame.SpleefMinigame;
import net.endercube.utility.block.Sign;
import net.endercube.utility.block.Skull;
import net.minestom.server.MinecraftServer;
import net.minestom.server.permission.Permission;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPooled;

/**
 * This is the entrypoint for the server
 */
public class Main {
    public static final Logger logger = LoggerFactory.getLogger(Main.class);
    public static @Nullable EndercubeServer endercubeServer;
    public static JedisPooled jedis;

    public static void main(String[] args) {
        logger.info("Starting Server");

        endercubeServer = new EndercubeServer.EndercubeServerBuilder()
                .addGlobalEvent(new AsyncPlayerConfiguration())
                .addGlobalEvent(new PlayerDisconnect())
                .addGlobalEvent(new ServerTickMonitor())
                .addGlobalEvent(new AsyncPlayerPreLogin())
                .addBlockHandler(NamespaceID.from("minecraft:sign"), Sign::new)
                .addBlockHandler(NamespaceID.from("minecraft:skull"), Skull::new)
                .startServer();

        endercubeServer
                .addMinigame(new ParkourMinigame(endercubeServer))
                .addMinigame(new SpleefMinigame(endercubeServer))
                .addMinigame(new HubMinigame(endercubeServer));

        jedis = endercubeServer.getJedisPooled();

        initCommands();

        Discord.init();
    }

    private static void initCommands() {
        // Add admin commands
        GenericRootCommand adminCommand = new GenericRootCommand("admin");
        adminCommand.setCondition(((sender, commandString) -> sender.hasPermission(new Permission("operator"))));
        adminCommand.addSubcommand(new ResetParkourTimeCommand());
        adminCommand.addSubcommand(new BanCommand());
        adminCommand.addSubcommand(new UnbanCommand());
        adminCommand.addSubcommand(new KickCommand());

        // Add public user commands
        MinecraftServer.getCommandManager().register(adminCommand);
        MinecraftServer.getCommandManager().register(new DiscordCommand());
        MinecraftServer.getCommandManager().register(new PerformanceCommand());
        MinecraftServer.getCommandManager().register(new GitHubCommand());
    }
}

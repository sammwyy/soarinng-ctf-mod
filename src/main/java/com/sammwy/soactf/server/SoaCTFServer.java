package com.sammwy.soactf.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sammwy.soactf.common.utils.FabricUtils;
import com.sammwy.soactf.server.commands.CTFCommand;
import com.sammwy.soactf.server.commands.types.GamePhaseType;
import com.sammwy.soactf.server.config.ConfigManager;
import com.sammwy.soactf.server.config.impl.CTFArenaConfig;
import com.sammwy.soactf.server.config.impl.CTFConfiguration;
import com.sammwy.soactf.server.config.impl.CTFMessagesConfig;
import com.sammwy.soactf.server.events.EventManager;
import com.sammwy.soactf.server.game.Game;
import com.sammwy.soactf.server.players.PlayerManager;
import com.sammwy.soactf.server.tasks.TaskScheduler;
import com.sammwy.soactf.server.teams.CTFTeamManager;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.util.Identifier;

public class SoaCTFServer implements DedicatedServerModInitializer {
    // Constants.
    public static final String MOD_ID = "soactf";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // Managers.
    private ConfigManager configManager;
    private EventManager eventManager;
    private PlayerManager playerManager;
    private CTFTeamManager teamManager;

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public EventManager getEventManager() {
        return this.eventManager;
    }

    public PlayerManager getPlayerManager() {
        return this.playerManager;
    }

    public CTFTeamManager getTeamManager() {
        return this.teamManager;
    }

    // Handlers.
    private TaskScheduler taskScheduler;

    public TaskScheduler getTaskScheduler() {
        return this.taskScheduler;
    }

    // State.
    private Game game;

    public Game getGame() {
        return this.game;
    }

    // Config getters.
    public CTFArenaConfig getArenaConfig() {
        return this.configManager.getConfig("arena.json", CTFArenaConfig.class);
    }

    public CTFConfiguration getConfig() {
        return this.configManager.getConfig("config.json", CTFConfiguration.class);
    }

    public CTFMessagesConfig getMessages() {
        return this.configManager.getConfig("messages.json", CTFMessagesConfig.class);
    }

    // Static instance.
    private static SoaCTFServer INSTANCE;

    public static SoaCTFServer getInstance() {
        return INSTANCE;
    }

    // Entry point.
    @Override
    public void onInitializeServer() {
        // Set static instance.
        INSTANCE = this;

        // Instantiate managers.
        this.configManager = new ConfigManager(FabricUtils.getConfigDir(MOD_ID));
        this.eventManager = new EventManager();
        this.playerManager = new PlayerManager(this, FabricUtils.getConfigDir(MOD_ID, "players"));
        this.teamManager = new CTFTeamManager(this, FabricUtils.getConfigDir(MOD_ID, "teams"));

        // Load data.
        this.teamManager.loadTeams();

        // Initialize state.
        this.game = new Game(this);

        // Initialize libraries.
        this.taskScheduler = new TaskScheduler();

        // Register tasks.
        this.taskScheduler.runTaskRepeat(() -> {
            this.game.tick();
        }, 20L);

        // Register argument types.
        ArgumentTypeRegistry.registerArgumentType(new Identifier(MOD_ID, "game_phase"), GamePhaseType.class,
                ConstantArgumentSerializer.of(GamePhaseType::gamePhase));

        // Register events.
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            this.taskScheduler.tick();
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registry, env) -> {
            new CTFCommand(this).register(dispatcher);
        });

        // Register handlers.
        new SoaCTFHandler(this);
    }
}

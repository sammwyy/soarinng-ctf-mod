package com.sammwy.soactf.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sammwy.soactf.common.Constants;
import com.sammwy.soactf.common.Initializer;
import com.sammwy.soactf.common.utils.FabricUtils;
import com.sammwy.soactf.common.utils.WorldUtils;
import com.sammwy.soactf.server.commands.CTFCommand;
import com.sammwy.soactf.server.commands.CTFLootCommand;
import com.sammwy.soactf.server.commands.CTFTeamCommand;
import com.sammwy.soactf.server.config.ConfigManager;
import com.sammwy.soactf.server.config.impl.CTFArenaConfig;
import com.sammwy.soactf.server.config.impl.CTFConfiguration;
import com.sammwy.soactf.server.config.impl.CTFMessagesConfig;
import com.sammwy.soactf.server.events.EventManager;
import com.sammwy.soactf.server.events.player.PlayerAttackEntityEvent;
import com.sammwy.soactf.server.events.player.PlayerBeforeDeathEvent;
import com.sammwy.soactf.server.events.player.PlayerRespawnEvent;
import com.sammwy.soactf.server.game.Game;
import com.sammwy.soactf.server.loots.LootBoxManager;
import com.sammwy.soactf.server.players.PlayerManager;
import com.sammwy.soactf.server.tasks.TaskScheduler;
import com.sammwy.soactf.server.teams.CTFTeamManager;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

public class SoaCTFServer implements DedicatedServerModInitializer {
    // Constants.
    public static final Logger LOGGER = LoggerFactory.getLogger(Constants.MOD_ID);

    // Managers.
    private ConfigManager configManager;
    private EventManager eventManager;
    private LootBoxManager lootBoxManager;
    private PlayerManager playerManager;
    private CTFTeamManager teamManager;

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public EventManager getEventManager() {
        return this.eventManager;
    }

    public LootBoxManager getLootBoxManager() {
        return this.lootBoxManager;
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
        Initializer.initAll();

        // Instantiate managers.
        this.configManager = new ConfigManager(FabricUtils.getConfigDir(Constants.MOD_ID));
        this.eventManager = new EventManager();
        this.lootBoxManager = new LootBoxManager(this);
        this.playerManager = new PlayerManager(this, FabricUtils.getConfigDir(Constants.MOD_ID, "players"));
        this.teamManager = new CTFTeamManager(this, FabricUtils.getConfigDir(Constants.MOD_ID, "teams"));

        // Load data.
        this.lootBoxManager.load();
        this.teamManager.loadTeams();

        // Initialize state.
        this.game = new Game(this);

        // Initialize libraries.
        this.taskScheduler = new TaskScheduler();

        // Register tasks.
        this.taskScheduler.runTaskRepeat(() -> {
            this.game.tick();
        }, 20L);

        // Register commands.
        CommandRegistrationCallback.EVENT.register((dispatcher, registry, env) -> {
            new CTFCommand(this).register(dispatcher);
            new CTFLootCommand(this).register(dispatcher);
            new CTFTeamCommand(this).register(dispatcher);
        });

        // Register events.
        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, source, damage) -> {
            if (entity instanceof ServerPlayerEntity playerEntity) {
                PlayerBeforeDeathEvent event = new PlayerBeforeDeathEvent(playerEntity, source, damage);
                this.eventManager.call(event);

                if (event.isCancelled()) {
                    entity.setHealth(1);
                    return false;
                }
            }

            return true;
        });

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            PlayerAttackEntityEvent event = new PlayerAttackEntityEvent(player, world, hand, entity, hitResult);
            this.eventManager.call(event);
            return event.isCancelled() ? ActionResult.FAIL : ActionResult.PASS;
        });

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            PlayerRespawnEvent event = new PlayerRespawnEvent(oldPlayer, newPlayer, alive);
            this.eventManager.call(event);
        });

        ServerTickEvents.START_SERVER_TICK.register(server -> {
            this.taskScheduler.tick();
        });

        ServerWorldEvents.LOAD.register((server, world) -> {
            if (world.getRegistryKey() != World.OVERWORLD) {
                return;
            }

            WorldUtils.setDefaultWorld(world);
            this.lootBoxManager.resetAllCooldown();
        });

        // Register handlers.
        new SoaCTFHandler(this);
    }
}

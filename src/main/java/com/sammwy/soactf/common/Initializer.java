package com.sammwy.soactf.common;

import com.sammwy.soactf.server.commands.types.ColorType;
import com.sammwy.soactf.server.commands.types.GamePhaseType;
import com.sammwy.soactf.server.commands.types.PlayerType;
import com.sammwy.soactf.server.commands.types.TeamType;

import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.util.Identifier;

public class Initializer {
    public static void initArgumentTypes() {
        ArgumentTypeRegistry.registerArgumentType(new Identifier(Constants.MOD_ID, "color"), ColorType.class,
                ConstantArgumentSerializer.of(ColorType::color));

        ArgumentTypeRegistry.registerArgumentType(new Identifier(Constants.MOD_ID, "game_phase"), GamePhaseType.class,
                ConstantArgumentSerializer.of(GamePhaseType::gamePhase));

        ArgumentTypeRegistry.registerArgumentType(new Identifier(Constants.MOD_ID, "player"), PlayerType.class,
                ConstantArgumentSerializer.of(PlayerType::player));

        ArgumentTypeRegistry.registerArgumentType(new Identifier(Constants.MOD_ID, "team"), TeamType.class,
                ConstantArgumentSerializer.of(TeamType::team));

    }

    public static void initAll() {
        // Initialize argument types.
        initArgumentTypes();
    }
}

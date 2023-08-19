package com.sammwy.soactf.server.commands.types;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.sammwy.soactf.common.utils.ArrayUtils;
import com.sammwy.soactf.server.game.GamePhase;

import net.minecraft.text.Text;

public class GamePhaseType implements ArgumentType<GamePhase> {
    public static GamePhaseType gamePhase() {
        return new GamePhaseType();
    }

    @Override
    public GamePhase parse(StringReader reader) throws CommandSyntaxException {
        int argBeginning = reader.getCursor();
        if (!reader.canRead()) {
            reader.skip();
        }

        while (reader.canRead() && reader.peek() != ' ') {
            reader.skip();
        }

        String phase = reader.getString().substring(argBeginning, reader.getCursor());

        try {
            return GamePhase.valueOf(phase.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new SimpleCommandExceptionType(Text.literal("Invalid game phase.")).createWithContext(reader);
        }
    }

    public static <S> GamePhase getGamePhase(String name, CommandContext<S> context) {
        return context.getArgument(name, GamePhase.class);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        List<String> suggestions = ArrayUtils.map(GamePhase.values(), GamePhase::name);
        ArrayUtils.iter(suggestions, builder::suggest);
        return builder.buildFuture();
    }
}
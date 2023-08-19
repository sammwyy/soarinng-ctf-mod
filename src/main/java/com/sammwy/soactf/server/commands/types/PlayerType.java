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
import com.sammwy.soactf.common.Constants;
import com.sammwy.soactf.common.utils.ArrayUtils;
import com.sammwy.soactf.server.SoaCTFServer;
import com.sammwy.soactf.server.players.Player;
import com.sammwy.soactf.server.players.PlayerManager;

import net.minecraft.text.Text;

public class PlayerType implements ArgumentType<Player> {
    public static PlayerType player() {
        return new PlayerType();
    }

    @Override
    public Player parse(StringReader reader) throws CommandSyntaxException {
        int argBeginning = reader.getCursor();
        if (!reader.canRead()) {
            reader.skip();
        }

        while (reader.canRead() && reader.peek() != ' ') {
            reader.skip();
        }

        String playerName = reader.getString().substring(argBeginning, reader.getCursor());
        PlayerManager players = SoaCTFServer.getInstance().getPlayerManager();
        Player player = players.getPlayerByName(playerName);

        if (player == null) {
            throw new SimpleCommandExceptionType(Text.literal("Player with this name doesn't found or is offline."))
                    .createWithContext(reader);
        }

        return player;
    }

    public static <S> Player getPlayer(String name, CommandContext<S> context) {
        return context.getArgument(name, Player.class);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if (Constants.IS_CLIENT) {
            return Suggestions.empty();
        }

        PlayerManager players = SoaCTFServer.getInstance().getPlayerManager();
        List<String> suggestions = ArrayUtils.map(players.getPlayers(), Player::getName);
        ArrayUtils.iter(suggestions, builder::suggest);
        return builder.buildFuture();
    }
}

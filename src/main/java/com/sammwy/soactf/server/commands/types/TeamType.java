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
import com.sammwy.soactf.server.teams.CTFTeam;
import com.sammwy.soactf.server.teams.CTFTeamManager;

import net.minecraft.text.Text;

public class TeamType implements ArgumentType<CTFTeam> {
    public static TeamType team() {
        return new TeamType();
    }

    @Override
    public CTFTeam parse(StringReader reader) throws CommandSyntaxException {
        int argBeginning = reader.getCursor();
        if (!reader.canRead()) {
            reader.skip();
        }

        while (reader.canRead() && reader.peek() != ' ') {
            reader.skip();
        }

        String teamID = reader.getString().substring(argBeginning, reader.getCursor());
        CTFTeamManager teams = SoaCTFServer.getInstance().getTeamManager();
        CTFTeam team = teams.getTeam(teamID);

        if (team == null) {
            throw new SimpleCommandExceptionType(Text.literal("Team with this ID doesn't exist."))
                    .createWithContext(reader);
        }

        return team;
    }

    public static <S> CTFTeam getTeam(String name, CommandContext<S> context) {
        return context.getArgument(name, CTFTeam.class);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if (Constants.IS_CLIENT) {
            return Suggestions.empty();
        }

        CTFTeamManager teams = SoaCTFServer.getInstance().getTeamManager();
        List<String> suggestions = ArrayUtils.map(teams.getTeams(), CTFTeam::getID);
        ArrayUtils.iter(suggestions, builder::suggest);
        return builder.buildFuture();
    }
}

package com.sammwy.soactf.server.teams;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.sammwy.soactf.server.SoaCTFServer;
import com.sammwy.soactf.server.chat.Color;

import net.minecraft.scoreboard.Scoreboard;

public class CTFTeamManager {
    private File dataDir;
    private SoaCTFServer server;

    private Map<String, CTFTeam> teams;
    private Scoreboard scoreboard;

    public CTFTeamManager(SoaCTFServer server, File dataDir) {
        this.dataDir = dataDir;
        this.server = server;

        this.teams = new HashMap<>();
        this.scoreboard = new Scoreboard();
    }

    public void addTeam(CTFTeam team) {
        this.teams.put(team.getID(), team);
        this.scoreboard.addTeam(team.getID());
    }

    public CTFTeam createTeam(String name, Color color) {
        CTFTeam team = CTFTeam.create(this.server.getConfig(), name, this.dataDir, this.scoreboard, false);
        team.setColor(color);
        this.addTeam(team);
        return team;
    }

    public Collection<CTFTeam> getTeams() {
        return this.teams.values();
    }

    public CTFTeam getTeam(String id) {
        return this.teams.get(id);
    }

    public CTFTeam getTeamWithLeastPlayers() {
        CTFTeam team = null;

        for (CTFTeam t : this.teams.values()) {
            if (team == null || t.getPlayers().size() < team.getPlayers().size()) {
                team = t;
            }
        }

        return team;
    }

    public void loadTeams() {
        File[] files = this.dataDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".json");
            }
        });

        for (File file : files) {
            CTFTeam team = new CTFTeam(this.server.getConfig(), file, scoreboard);
            this.addTeam(team);
        }
    }
}

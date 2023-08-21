package com.sammwy.soactf.server.config.impl;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sammwy.soactf.server.config.Configuration;

public class CTFMessagesConfig extends Configuration {
    public static class GameMessages {
        @Expose
        @SerializedName("cannot_capture_own_flag")
        public String cannotCaptureOwnFlag = "&cNo puedes capturar tu propia bandera.";

        @Expose
        @SerializedName("flag_captured")
        public String flagCaptured = "{captured_by} &7ha capturado la bandera del equipo {captured_team}&7.";

        @Expose
        @SerializedName("flag_captured_dropped")
        public String flagCapturedDropped = "{captured_by} &7recogio del suelo la bandera del equipo {captured_team}&7.";

        @Expose
        @SerializedName("flag_returned")
        public String flagReturned = "{captured_by} devolvio la bandera del equipo {captured_team}&7 a su base.";

        @Expose
        @SerializedName("your_flag_captured_title")
        public String yourFlagCapturedTitle = "&cTu bandera ha sido capturada!";

        @Expose
        @SerializedName("your_flag_captured_subtitle")
        public String yourFlagCapturedSubtitle = "&7Tu bandera ha sido capturada por {team_captured_by}&7.";

        @Expose
        @SerializedName("your_flag_returned_title")
        public String yourFlagReturnedTitle = "&aTu bandera ha sido devuelta!";

        @Expose
        @SerializedName("your_flag_returned_subtitle")
        public String yourFlagReturnedSubtitle = "&7Tu bandera ha sido devuelta por {team_returned_by}&7.";

        @Expose
        @SerializedName("goal")
        public String goal = "{player_display} &7anoto un punto capturando la bandera {captured_team}&7.";

        @Expose
        @SerializedName("goal_title")
        public String goalTitle = "&aGol!";

        @Expose
        @SerializedName("goal_subtitle")
        public String goalSubtitle = "&7Tu equipo ahora tiene &a{team_points} &7puntos";

        @Expose
        @SerializedName("respawn_title")
        public String respawnTitle = "&cHas muerto";

        @Expose
        @SerializedName("respawn_subtitle")
        public String respawnSubtitle = "&7Reapareceras en &a{player_respawn_time} &7segundos";

        @Expose
        public String starting = "&7El juego comenzara en &a{game_time} &7segundos.";

        @Expose
        public String started = "&dEl juego ha comenzado!";

        @Expose
        public String finishing = "&7El juego terminara en &a{game_time} &7segundos.";

        @Expose
        public String finished = "&dEl juego ha terminado!";

        @Expose
        @SerializedName("team_killed")
        public String teamKilled = "&7El equipo {team_killed} &7ha sido eliminado!";

        @Expose
        public String actionbar = "&e{game_time}";
    }

    @Expose
    public GameMessages game = new GameMessages();
}

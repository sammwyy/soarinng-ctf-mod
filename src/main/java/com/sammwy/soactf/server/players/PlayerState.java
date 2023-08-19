package com.sammwy.soactf.server.players;

public enum PlayerState {
    ALIVE, // Player is alive. (Playing)
    DEAD, // Player is dead. (Definitive game over)
    SPECTATOR, // Player is spectator. (Staff)
    RESPAWNING // Player is dead. (Respawning)
}

package com.sammwy.soactf.server.config.impl;

import com.google.gson.annotations.Expose;
import com.sammwy.soactf.server.config.Configuration;
import com.sammwy.soactf.server.world.Position;

public class CTFArenaConfig extends Configuration {
    @Expose
    public Position lobby;

    @Expose
    public Position spectator;
}

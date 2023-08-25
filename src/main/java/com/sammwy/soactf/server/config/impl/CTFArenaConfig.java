package com.sammwy.soactf.server.config.impl;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sammwy.soactf.server.config.Configuration;
import com.sammwy.soactf.server.world.BlockPosition;
import com.sammwy.soactf.server.world.Position;

public class CTFArenaConfig extends Configuration {
    @Expose
    public Position lobby;

    @Expose
    public Position spectator;

    @Expose
    @SerializedName("loot_boxes")
    public List<BlockPosition> lootBoxes = new ArrayList<>();
}

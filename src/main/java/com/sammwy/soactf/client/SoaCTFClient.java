package com.sammwy.soactf.client;

import com.sammwy.soactf.common.Initializer;

import net.fabricmc.api.ClientModInitializer;

public class SoaCTFClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Initializer.initAll();
    }
}

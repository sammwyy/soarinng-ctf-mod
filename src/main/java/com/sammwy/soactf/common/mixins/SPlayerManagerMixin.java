package com.sammwy.soactf.common.mixins;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.sammwy.soactf.server.events.EventManager;
import com.sammwy.soactf.server.events.player.PlayerConnectionEvent;
import com.sammwy.soactf.server.events.player.PlayerJoinEvent;

@Mixin(PlayerManager.class)
public class SPlayerManagerMixin {
    @Inject(at = @At("HEAD"), method = "onPlayerConnect")
    private void onPlayerConnect(ClientConnection connection, ServerPlayerEntity playerEntity, CallbackInfo info) {
        PlayerConnectionEvent event = new PlayerConnectionEvent(connection, playerEntity);
        EventManager.staticCall(event);
    }

    @Inject(at = @At(value = "INVOKE", target = "net.minecraft.server.network.ServerPlayerEntity.onSpawn()V"), method = "onPlayerConnect")
    private void onPlayerJoin(ClientConnection connection, ServerPlayerEntity playerEntity, CallbackInfo info) {
        PlayerJoinEvent event = new PlayerJoinEvent(connection, playerEntity);
        EventManager.staticCall(event);
    }
}
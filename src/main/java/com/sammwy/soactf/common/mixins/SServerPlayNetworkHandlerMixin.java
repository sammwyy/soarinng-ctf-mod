package com.sammwy.soactf.common.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.sammwy.soactf.server.events.EventManager;
import com.sammwy.soactf.server.events.player.PlayerDisconnectEvent;

import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

@Mixin(ServerPlayNetworkHandler.class)
public class SServerPlayNetworkHandlerMixin {
    @Shadow
    public ServerPlayerEntity player;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;onDisconnect()V"), method = "onDisconnected")
    private void onDisconnected(Text reason, CallbackInfo info) {
        PlayerDisconnectEvent event = new PlayerDisconnectEvent(player, reason);
        EventManager.staticCall(event);
    }
}

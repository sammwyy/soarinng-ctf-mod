package com.sammwy.soactf.common.mixins;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public class CClientPlayerEntityMixin extends AbstractClientPlayerEntity {
    public CClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(at = @At("HEAD"), method = "dropSelectedItem", cancellable = true)
    public void dropSelectedItem(boolean dropEntireStack, CallbackInfoReturnable<Boolean> info) {
        int slot = this.getInventory().selectedSlot;
        if (slot != 1 && slot != 2) {
            info.setReturnValue(false);
        }
    }
}

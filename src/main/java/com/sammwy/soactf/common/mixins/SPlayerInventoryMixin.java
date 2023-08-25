package com.sammwy.soactf.common.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

@Mixin(PlayerInventory.class)
public class SPlayerInventoryMixin {
    @Inject(method = "getEmptySlot", at = @At("HEAD"), cancellable = true)
    public void getEmptySlot(CallbackInfoReturnable<Integer> ci) {
        PlayerInventory _this = (PlayerInventory) (Object) this;

        if (!_this.player.isCreative()) {
            if (_this.main.get(1).isEmpty()) {
                ci.setReturnValue(1);
            } else if (_this.main.get(2).isEmpty()) {
                ci.setReturnValue(2);
            } else {
                ci.setReturnValue(-1);
            }
        }
    }

    @Inject(method = "getSwappableHotbarSlot", at = @At("HEAD"), cancellable = true)
    public void getSwappableHotbarSlot(CallbackInfoReturnable<Integer> ci) {
        PlayerInventory _this = (PlayerInventory) (Object) this;

        if (!_this.player.isCreative()) {
            int i;
            int j;
            for (i = 1; i < 3; ++i) {
                j = (_this.selectedSlot + i) % 9;
                if (((ItemStack) _this.main.get(j)).isEmpty()) {
                    ci.setReturnValue(j);
                    return;
                }
            }

            for (i = 1; i < 3; ++i) {
                j = (_this.selectedSlot + i) % 9;
                if (!((ItemStack) _this.main.get(j)).hasEnchantments()) {
                    ci.setReturnValue(j);
                    return;
                }
            }

            ci.setReturnValue(_this.selectedSlot);
        }
    }
}

package com.sammwy.soactf.common.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

@Mixin(HandledScreen.class)
public abstract class CHandledScreenMixin<T extends ScreenHandler> extends Screen {
    protected CHandledScreenMixin(Text title) {
        super(title);
    }

    public boolean mustCancel() {
        MinecraftClient client = MinecraftClient.getInstance();
        boolean isCreative = client.player.isCreative();

        HandledScreen<?> screen = (HandledScreen<?>) (Object) this;
        boolean isInventory = screen instanceof InventoryScreen;

        return !isCreative && isInventory;
    }

    @Inject(at = @At("HEAD"), method = "onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V", cancellable = true)
    public void onMouseClick(Slot slot, int invSlot, int clickData, SlotActionType actionType, CallbackInfo info) {
        if (slot != null && this.mustCancel()) {
            info.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "keyPressed", cancellable = true)
    public void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> info) {
        if (this.mustCancel()) {
            if (this.client.options.dropKey.matchesKey(keyCode, scanCode)) {
                info.cancel();
                return;
            }

            for (KeyBinding bind : this.client.options.hotbarKeys) {
                if (bind.matchesKey(keyCode, scanCode)) {
                    info.cancel();
                    return;
                }
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "handleHotbarKeyPressed", cancellable = true)
    public void handleHotbarKeyPressed(int keyCode, int scanCode, CallbackInfoReturnable<Boolean> info) {
        if (this.mustCancel()) {
            info.cancel();
        }
    }
}

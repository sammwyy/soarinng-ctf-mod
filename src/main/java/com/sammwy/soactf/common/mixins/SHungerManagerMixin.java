package com.sammwy.soactf.common.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.sammwy.soactf.server.events.EventManager;
import com.sammwy.soactf.server.events.player.PlayerFoodLevelChangeEvent;

import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;

@Mixin(HungerManager.class)
public class SHungerManagerMixin {
    @Shadow
    private int foodLevel;

    @Shadow
    private float saturationLevel;

    @Shadow
    private float exhaustion;

    @Shadow
    private int foodTickTimer;

    @Shadow
    private int prevFoodLevel;

    @Shadow
    public void addExhaustion(float exhaustion) {

    }

    public boolean updateFoodLevel(PlayerEntity entity, HungerManager manager, int newFoodLevel) {
        if (this.prevFoodLevel == newFoodLevel) {
            return false;
        }

        PlayerFoodLevelChangeEvent event = new PlayerFoodLevelChangeEvent(entity, manager, newFoodLevel);
        EventManager.staticCall(event);
        boolean canUpdate = !event.isCancelled();

        if (canUpdate) {
            this.foodLevel = event.getNewFoodLevel();
        }

        return canUpdate;
    }

    @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    public void update(PlayerEntity player, CallbackInfo ci) {
        Difficulty difficulty = player.world.getDifficulty();
        HungerManager _this = (HungerManager) (Object) this;

        this.prevFoodLevel = this.foodLevel;

        if (this.exhaustion > 4.0F) {
            this.exhaustion -= 4.0F;
            if (this.saturationLevel > 0.0F) {
                this.saturationLevel = Math.max(this.saturationLevel - 1.0F, 0.0F);
            } else if (difficulty != Difficulty.PEACEFUL) {
                this.updateFoodLevel(player, _this, Math.max(this.foodLevel - 1, 0));
            }
        }

        boolean bl = player.world.getGameRules().getBoolean(GameRules.NATURAL_REGENERATION);
        if (bl && this.saturationLevel > 0.0F && player.canFoodHeal() && this.foodLevel >= 20) {
            ++this.foodTickTimer;
            if (this.foodTickTimer >= 10) {
                float f = Math.min(this.saturationLevel, 6.0F);
                player.heal(f / 6.0F);
                this.addExhaustion(f);
                this.foodTickTimer = 0;
            }
        } else if (bl && this.foodLevel >= 18 && player.canFoodHeal()) {
            ++this.foodTickTimer;
            if (this.foodTickTimer >= 80) {
                player.heal(1.0F);
                this.addExhaustion(6.0F);
                this.foodTickTimer = 0;
            }
        } else if (this.foodLevel <= 0) {
            ++this.foodTickTimer;
            if (this.foodTickTimer >= 80) {
                if (player.getHealth() > 10.0F || difficulty == Difficulty.HARD
                        || player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {
                    player.damage(player.getDamageSources().starve(), 1.0F);
                }

                this.foodTickTimer = 0;
            }
        } else {
            this.foodTickTimer = 0;
        }

        ci.cancel();
    }
}

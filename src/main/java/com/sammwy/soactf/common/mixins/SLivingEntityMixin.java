package com.sammwy.soactf.common.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.sammwy.soactf.server.events.EventManager;
import com.sammwy.soactf.server.events.entity.EntityTakeDamageEvent;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.tag.DamageTypeTags;

@Mixin(net.minecraft.entity.LivingEntity.class)
public class SLivingEntityMixin {

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    public void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (entity.isInvulnerableTo(source) || entity.world.isClient || entity.isDead()
                || source.isIn(DamageTypeTags.IS_FIRE) && entity.hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
            return;
        }

        EntityTakeDamageEvent event = new EntityTakeDamageEvent(entity, source, amount);
        EventManager.staticCall(event);

        if (event.isCancelled()) {
            ci.setReturnValue(true);
        }
    }
}

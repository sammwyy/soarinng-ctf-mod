package com.sammwy.soactf.common.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.sammwy.soactf.server.events.EventManager;
import com.sammwy.soactf.server.events.block.BlockBreakEvent;
import com.sammwy.soactf.server.events.block.BlockInteractEvent;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(ServerPlayerInteractionManager.class)
public class SServerPlayerInteractionManagerMixin {
    @Shadow
    public ServerPlayerEntity player;
    @Shadow
    public ServerWorld world;

    @Inject(locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true, method = "tryBreakBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;onBreak(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/player/PlayerEntity;)V"))
    private void tryBreakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> ci, BlockState state,
            BlockEntity entity, Block block) {
        BlockBreakEvent event = new BlockBreakEvent(player, world, pos, state, entity, block);
        EventManager.staticCall(event);
        if (event.isCancelled()) {
            ci.setReturnValue(false);
        }
    }

    @Inject(cancellable = true, method = "interactBlock", at = @At(value = "HEAD"))
    private void interactBlock(ServerPlayerEntity player, World world, ItemStack stack, Hand hand,
            BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> ci) {
        if (hitResult.getType() == Type.BLOCK) {
            BlockInteractEvent event = new BlockInteractEvent(player, world, stack, hand, hitResult);
            EventManager.staticCall(event);

            if (event.isCancelled()) {
                ci.setReturnValue(ActionResult.FAIL);
                return;
            }

        }
    }

}
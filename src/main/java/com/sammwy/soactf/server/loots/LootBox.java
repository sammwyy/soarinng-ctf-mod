package com.sammwy.soactf.server.loots;

import java.util.ArrayList;
import java.util.List;

import com.sammwy.soactf.common.utils.ArrayUtils;
import com.sammwy.soactf.common.utils.ItemUtils;
import com.sammwy.soactf.common.utils.TextUtils;
import com.sammwy.soactf.common.utils.WorldUtils;
import com.sammwy.soactf.server.world.BlockPosition;

import net.minecraft.block.Blocks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class LootBox {
    private BlockPosition position;
    private List<ItemStack> items;
    private int defaultResetTime;

    private int resetTime;

    public LootBox(BlockPosition position, List<String> items, int defaultResetTime) {
        this.position = position;
        this.items = new ArrayList<>();
        this.defaultResetTime = defaultResetTime;

        this.resetTime = -1;

        ArrayUtils.map(items, ItemUtils::getItemStack)
                .stream().forEach(this::addItem);
    }

    public void addItem(ItemStack item) {
        this.items.add(item);
    }

    public BlockPosition getPosition() {
        return this.position;
    }

    public boolean hasLoot() {
        return this.resetTime == -1;
    }

    public boolean is(BlockPosition position) {
        return this.position.x == position.x && position.y == this.position.y && position.z == this.position.z;
    }

    public boolean is(BlockPos pos) {
        return this.is(BlockPosition.fromBlockPos(pos));
    }

    public void spawnItem(ItemStack item) {
        ItemStack stack = item.copy();
        ServerWorld world = WorldUtils.getDefaultWorld();

        double x = this.position.x + 0.5D;
        double y = this.position.y + 0.5D;
        double z = this.position.z + 0.5D;

        ItemEntity entity = new ItemEntity(world, x, y, z, stack, 0, 2, 0);
        world.spawnEntity(entity);
    }

    public void spawnRandomItem() {
        this.spawnItem(ArrayUtils.random(this.items));
    }

    public void setInCooldown(boolean cooldown) {
        ServerWorld world = WorldUtils.getDefaultWorld();

        if (cooldown) {
            this.resetTime = this.defaultResetTime;
            world.setBlockState(this.position.toBlockPos(), Blocks.AIR.getDefaultState());
        } else {
            this.resetTime = -1;
            world.setBlockState(this.position.toBlockPos(), Blocks.SPONGE.getDefaultState());
        }
    }

    public boolean claim() {
        if (this.hasLoot()) {
            this.spawnRandomItem();
            this.setInCooldown(true);
            return true;
        }

        return false;
    }

    public Text toText(String prefix) {
        String position = "&e" + this.position.toString();

        String color = this.hasLoot() ? "&a" : "&c";
        String time = color + this.resetTime;
        String meta = "&7{&ftime=" + time + "&7| &fitems=&b" + this.items.size() + "&7}";

        return TextUtils.from(prefix, position, meta);
    }

    public void tick() {
        if (this.resetTime == 0) {
            this.setInCooldown(false);
        }

        if (this.resetTime > 0) {
            this.resetTime--;
        }
    }
}

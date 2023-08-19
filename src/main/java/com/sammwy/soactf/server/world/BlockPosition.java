package com.sammwy.soactf.server.world;

import com.google.gson.annotations.Expose;

import net.minecraft.util.math.BlockPos;

public class BlockPosition {
    @Expose
    public int x;
    @Expose
    public int y;
    @Expose
    public int z;

    public BlockPosition(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BlockPosition(double x, double y, double z) {
        this.x = (int) Math.floor(x);
        this.y = (int) Math.floor(y);
        this.z = (int) Math.floor(z);
    }

    public BlockPosition(BlockPosition position) {
        this.x = position.x;
        this.y = position.y;
        this.z = position.z;
    }

    public BlockPosition() {
        this(0, 0, 0);
    }

    public BlockPosition add(int x, int y, int z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public BlockPosition subtract(int x, int y, int z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    public BlockPosition multiply(int x, int y, int z) {
        this.x *= x;
        this.y *= y;
        this.z *= z;
        return this;
    }

    public BlockPosition divide(int x, int y, int z) {
        this.x /= x;
        this.y /= y;
        this.z /= z;
        return this;
    }

    public double distance(BlockPosition position) {
        return Math.sqrt(Math.pow(this.x - position.x, 2) + Math.pow(this.y - position.y, 2)
                + Math.pow(this.z - position.z, 2));
    }

    public double distance(Position position) {
        return Math.sqrt(Math.pow(this.x - position.x, 2) + Math.pow(this.y - position.y, 2)
                + Math.pow(this.z - position.z, 2));
    }

    public double distance(BlockPos pos) {
        return Math.sqrt(Math.pow(this.x - pos.getX(), 2) + Math.pow(this.y - pos.getY(), 2)
                + Math.pow(this.z - pos.getZ(), 2));
    }

    public double distanceSquared(BlockPosition position) {
        return Math.pow(this.x - position.x, 2) + Math.pow(this.y - position.y, 2) + Math.pow(this.z - position.z, 2);
    }

    public double distanceSquared(Position position) {
        return Math.pow(this.x - position.x, 2) + Math.pow(this.y - position.y, 2) + Math.pow(this.z - position.z, 2);
    }

    public BlockPosition clone() {
        return new BlockPosition(this);
    }

    public void set(BlockPosition position) {
        this.x = position.x;
        this.y = position.y;
        this.z = position.z;
    }

    public BlockPos toBlockPos() {
        return new BlockPos(this.x, this.y, this.z);
    }

    public Position toPosition() {
        return new Position(this.x, this.y, this.z);
    }

    public boolean equals(BlockPos blockPos) {
        return this.x == blockPos.getX() && this.y == blockPos.getY() && this.z == blockPos.getZ();
    }

    public boolean equals(BlockPosition position) {
        return this.x == position.x && this.y == position.y && this.z == position.z;
    }

    public static BlockPosition fromBlockPos(BlockPos pos) {
        return new BlockPosition(pos.getX(), pos.getY(), pos.getZ());
    }
}

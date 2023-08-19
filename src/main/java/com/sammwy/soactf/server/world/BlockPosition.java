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

    public BlockPosition(Position position) {
        this.x = (int) position.x;
        this.y = (int) position.y;
        this.z = (int) position.z;
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

    public double distance(BlockPos pos) {
        double dX = Math.abs(this.x - pos.getX());
        double dY = Math.abs(this.y - pos.getY());
        double dZ = Math.abs(this.z - pos.getZ());

        return (dX + dY + dZ) / 3.0D;
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

    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }

    public static BlockPosition fromBlockPos(BlockPos pos) {
        return new BlockPosition(pos.getX(), pos.getY(), pos.getZ());
    }
}

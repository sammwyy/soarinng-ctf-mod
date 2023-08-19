package com.sammwy.soactf.server.world;

import com.google.gson.annotations.Expose;

import net.minecraft.util.math.BlockPos;

public class Position {
    @Expose
    public double x;
    @Expose
    public double y;
    @Expose
    public double z;
    @Expose
    public float yaw;
    @Expose
    public float pitch;

    public Position(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Position(double x, double y, double z) {
        this(x, y, z, 0, 0);
    }

    public Position(Position position) {
        this(position.x, position.y, position.z, position.yaw, position.pitch);
    }

    public Position() {
        this(0, 0, 0, 0, 0);
    }

    public Position add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public Position subtract(double x, double y, double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    public Position multiply(double x, double y, double z) {
        this.x *= x;
        this.y *= y;
        this.z *= z;
        return this;
    }

    public Position divide(double x, double y, double z) {
        this.x /= x;
        this.y /= y;
        this.z /= z;
        return this;
    }

    public double distance(Position position) {
        return Math.sqrt(
                Math.pow(this.x - position.x, 2) + Math.pow(this.y - position.y, 2) + Math.pow(this.z - position.z, 2));
    }

    public double distanceSquared(Position position) {
        return Math.pow(this.x - position.x, 2) + Math.pow(this.y - position.y, 2) + Math.pow(this.z - position.z, 2);
    }

    public Position clone() {
        return new Position(this);
    }

    public Position set(Position position) {
        this.x = position.x;
        this.y = position.y;
        this.z = position.z;
        this.yaw = position.yaw;
        this.pitch = position.pitch;
        return this;
    }

    public BlockPos toBlockPos() {
        return new BlockPos((int) this.x, (int) this.y, (int) this.z);
    }

    public static Position fromBlockPos(BlockPos pos) {
        return new Position(pos.getX(), pos.getY(), pos.getZ());
    }
}

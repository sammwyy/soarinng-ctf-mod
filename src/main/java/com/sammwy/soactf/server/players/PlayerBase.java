package com.sammwy.soactf.server.players;

import com.sammwy.soactf.common.utils.TextUtils;
import com.sammwy.soactf.server.world.Position;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameMode;

public class PlayerBase {
    private ClientConnection connection;
    private ServerPlayerEntity entity;
    private PlayerInventory inventory;

    public PlayerBase(ClientConnection connection, ServerPlayerEntity entity) {
        this.connection = connection;
        this.entity = entity;
        this.inventory = new PlayerInventory(entity);
    }

    public ClientConnection getConnection() {
        return this.connection;
    }

    public ServerPlayerEntity getEntity() {
        return this.entity;
    }

    public GameMode getGameMode() {
        return this.entity.interactionManager.getGameMode();
    }

    public float getHealth() {
        return this.entity.getHealth();
    }

    public PlayerInventory getInventory() {
        return this.inventory;
    }

    public String getName() {
        return this.entity.getName().getString();
    }

    public Position getPosition() {
        return new Position(this.entity.getX(), this.entity.getY(), this.entity.getZ(), this.entity.getYaw(),
                this.entity.getPitch());
    }

    public String getUUID() {
        return this.entity.getUuidAsString();
    }

    public boolean isOP() {
        return this.entity.hasPermissionLevel(4);
    }

    public void kick(String reason) {
        this.entity.networkHandler.disconnect(TextUtils.from(reason));
    }

    public void kill() {
        this.entity.kill();
    }

    public void sendActionBar(String message) {
        this.entity.sendMessage(TextUtils.from(message), true);
    }

    public void sendMessage(String message) {
        this.entity.sendMessage(TextUtils.from(message), false);
    }

    public void sendTitle(String title, String subtitle, int fadeInTicks, int stayTicks, int fadeOutTicks) {
        TitleS2CPacket titlePacket = new TitleS2CPacket(TextUtils.from(title));
        this.entity.networkHandler.sendPacket(titlePacket);

        SubtitleS2CPacket subtitlePacket = new SubtitleS2CPacket(TextUtils.from(subtitle));
        this.entity.networkHandler.sendPacket(subtitlePacket);

        TitleFadeS2CPacket fadePacket = new TitleFadeS2CPacket(fadeInTicks, stayTicks, fadeOutTicks);
        this.entity.networkHandler.sendPacket(fadePacket);

    }

    public void setGameMode(GameMode mode) {
        this.entity.changeGameMode(mode);
    }

    public void setHealth(float health) {
        this.entity.setHealth(health);
    }

    public void teleport(Position position) {
        if (position == null) {
            this.sendMessage("&cError al teletransportar: la posicion es nula");
            return;
        }

        ServerWorld world = this.entity.getWorld();
        double x = position.x;
        double y = position.y;
        double z = position.z;
        float yaw = position.yaw;
        float pitch = position.pitch;
        this.entity.teleport(world, x, y, z, yaw, pitch);
    }
}

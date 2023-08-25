package com.sammwy.soactf.server.events.player;

import com.sammwy.soactf.server.events.Cancellable;

import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;

public class PlayerFoodLevelChangeEvent extends Cancellable {
    private PlayerEntity entity;
    private HungerManager hungerManager;
    private int oldFoodLevel;
    private int difference;
    private int newFoodLevel;

    public PlayerFoodLevelChangeEvent(PlayerEntity entity, HungerManager hungerManager, int newFoodLevel) {
        this.entity = entity;
        this.hungerManager = hungerManager;
        this.oldFoodLevel = hungerManager.getFoodLevel();
        this.difference = newFoodLevel - this.oldFoodLevel;
        this.newFoodLevel = newFoodLevel;
    }

    public PlayerEntity getEntity() {
        return this.entity;
    }

    public HungerManager getHungerManager() {
        return this.hungerManager;
    }

    public int getOldFoodLevel() {
        return this.oldFoodLevel;
    }

    public int getDifference() {
        return this.difference;
    }

    public int getNewFoodLevel() {
        return this.newFoodLevel;
    }

    public void setNewFoodLevel(int newFoodLevel) {
        this.newFoodLevel = newFoodLevel;
    }
}

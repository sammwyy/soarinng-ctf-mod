package com.sammwy.soactf.server.players;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerInventory {
    private ServerPlayerEntity entity;

    public PlayerInventory(ServerPlayerEntity entity) {
        this.entity = entity;
    }

    public void clearItem(int slot) {
        this.entity.getInventory().removeStack(slot);
        this.entity.getInventory().updateItems();
    }

    public ItemStack getItemInMainHand() {
        return this.entity.getInventory().getMainHandStack();
    }

    public void setBoots(ItemStack item) {
        this.entity.equipStack(EquipmentSlot.FEET, item);
    }

    public void setChest(ItemStack item) {
        this.entity.equipStack(EquipmentSlot.CHEST, item);
    }

    public void setHelmet(ItemStack item) {
        this.entity.equipStack(EquipmentSlot.HEAD, item);
    }

    public void setLeggings(ItemStack item) {
        this.entity.equipStack(EquipmentSlot.LEGS, item);
    }

    public void setItem(int slot, ItemStack item) {
        this.entity.getInventory().setStack(slot, item);
        this.sync();
    }

    public void sync() {
        this.entity.getInventory().updateItems();
    }

    public void equipArmor(ItemStack helmet, ItemStack chest, ItemStack leggings, ItemStack boots) {
        this.setHelmet(helmet);
        this.setChest(chest);
        this.setLeggings(leggings);
        this.setBoots(boots);
    }
}

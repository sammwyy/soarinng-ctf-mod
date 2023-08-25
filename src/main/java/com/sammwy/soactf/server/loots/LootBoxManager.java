package com.sammwy.soactf.server.loots;

import java.util.ArrayList;
import java.util.List;

import com.sammwy.soactf.common.utils.ItemUtils;
import com.sammwy.soactf.common.utils.WorldUtils;
import com.sammwy.soactf.server.SoaCTFServer;
import com.sammwy.soactf.server.config.impl.CTFArenaConfig;
import com.sammwy.soactf.server.config.impl.CTFConfiguration;
import com.sammwy.soactf.server.world.BlockPosition;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class LootBoxManager {
    private SoaCTFServer server;
    private List<LootBox> boxes;

    public LootBoxManager(SoaCTFServer server) {
        this.server = server;
        this.boxes = new ArrayList<>();
    }

    public void add(BlockPosition pos) {
        CTFConfiguration config = this.server.getConfig();
        LootBox box = new LootBox(pos, config.loots.items, config.loots.respawnCooldown);
        this.boxes.add(box);

        if (WorldUtils.isDefaultWorldLoaded()) {
            box.setInCooldown(false);
        }
    }

    public void addItem(ItemStack stack) {
        CTFConfiguration config = this.server.getConfig();
        config.loots.items.add(ItemUtils.itemsAsDescriptor(stack));
        config.save();

        for (LootBox box : this.boxes) {
            box.addItem(stack);
        }
    }

    public void create(BlockPosition pos) {
        CTFArenaConfig arena = this.server.getArenaConfig();
        arena.lootBoxes.add(pos);
        this.add(pos);
        arena.save();
    }

    public LootBox getAt(BlockPos pos) {
        for (LootBox box : this.boxes) {
            if (box.is(pos)) {
                return box;
            }
        }

        return null;
    }

    public List<LootBox> getBoxes() {
        return this.boxes;
    }

    public void load() {
        CTFArenaConfig arena = this.server.getArenaConfig();

        for (BlockPosition position : arena.lootBoxes) {
            this.add(position);
        }
    }

    public boolean remove(BlockPos pos) {
        LootBox box = this.getAt(pos);
        if (box == null) {
            return false;
        }

        CTFArenaConfig arena = this.server.getArenaConfig();
        arena.lootBoxes.remove(box.getPosition());
        arena.save();

        this.boxes.remove(box);
        return true;
    }

    public void resetAllCooldown() {
        for (LootBox box : this.boxes) {
            box.setInCooldown(false);
        }
    }

    public void tick() {
        for (LootBox box : this.boxes) {
            box.tick();
        }
    }
}

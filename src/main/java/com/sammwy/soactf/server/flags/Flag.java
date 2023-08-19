package com.sammwy.soactf.server.flags;

import com.sammwy.soactf.common.utils.PositionUtils;
import com.sammwy.soactf.common.utils.WorldUtils;
import com.sammwy.soactf.server.players.Player;
import com.sammwy.soactf.server.teams.CTFTeam;
import com.sammwy.soactf.server.world.BlockPosition;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class Flag {
    private CTFTeam team;
    private BlockPosition flagSpawn;
    private BlockPosition currentPosition;
    private Player capturedBy;
    private Player returnedBy;
    private FlagState state;
    private int returnTimer;
    private int currentReturnTimer;

    public Flag(CTFTeam team, int returnTimer, BlockPosition flagSpawn) {
        this.team = team;
        this.flagSpawn = flagSpawn;
        this.currentPosition = flagSpawn != null ? flagSpawn.clone() : null;
        this.state = FlagState.SAFE;
        this.returnTimer = returnTimer;
        this.currentReturnTimer = -1;
    }

    public ServerWorld getWorld() {
        return WorldUtils.getDefaultWorld();
    }

    public void despawnFlag() {
        if (this.getWorld() == null) {
            throw new IllegalStateException("Cannot despawn flag without a world");
        }

        if (this.currentPosition != null) {
            this.getWorld().setBlockState(this.currentPosition.toBlockPos(),
                    Block.getBlockFromItem(Items.AIR).getDefaultState());
        }
    }

    public void spawnFlag() {
        if (this.getWorld() == null) {
            throw new IllegalStateException("Cannot spawn flag without a world");
        }

        this.getWorld().setBlockState(this.currentPosition.toBlockPos(),
                this.getBlock().getDefaultState());
    }

    public void returnFlag() {
        if (this.state == FlagState.DROPPED) {
            this.despawnFlag();
        } else if (this.state == FlagState.CARRIED) {
            if (this.capturedBy != null) {
                this.capturedBy.setCapturedFlag(null);
                this.capturedBy = null;
            }
        }

        this.currentPosition = this.flagSpawn;
        this.state = FlagState.SAFE;
        this.spawnFlag();
    }

    public boolean dropFlag(Player player) {
        if (this.getWorld() == null) {
            throw new IllegalStateException("Cannot drop flag without a world");
        }

        player.setCapturedFlag(null);
        this.capturedBy = null;

        BlockPosition safePosition = PositionUtils.getSafeBlockPosition(this.getWorld(), player.getPosition());

        if (safePosition == null) {
            this.returnFlag();
            return false;
        } else {
            this.currentPosition = safePosition;
            this.state = FlagState.DROPPED;
            this.spawnFlag();
            this.currentReturnTimer = this.returnTimer;
            return true;
        }
    }

    public FlagCaptureResult capture(Player player) {
        boolean own = player.getTeam() == this.team;

        if (this.state == FlagState.SAFE && own) {
            return FlagCaptureResult.CANNOT_CAPTURE_OWN_FLAG;
        }

        else if (this.state == FlagState.SAFE && !own) {
            this.despawnFlag();
            this.capturedBy = player;
            this.currentPosition = null;
            this.state = FlagState.CARRIED;
            player.setCapturedFlag(this);
            return FlagCaptureResult.CAPTURED;
        }

        else if (this.state == FlagState.DROPPED && own) {
            this.returnFlag();
            this.returnedBy = player;
            return FlagCaptureResult.RETURNED;
        }

        else if (this.state == FlagState.DROPPED && !own) {
            if (this.capturedBy != null) {
                this.capturedBy.setCapturedFlag(null);
            }

            this.despawnFlag();
            this.currentPosition = null;
            this.capturedBy = player;
            player.setCapturedFlag(this);
            return FlagCaptureResult.CAPTURED_DROPPED;
        }

        else {
            return FlagCaptureResult.ALREADY_CAPTURED;
        }
    }

    public Block getBlock() {
        return this.team.getColor().getBlock();
    }

    public Player getCapturedBy() {
        return this.capturedBy;
    }

    public Item getItem() {
        return this.team.getColor().getItem();
    }

    public ItemStack getItemStack() {
        return new ItemStack(this.getItem(), 1);
    }

    public BlockPosition getPosition() {
        return this.currentPosition;
    }

    public Player getReturnedBy() {
        return this.returnedBy;
    }

    public BlockPosition getSpawn() {
        return this.flagSpawn;
    }

    public FlagState getState() {
        return this.state;
    }

    public CTFTeam getTeam() {
        return this.team;
    }

    public boolean isFlag(BlockPos pos) {
        if (this.state == FlagState.SAFE) {
            return this.flagSpawn.toBlockPos().equals(pos);
        } else {
            return this.currentPosition.toBlockPos().equals(pos);
        }
    }

    public boolean tickReturnTimer() {
        if (this.state == FlagState.DROPPED) {
            this.currentReturnTimer--;

            if (this.currentReturnTimer <= 0) {
                this.currentReturnTimer = -1;
                this.returnFlag();
                return true;
            }
        }

        return false;
    }
}

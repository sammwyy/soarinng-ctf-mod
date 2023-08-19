package com.sammwy.soactf.server;

import com.sammwy.soactf.server.events.Listener;
import com.sammwy.soactf.server.events.block.BlockBreakEvent;
import com.sammwy.soactf.server.events.block.BlockInteractEvent;
import com.sammwy.soactf.server.events.player.PlayerDisconnectEvent;
import com.sammwy.soactf.server.events.player.PlayerJoinEvent;
import com.sammwy.soactf.server.flags.Flag;
import com.sammwy.soactf.server.flags.FlagState;
import com.sammwy.soactf.server.players.Player;
import com.sammwy.soactf.server.teams.CTFTeam;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class SoaCTFHandler {
    private SoaCTFServer server;

    public SoaCTFHandler(SoaCTFServer server) {
        this.server = server;
        server.getEventManager().subscribeAll(this);
    }

    @Listener
    public void onPlayerJoin(PlayerJoinEvent e) {
        ClientConnection client = e.getConnection();
        ServerPlayerEntity entity = e.getPlayerEntity();

        Player player = this.server.getPlayerManager().addPlayer(client, entity);
        this.server.getGame().joinPlayer(player);
    }

    @Listener
    public void onPlayerDisconnect(PlayerDisconnectEvent e) {
        Player player = this.server.getPlayerManager().getPlayer(e.getPlayerEntity());
        this.server.getGame().leavePlayer(player);
    }

    @Listener
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = this.server.getPlayerManager().getPlayer(e.getEntity());

        if (player.isOP() && player.isSpectator()) {
            return;
        }

        e.cancel();

        if (!this.server.getGame().isRunning() || !player.isPlaying()
                || !e.getBlock().getName().getString().toLowerCase().contains("banner")) {
            return;
        }

        Flag flag = this.server.getGame().getFlagAt(e.getBlockPos());

        if (flag == null) {
            return;
        }

        this.server.getGame().captureFlag(player, flag);
    }

    @Listener
    public void onBlockInteract(BlockInteractEvent e) {
        Player player = this.server.getPlayerManager().getPlayer(e.getEntity());
        CTFTeam team = player.getTeam();
        BlockPos blockPos = e.getHitResult().getBlockPos();
        Item item = e.getStack().getItem();

        if (player.isOP() && player.isSpectator()) {
            return;
        }

        e.cancel();

        if (!this.server.getGame().isRunning() || !player.isPlaying() || item == null || item == Items.AIR
                || !item.getName().getString().toLowerCase().contains("banner")) {

            return;
        }

        if (team.getFlag().getState() == FlagState.SAFE) {
            double distance = team.getFlagSpawn().distance(blockPos);
            if (distance <= 2D) {
                this.server.getGame().goal(player);
            }
        }
    }
}

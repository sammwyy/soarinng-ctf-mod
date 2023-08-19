package com.sammwy.soactf.server;

import com.sammwy.soactf.server.events.Listener;
import com.sammwy.soactf.server.events.block.BlockBreakEvent;
import com.sammwy.soactf.server.events.block.BlockInteractEvent;
import com.sammwy.soactf.server.events.player.PlayerDisconnectEvent;
import com.sammwy.soactf.server.events.player.PlayerJoinEvent;
import com.sammwy.soactf.server.flags.Flag;
import com.sammwy.soactf.server.flags.FlagCaptureResult;
import com.sammwy.soactf.server.flags.FlagState;
import com.sammwy.soactf.server.players.Player;
import com.sammwy.soactf.server.teams.CTFTeam;

import net.minecraft.item.BannerItem;
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

        if (!player.isPlaying()) {
            e.cancel();
            player.sendMessage("&cNo puedes romper bloques en este estado.");
            return;
        }

        if (!e.getBlock().getName().getString().toLowerCase().contains("banner")) {
            e.cancel();
            player.sendMessage("&cNo puedes romper el mapa.");
            return;
        }

        Flag flag = this.server.getGame().getFlagAt(e.getBlockPos());

        if (flag == null) {
            e.cancel();
            player.sendMessage("&cNo puedes romper el mapa.");
            return;
        }

        FlagCaptureResult result = flag.capture(player);

        if (result == FlagCaptureResult.CANNOT_CAPTURE_OWN_FLAG) {
            e.cancel();
            player.sendMessage("&cNo puedes capturar tu propia bandera.");
            return;
        }
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

        if (player.isPlaying() || item == null || item == Items.AIR || !(item instanceof BannerItem)) {
            e.cancel();
            return;
        }

        if (team.getFlag().getState() == FlagState.SAFE) {
            double distance = team.getFlagSpawn().distance(blockPos);

            if (distance <= 3) {
                this.server.getGame().goal(player);
            }
        }

        e.cancel();
    }
}

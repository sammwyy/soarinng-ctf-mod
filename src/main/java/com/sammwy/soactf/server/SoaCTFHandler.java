package com.sammwy.soactf.server;

import com.sammwy.soactf.server.events.Listener;
import com.sammwy.soactf.server.events.block.BlockBreakEvent;
import com.sammwy.soactf.server.events.block.BlockInteractEvent;
import com.sammwy.soactf.server.events.entity.EntityTakeDamageEvent;
import com.sammwy.soactf.server.events.player.PlayerAttackEntityEvent;
import com.sammwy.soactf.server.events.player.PlayerBeforeDeathEvent;
import com.sammwy.soactf.server.events.player.PlayerDeathEvent;
import com.sammwy.soactf.server.events.player.PlayerDisconnectEvent;
import com.sammwy.soactf.server.events.player.PlayerFoodLevelChangeEvent;
import com.sammwy.soactf.server.events.player.PlayerJoinEvent;
import com.sammwy.soactf.server.events.player.PlayerRespawnEvent;
import com.sammwy.soactf.server.flags.Flag;
import com.sammwy.soactf.server.flags.FlagState;
import com.sammwy.soactf.server.loots.LootBox;
import com.sammwy.soactf.server.players.Player;
import com.sammwy.soactf.server.teams.CTFTeam;

import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;

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
        boolean isSponge = e.getBlock().asItem() == Items.SPONGE;
        boolean isBanner = e.getBlock().getName().getString().toLowerCase().contains("banner");

        if (player.isOP() && (player.isSpectator() || player.getGameMode() == GameMode.CREATIVE)) {
            if (isSponge && this.server.getLootBoxManager().remove(e.getBlockPos())) {
                player.sendMessage("&aHas removido esta caja de botin.");
            }

            return;
        }

        e.cancel();

        if (!this.server.getGame().isRunning() || !player.isPlaying()) {
            return;
        }

        if (isSponge) {
            LootBox box = this.server.getLootBoxManager().getAt(e.getBlockPos());
            if (box != null) {
                boolean result = box.claim();
                if (!result) {
                    player.sendMessage("&cEsta caja de botín se encuentra en enfriamiento.");
                }
            }
        } else if (isBanner) {
            Flag flag = this.server.getGame().getFlagAt(e.getBlockPos());
            if (flag != null) {
                this.server.getGame().captureFlag(player, flag);
                player.getInventory().sync();
            }
        }
    }

    @Listener
    public void onBlockInteract(BlockInteractEvent e) {
        if (e.getHand() == null || e.getHand() == Hand.OFF_HAND) {
            return;
        }

        Player player = this.server.getPlayerManager().getPlayer(e.getEntity());
        CTFTeam team = player.getTeam();
        BlockPos blockPos = e.getHitResult().getBlockPos();
        Item item = e.getStack().getItem();

        if (player.isOP() && (player.isSpectator() || player.getGameMode() == GameMode.CREATIVE)) {
            return;
        }

        e.cancel();

        if (!this.server.getGame().isRunning() || !player.isPlaying() || item == null || item == Items.AIR
                || !item.getName().getString().toLowerCase().contains("banner")) {

            return;
        }

        if (team.getFlag().getState() == FlagState.SAFE) {
            double distance = team.getFlagSpawn().distance(blockPos);
            if (distance <= 7D) {
                this.server.getGame().goal(player);
            }
        } else {
            player.sendMessage("&cTu bandera no se encuentra en su base.");
        }
    }

    @Listener
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player player = this.server.getPlayerManager().getPlayer(e.getEntity());

        if (player.isOP() && player.isSpectator()) {
            return;
        }

        if (!this.server.getGame().isRunning() || !player.isPlaying()) {
            return;
        }

        if (e.getSource().getAttacker() instanceof ServerPlayerEntity attackerEntity) {
            Player attacker = this.server.getPlayerManager().getPlayer(attackerEntity);
            attacker.addKill();
        }

        player.kill();
    }

    @Listener
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        Player player = this.server.getPlayerManager().getPlayer(e.getOldEntity());
        player.update(e.getNewEntity());
        player.prepareRespawn();
    }

    @Listener
    public void onPlayerBeforeDeath(PlayerBeforeDeathEvent e) {
        Player player = this.server.getPlayerManager().getPlayer(e.getEntity());

        if (player.isOP() && player.isSpectator()) {
            return;
        }

        e.cancel();

        if (!this.server.getGame().isRunning() || !player.isPlaying()) {
            return;
        }

        if (player.kill(false)) {
            player.prepareRespawn();

            if (e.getSource().getAttacker() instanceof ServerPlayerEntity attackerEntity) {
                Player attacker = this.server.getPlayerManager().getPlayer(attackerEntity);
                attacker.addKill();
            }
        }
    }

    @Listener
    public void onPlayerAttackEntity(PlayerAttackEntityEvent e) {
        Player player = this.server.getPlayerManager().getPlayer(e.getPlayer());

        if (player.isOP() && player.isSpectator()) {
            return;
        }

        if (!this.server.getGame().isRunning() || !player.isPlaying()) {
            e.cancel();
            return;
        }

        if (e.getEntity() instanceof ServerPlayerEntity entity) {
            Player target = this.server.getPlayerManager().getPlayer(entity);
            if (player.getTeam() == target.getTeam()) {
                e.cancel();
            }
        }
    }

    @Listener
    public void onEntityTakeDamage(EntityTakeDamageEvent e) {
        if (e.getEntity() instanceof ServerPlayerEntity entity) {
            Player player = this.server.getPlayerManager().getPlayer(entity);

            if (player.isOP() && player.isSpectator()) {
                e.cancel();
                return;
            }

            if (!this.server.getGame().isRunning() || !player.isPlaying()) {
                e.cancel();
                return;
            }

            if (e.getSource().isOf(DamageTypes.FALL) && !this.server.getConfig().game.fallDamage) {
                e.cancel();
                return;
            }

            if (e.getSource().isOf(DamageTypes.OUT_OF_WORLD)) {
                player.kill(false);
                player.prepareRespawn();
                player.teleport(this.server.getArenaConfig().spectator);
                e.cancel();
                return;
            }
        }
    }

    @Listener
    public void onPlayerFoodLevelChange(PlayerFoodLevelChangeEvent e) {
        if (this.server.getConfig().game.disableHunger) {
            e.setNewFoodLevel(20);
        }
    }
}

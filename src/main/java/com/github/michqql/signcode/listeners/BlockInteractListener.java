package com.github.michqql.signcode.listeners;

import com.github.michqql.signcode.SignCodePlugin;
import com.github.michqql.signcode.VaultManager;
import com.github.michqql.signcode.gui.CodeInputGui;
import com.github.michqql.signcode.vault.Vault;
import me.michqql.core.gui.GuiHandler;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class BlockInteractListener implements Listener {

    private final VaultManager vaultManager;
    private final GuiHandler guiHandler;

    public BlockInteractListener(SignCodePlugin plugin, VaultManager vaultManager) {
        this.vaultManager = vaultManager;
        this.guiHandler = new GuiHandler(plugin);
    }

    @EventHandler
    public void onSignInteract(PlayerInteractEvent e) {
        if(!e.hasBlock() || e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        // Clicked block should never be null (if event has block), this is to shut editor up
        assert e.getClickedBlock() != null;

        if(e.getClickedBlock().getState() instanceof Sign) {
            Sign sign = (Sign) e.getClickedBlock().getState();
            String line0 = sign.getLine(0);
            if(line0.equalsIgnoreCase("[VAULT]")) {
                new CodeInputGui(guiHandler, e.getPlayer(),
                        e.getClickedBlock(), vaultManager.getVault(e.getClickedBlock())).openGui();
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Block block = e.getBlockPlaced();
        if(block.getState() instanceof Sign) {
            vaultManager.markSignInCreation(e.getPlayer(), block);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        Player player = e.getPlayer();

        Vault vault = vaultManager.getVault(block);
        if(vault != null) {
            if(!player.isOp() && !player.getUniqueId().equals(vault.getOwner())) {
                player.sendMessage(ChatColor.RED + "You are not the owner of this vault, and so cannot break it!");
                e.setCancelled(true);
            }
        }
    }
}

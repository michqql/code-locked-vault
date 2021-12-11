package com.github.michqql.signcode;

import com.github.michqql.signcode.vault.Vault;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class VaultManager {

    private final HashMap<Block, Vault> signToVaultMap;
    private final HashMap<UUID, Block> signCreationMap;

    public VaultManager() {
        this.signToVaultMap = new HashMap<>();
        this.signCreationMap = new HashMap<>();
    }

    public void createVault(Block block, Vault vault) {
        Block marked = signCreationMap.remove(vault.getOwner());
        if(!block.equals(marked))
            return;

        signToVaultMap.put(block, vault);
    }

    public Vault getVault(Block block) {
        return signToVaultMap.get(block);
    }

    public void markSignInCreation(Player player, Block block) {
        signCreationMap.put(player.getUniqueId(), block);
    }

    public boolean isSignMarked(Block block) {
        return signCreationMap.containsValue(block);
    }
}

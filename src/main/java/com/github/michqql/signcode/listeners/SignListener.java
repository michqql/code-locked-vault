package com.github.michqql.signcode.listeners;

import com.github.michqql.signcode.VaultManager;
import com.github.michqql.signcode.vault.Vault;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class SignListener implements Listener {

    private final VaultManager vaultManager;

    public SignListener(VaultManager vaultManager) {
        this.vaultManager = vaultManager;
    }

    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        String[] lines = e.getLines();
        if(lines[0].equalsIgnoreCase("[VAULT]")) {
            String code = lines[1];
            if(isCodeValid(code)) {
                Vault vault = new Vault(e.getPlayer().getUniqueId());
                vault.getCode().setCode(convertCodeToIntArray(code));
                vaultManager.createVault(e.getBlock(), vault);

                e.setLine(1, "");
                e.setLine(2, "Owner:");
                e.setLine(3, e.getPlayer().getName());
                return;
            }

            e.getPlayer().sendMessage(ChatColor.RED + "Invalid code length! (" + code.length() + "/4)");
            e.setCancelled(true);
        }
    }

    private boolean isCodeValid(String str) {
        if(str.length() != 4)
            return false;

        char[] chars = str.toCharArray();
        for(char c : chars) {
            if (!Character.isDefined(c))
                return false;
        }

        return true;
    }

    private int[] convertCodeToIntArray(String str) {
        int[] code = new int[4];
        char[] chars = str.toCharArray();

        for(int i = 0; i < code.length; i++) {
            code[i] = Character.getNumericValue(chars[i]);
        }

        return code;
    }
}

package com.github.michqql.signcode.gui;

import com.github.michqql.signcode.vault.Code;
import com.github.michqql.signcode.vault.Vault;
import me.michqql.core.gui.Gui;
import me.michqql.core.gui.GuiHandler;
import me.michqql.core.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.type.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class CodeInputGui extends Gui {

    private final static int[] CODE_SLOTS = new int[]{
            28,         //   0    <-- Below rest of numbers, like Apple passcode design
            0, 1, 2,    // 1 2 3
            9, 10, 11,  // 4 5 6
            18, 19, 20  // 7 8 9
    };

    private final static int[] INPUT_SLOTS = new int[]{
            13, 14, 15, 16
    };

    private final static PotionEffect WITHER_EFFECT = new PotionEffect(PotionEffectType.WITHER, 40, 1);

    private final Block block;
    private final Vault vault;
    private final Code code;

    private boolean inputLocked;

    public CodeInputGui(GuiHandler guiHandler, Player player, Block block, Vault vault) {
        super(guiHandler, player);
        this.block = block;
        this.vault = vault;
        this.code = new Code();

        build("&eOpen vault...", 4);
    }

    @Override
    protected void createInventory() {
        updateInventory();
    }

    @Override
    protected void updateInventory() {
        super.clearSlots(0, inventory.getSize());

        // Loop through input values
        for(int i = 0; i < code.getLength(); i++) {
            int code = this.code.getCodeAt(i);
            if(code == -1) {
                this.inventory.setItem(INPUT_SLOTS[i], new ItemBuilder(Material.GRAY_CONCRETE)
                        .displayName("&0 ").getItem());
            } else {
                this.inventory.setItem(INPUT_SLOTS[i], new ItemBuilder(Material.YELLOW_CONCRETE)
                        .displayName("&e" + code).getItem());
            }
        }

        // Loop through passcode items
        for(int index = 0; index < CODE_SLOTS.length; index++) {
            this.inventory.setItem(CODE_SLOTS[index], new ItemBuilder(Material.GRAY_DYE)
                    .displayName("&f" + index).getItem());
        }
    }

    @Override
    protected void onClose() {

    }

    @Override
    protected boolean onClickEvent(Player player, int slot, ClickType clickType) {
        if(inputLocked)
            return true;

        // Check which passcode item the user clicked on
        int index = -1;
        for(int i = 0; i < CODE_SLOTS.length; i++) {
            if(CODE_SLOTS[i] == slot) {
                index = i;
                break;
            }
        }

        // If index != -1, they clicked on a code
        if(index != -1) {
            int nextFreeIndex = code.getNextFreeIndex();
            if(nextFreeIndex < 0 || nextFreeIndex >= 4) // Array out of bounds exception stopped
                return true;

            code.setCodeAt(nextFreeIndex, index);
            if(code.isComplete()) {
                if(checkCode())
                    successful();
                else
                    unsuccessful();
                return true;
            }
            updateInventory();
        }

        return true;
    }

    @Override
    protected boolean onPlayerInventoryClickEvent(Player player, int i, ClickType clickType) {
        return false;
    }

    private boolean checkCode() {
        return (code.isComplete() && vault.getCode().equals(code));
    }

    private void unsuccessful() {
        // Apply wither affect to user
        player.addPotionEffect(WITHER_EFFECT);
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_FALL, 0.5f, 0.3f);

        // Reset input
        for (int i = 0; i < code.getLength(); i++) {
            code.setCodeAt(i, -1);
        }

        // Stop user interacting with inventory temporarily
        this.inputLocked = true;

        // Show error message
        ItemStack denied = new ItemBuilder(Material.REDSTONE_BLOCK)
                .displayName("&4Denied! Password incorrect...").getItem();
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, denied);
        }

        // Re-enable interaction and reset screen
        new BukkitRunnable() {
            @Override
            public void run() {
                inputLocked = false;
                updateInventory();
            }
        }.runTaskLater(plugin, 40L);
    }

    private void successful() {
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 0.8f);
        player.closeInventory();

        Sign sign = (Sign) block.getBlockData();
        Block target = block.getRelative(sign.getRotation().getOppositeFace());

        if(!(target.getBlockData() instanceof Powerable))
            return;

        new BukkitRunnable() {
            int ticks;

            @Override
            public void run() {
                if(ticks >= 20) {
                    this.cancel();
                    return;
                }

                Powerable powerable = (Powerable) target.getBlockData();
                powerable.setPowered(true);
                target.setBlockData(powerable);
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}

package dev.creepysalhack.util.entity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class PlayerUtil {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    private PlayerUtil() {}

    public static boolean isNull() {
        return mc.player == null || mc.world == null;
    }

    /** Center of the block the player is standing on. */
    public static Vec3d getBlockCenter() {
        if (isNull()) return Vec3d.ZERO;
        BlockPos feet = mc.player.getBlockPos();
        return new Vec3d(feet.getX() + 0.5, feet.getY(), feet.getZ() + 0.5);
    }

    /** How many totems of undying the player has in their inventory. */
    public static int countTotems() {
        if (isNull()) return 0;
        int count = 0;
        for (ItemStack s : mc.player.getInventory().main) {
            if (s.getItem() == Items.TOTEM_OF_UNDYING) count++;
        }
        return count;
    }

    /** Slot index (0-35) of the first totem in the inventory, -1 if none. */
    public static int findTotemSlot() {
        if (isNull()) return -1;
        for (int i = 0; i < mc.player.getInventory().main.size(); i++) {
            if (mc.player.getInventory().main.get(i).getItem() == Items.TOTEM_OF_UNDYING)
                return i;
        }
        return -1;
    }

    /** True when the player is moving on the ground. */
    public static boolean isMoving() {
        if (isNull()) return false;
        return mc.player.input.movementForward != 0 || mc.player.input.movementSideways != 0;
    }

    /** Direction-aware horizontal speed. */
    public static double getHorizontalSpeed() {
        if (isNull()) return 0;
        double dx = mc.player.getX() - mc.player.prevX;
        double dz = mc.player.getZ() - mc.player.prevZ;
        return Math.sqrt(dx * dx + dz * dz);
    }

    public static float getHealth() {
        if (isNull()) return 0;
        return mc.player.getHealth() + mc.player.getAbsorptionAmount();
    }
}

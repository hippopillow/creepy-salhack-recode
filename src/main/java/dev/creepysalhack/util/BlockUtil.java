package dev.creepysalhack.util;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public final class BlockUtil {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    private BlockUtil() {}

    /** True if the given block pos is obsidian or bedrock (crystal-safe). */
    public static boolean isCrystalBase(BlockPos pos) {
        if (mc.world == null) return false;
        Block b = mc.world.getBlockState(pos).getBlock();
        return b == Blocks.OBSIDIAN || b == Blocks.BEDROCK;
    }

    /** True if the pos is fully air (entity can stand there). */
    public static boolean isAir(BlockPos pos) {
        if (mc.world == null) return false;
        return mc.world.getBlockState(pos).isAir();
    }

    /** True if the pos is a valid hole block (obsidian or bedrock on all 4 sides & below). */
    public static boolean isHoleBlock(BlockPos pos) {
        if (mc.world == null) return false;
        return isSolid(pos.north()) && isSolid(pos.south())
            && isSolid(pos.east()) && isSolid(pos.west())
            && isSolid(pos.down());
    }

    public static boolean isSolid(BlockPos pos) {
        if (mc.world == null) return false;
        return mc.world.getBlockState(pos).isSolidBlock(mc.world, pos);
    }

    /** Place a block on top of the given pos using a packet (no swing). */
    public static void placeBlockPacket(BlockPos pos, Hand hand) {
        if (mc.player == null || mc.getNetworkHandler() == null) return;
        Vec3d hitVec = new Vec3d(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
        BlockHitResult hit = new BlockHitResult(hitVec, Direction.UP, pos, false);
        mc.getNetworkHandler().sendPacket(new PlayerInteractBlockC2SPacket(hand, hit, 0));
    }

    /** Slot index (hotbar 0-8) of the first matching item class, -1 if not found. */
    public static int findItemSlot(Class<?> itemClass) {
        if (mc.player == null) return -1;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (itemClass.isInstance(stack.getItem())) return i;
        }
        return -1;
    }

    /** Switch hotbar slot silently (no animation). */
    public static void switchSlot(int slot) {
        if (mc.player == null) return;
        mc.player.getInventory().selectedSlot = slot;
    }

    /** Distance from the player's eye to the centre of a block. */
    public static double distanceTo(BlockPos pos) {
        if (mc.player == null) return Double.MAX_VALUE;
        return mc.player.getEyePos().distanceTo(
            new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
    }
}

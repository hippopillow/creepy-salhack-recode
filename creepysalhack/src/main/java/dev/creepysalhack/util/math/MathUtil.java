package dev.creepysalhack.util.math;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class MathUtil {

    private MathUtil() {}

    /** Yaw in degrees from the player's eye to a target position. */
    public static float getYaw(Vec3d target) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return 0;
        Vec3d eye = mc.player.getEyePos();
        double dx = target.x - eye.x;
        double dz = target.z - eye.z;
        return (float) Math.toDegrees(Math.atan2(dz, dx)) - 90f;
    }

    /** Pitch in degrees from the player's eye to a target position. */
    public static float getPitch(Vec3d target) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return 0;
        Vec3d eye = mc.player.getEyePos();
        double dx = target.x - eye.x;
        double dy = target.y - eye.y;
        double dz = target.z - eye.z;
        double dist = Math.sqrt(dx * dx + dz * dz);
        return (float) -Math.toDegrees(Math.atan2(dy, dist));
    }

    /** Wraps angle to [-180, 180]. */
    public static float wrapDegrees(float angle) {
        return MathHelper.wrapDegrees(angle);
    }

    /** Normalises a raw angle difference. */
    public static float getAngleDiff(float from, float to) {
        float diff = to - from;
        while (diff > 180)  diff -= 360;
        while (diff < -180) diff += 360;
        return diff;
    }

    /** Clamp a float. */
    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }

    /** Clamp a double. */
    public static double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }

    /**
     * Approximate explosion damage dealt to a target at {@code targetPos}
     * from a crystal placed at {@code crystalPos}.
     */
    public static float calcExplosionDamage(Vec3d crystalPos, Vec3d targetPos,
                                            float maxHealth, float power) {
        double dist = crystalPos.distanceTo(targetPos);
        double exposure = 1.0 - (dist / (power * 2.0));
        if (exposure < 0) return 0;
        float impact = (float) ((exposure * exposure + exposure) / 2.0 * 7.0 * power * 2.0 + 1.0);
        // simplified — does not factor armour; extend as needed
        return impact * 0.85f;
    }
}

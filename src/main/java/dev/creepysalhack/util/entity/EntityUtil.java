package dev.creepysalhack.util.entity;

import dev.creepysalhack.CreepySalHack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class EntityUtil {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    private EntityUtil() {}

    /** All living entities in the world, excluding the local player. */
    public static List<LivingEntity> getLivingEntities() {
        if (mc.world == null) return List.of();
        List<LivingEntity> list = new ArrayList<>();
        mc.world.getEntities().forEach(e -> {
            if (e instanceof LivingEntity living && e != mc.player) list.add(living);
        });
        return list;
    }

    /** All player entities in the world, excluding the local player. */
    public static List<PlayerEntity> getPlayers() {
        if (mc.world == null) return List.of();
        return mc.world.getPlayers().stream()
                .filter(p -> p != mc.player)
                .toList();
    }

    /** Nearest player within range; null if none. */
    public static PlayerEntity getNearestPlayer(double range) {
        if (mc.player == null || mc.world == null) return null;
        return getPlayers().stream()
                .filter(p -> p.distanceTo(mc.player) <= range)
                .min(Comparator.comparingDouble(p -> p.distanceTo(mc.player)))
                .orElse(null);
    }

    /** Nearest living entity within range; null if none. */
    public static LivingEntity getNearestEntity(double range) {
        if (mc.player == null || mc.world == null) return null;
        return getLivingEntities().stream()
                .filter(e -> e.distanceTo(mc.player) <= range)
                .min(Comparator.comparingDouble(e -> e.distanceTo(mc.player)))
                .orElse(null);
    }

    /** Health of a living entity including absorption. */
    public static float getHealth(LivingEntity e) {
        return e.getHealth() + e.getAbsorptionAmount();
    }

    /** True if the given entity is a friend. */
    public static boolean isFriend(Entity e) {
        if (!(e instanceof PlayerEntity p)) return false;
        return CreepySalHack.getFriendManager().isFriend(p.getGameProfile().getName());
    }

    /** Eye position of an entity. */
    public static Vec3d getEyePos(Entity e) {
        return new Vec3d(e.getX(), e.getEyeY(), e.getZ());
    }

    /** Interpolated bounding box for the current frame delta. */
    public static Box getInterpolatedBox(Entity e, float delta) {
        Vec3d pos = e.getLerpedPos(delta);
        Box bb = e.getBoundingBox();
        double hw = (bb.maxX - bb.minX) / 2.0;
        double h  =  bb.maxY - bb.minY;
        return new Box(pos.x - hw, pos.y, pos.z - hw,
                       pos.x + hw, pos.y + h, pos.z + hw);
    }
}

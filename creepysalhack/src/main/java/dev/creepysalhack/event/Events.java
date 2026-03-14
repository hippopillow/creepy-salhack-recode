package dev.creepysalhack.event;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.Packet;

/** All game events used by the client. */
public final class Events {

    // ── Tick ─────────────────────────────────────────────────────────────────
    public static class ClientTickEvent extends Event {
        public enum Phase { PRE, POST }
        private final Phase phase;
        public ClientTickEvent(Phase phase) { this.phase = phase; }
        public Phase getPhase() { return phase; }
    }

    // ── Player movement ───────────────────────────────────────────────────────
    public static class PlayerMoveEvent extends CancellableEvent {
        private double x, y, z;
        public PlayerMoveEvent(double x, double y, double z) { this.x=x; this.y=y; this.z=z; }
        public double getX() { return x; } public void setX(double x) { this.x = x; }
        public double getY() { return y; } public void setY(double y) { this.y = y; }
        public double getZ() { return z; } public void setZ(double z) { this.z = z; }
    }

    public static class PlayerJumpEvent extends CancellableEvent {}

    public static class PlayerUpdateEvent extends Event {
        public enum Phase { PRE, POST }
        private final Phase phase;
        public PlayerUpdateEvent(Phase phase) { this.phase = phase; }
        public Phase getPhase() { return phase; }
    }

    // ── Network ───────────────────────────────────────────────────────────────
    public static class PacketSendEvent extends CancellableEvent {
        private Packet<?> packet;
        public PacketSendEvent(Packet<?> packet) { this.packet = packet; }
        public Packet<?> getPacket() { return packet; }
        public void setPacket(Packet<?> p) { this.packet = p; }
    }

    public static class PacketReceiveEvent extends CancellableEvent {
        private Packet<?> packet;
        public PacketReceiveEvent(Packet<?> packet) { this.packet = packet; }
        public Packet<?> getPacket() { return packet; }
    }

    // ── Render ────────────────────────────────────────────────────────────────
    public static class RenderWorldEvent extends Event {
        private final MatrixStack matrices;
        private final float tickDelta;
        public RenderWorldEvent(MatrixStack matrices, float tickDelta) {
            this.matrices = matrices; this.tickDelta = tickDelta;
        }
        public MatrixStack getMatrices() { return matrices; }
        public float getTickDelta() { return tickDelta; }
    }

    public static class RenderHudEvent extends Event {
        private final MatrixStack matrices;
        private final float tickDelta;
        public RenderHudEvent(MatrixStack matrices, float tickDelta) {
            this.matrices = matrices; this.tickDelta = tickDelta;
        }
        public MatrixStack getMatrices() { return matrices; }
        public float getTickDelta() { return tickDelta; }
    }

    // ── Entity ────────────────────────────────────────────────────────────────
    public static class EntityRenderEvent extends CancellableEvent {
        private final Entity entity;
        public EntityRenderEvent(Entity entity) { this.entity = entity; }
        public Entity getEntity() { return entity; }
    }

    // ── Key ───────────────────────────────────────────────────────────────────
    public static class KeyPressEvent extends Event {
        private final int keyCode;
        public KeyPressEvent(int keyCode) { this.keyCode = keyCode; }
        public int getKeyCode() { return keyCode; }
    }

    // ── Chat ──────────────────────────────────────────────────────────────────
    public static class SendChatEvent extends CancellableEvent {
        private String message;
        public SendChatEvent(String message) { this.message = message; }
        public String getMessage() { return message; }
        public void setMessage(String msg) { this.message = msg; }
    }

    // ── Attack ────────────────────────────────────────────────────────────────
    public static class AttackEntityEvent extends CancellableEvent {
        private final Entity target;
        public AttackEntityEvent(Entity target) { this.target = target; }
        public Entity getTarget() { return target; }
    }

    private Events() {}
}

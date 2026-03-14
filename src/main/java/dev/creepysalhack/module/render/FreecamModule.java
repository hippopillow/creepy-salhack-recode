package dev.creepysalhack.module.render;

import dev.creepysalhack.event.EventBus;
import dev.creepysalhack.event.Events;
import dev.creepysalhack.module.Module;
import dev.creepysalhack.module.Setting;
import net.minecraft.util.math.Vec3d;

public class FreecamModule extends Module {

    private final Setting<Float> speed = register(new Setting<>(
            "Speed", "Camera movement speed", 1.0f, 0.1f, 5.0f));

    private Vec3d savedPos   = null;
    private float savedYaw   = 0;
    private float savedPitch = 0;

    // Camera position offset from player
    private double camX, camY, camZ;

    public FreecamModule() {
        super("Freecam", "Detaches your camera so you can look around freely", Category.RENDER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (mc.player == null) return;
        savedPos   = mc.player.getPos();
        savedYaw   = mc.player.getYaw();
        savedPitch = mc.player.getPitch();
        camX = savedPos.x;
        camY = savedPos.y + mc.player.getEyeHeight(mc.player.getPose());
        camZ = savedPos.z;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (mc.player == null || savedPos == null) return;
        mc.player.setPosition(savedPos);
        mc.player.setYaw(savedYaw);
        mc.player.setPitch(savedPitch);
        savedPos = null;
    }

    @EventBus.EventHandler
    public void onPlayerUpdate(Events.PlayerUpdateEvent event) {
        if (event.getPhase() != Events.PlayerUpdateEvent.Phase.PRE) return;
        if (mc.player == null) return;

        float spd     = speed.getValue();
        float yawRad  = (float) Math.toRadians(mc.player.getYaw());
        float pitchRad= (float) Math.toRadians(mc.player.getPitch());

        double forward = mc.player.input.movementForward  * spd * 0.1;
        double strafe  = mc.player.input.movementSideways * spd * 0.1;
        double up      = (mc.player.input.jumping   ? spd * 0.1 : 0)
                       - (mc.player.input.sneaking  ? spd * 0.1 : 0);

        // Move camera in look direction
        camX -= Math.sin(yawRad)  * forward + Math.cos(yawRad) * strafe;
        camY += -Math.sin(pitchRad) * forward + up;
        camZ += Math.cos(yawRad)  * forward - Math.sin(yawRad) * strafe;

        // Cancel actual player movement
        mc.player.setVelocity(Vec3d.ZERO);
        mc.player.setPosition(savedPos);
    }

    @Override
    public String getArrayListSuffix() { return String.format("%.1f", speed.getValue()); }
}

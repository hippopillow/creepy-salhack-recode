package dev.creepysalhack.mixin;

import dev.creepysalhack.CreepySalHack;
import dev.creepysalhack.event.Events;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer {

    @Inject(
        method = "renderEntity",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onRenderEntity(Entity entity,
                                double cameraX, double cameraY, double cameraZ,
                                float tickProgress,
                                net.minecraft.client.util.math.MatrixStack matrices,
                                net.minecraft.client.render.VertexConsumerProvider vertexConsumers,
                                CallbackInfo ci) {
        Events.EntityRenderEvent ev = CreepySalHack.getEventManager()
                .post(new Events.EntityRenderEvent(entity));
        if (ev.isCancelled()) ci.cancel();
    }
}

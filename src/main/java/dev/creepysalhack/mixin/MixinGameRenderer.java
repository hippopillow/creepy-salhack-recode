package dev.creepysalhack.mixin;

import dev.creepysalhack.CreepySalHack;
import dev.creepysalhack.event.Events;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @Inject(
        method = "renderWorld",
        at = @At(value = "INVOKE",
                 target = "Lnet/minecraft/client/render/WorldRenderer;render(Lnet/minecraft/client/render/RenderTickCounter;ZLnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/GameRenderer;Lnet/minecraft/client/render/LightmapTextureManager;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V",
                 shift = At.Shift.AFTER)
    )
    private void onRenderWorld(net.minecraft.client.render.RenderTickCounter tickCounter, CallbackInfo ci) {
        MatrixStack matrices = new MatrixStack();
        CreepySalHack.getEventManager().post(
            new Events.RenderWorldEvent(matrices, tickCounter.getTickDelta(true)));
    }
}

package dev.creepysalhack.mixin;

import dev.creepysalhack.CreepySalHack;
import dev.creepysalhack.event.Events;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class MixinInGameHud {

    @Inject(method = "render", at = @At("TAIL"))
    private void onRenderHud(net.minecraft.client.gui.DrawContext context,
                             net.minecraft.client.render.RenderTickCounter tickCounter,
                             CallbackInfo ci) {
        MatrixStack matrices = context.getMatrices();
        float delta = tickCounter.getTickDelta(true);

        // fire HUD render event – all HUD elements listen here
        CreepySalHack.getEventManager().post(new Events.RenderHudEvent(matrices, delta));

        // render HUD elements
        for (var el : CreepySalHack.getHudManager().getElements()) {
            if (el.isEnabled()) el.render(context, delta);
        }
    }
}

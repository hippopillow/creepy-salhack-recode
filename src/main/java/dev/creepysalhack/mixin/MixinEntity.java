package dev.creepysalhack.mixin;

import dev.creepysalhack.CreepySalHack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class MixinEntity {

    @Shadow public abstract boolean equals(Object o);

    /**
     * Hook setVelocity so the Velocity module can zero-out
     * knockback by detecting when the method is called on the local player.
     */
    @Inject(method = "setVelocity(Lnet/minecraft/util/math/Vec3d;)V",
            at = @At("HEAD"), cancellable = true)
    private void onSetVelocity(Vec3d velocity, CallbackInfo ci) {
        if (CreepySalHack.mc() == null) return;
        Entity self = (Entity)(Object)this;
        if (self == CreepySalHack.mc().player) {
            boolean cancel = CreepySalHack.getModuleManager().isEnabled("Velocity");
            if (cancel) ci.cancel();
        }
    }
}

package dev.creepysalhack.mixin;

import dev.creepysalhack.CreepySalHack;
import dev.creepysalhack.event.Events;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTickPre(CallbackInfo ci) {
        CreepySalHack.getEventManager().post(
            new Events.ClientTickEvent(Events.ClientTickEvent.Phase.PRE));
        CreepySalHack.getNotifManager().tick();
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTickPost(CallbackInfo ci) {
        CreepySalHack.getEventManager().post(
            new Events.ClientTickEvent(Events.ClientTickEvent.Phase.POST));
    }
}

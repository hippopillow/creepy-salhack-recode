package dev.creepysalhack.mixin;

import dev.creepysalhack.CreepySalHack;
import dev.creepysalhack.event.Events;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerEntity {

    /** PRE player update event – used by combat/movement modules. */
    @Inject(method = "tick", at = @At("HEAD"))
    private void onPreUpdate(CallbackInfo ci) {
        CreepySalHack.getEventManager().post(
            new Events.PlayerUpdateEvent(Events.PlayerUpdateEvent.Phase.PRE));
    }

    /** POST player update event. */
    @Inject(method = "tick", at = @At("TAIL"))
    private void onPostUpdate(CallbackInfo ci) {
        CreepySalHack.getEventManager().post(
            new Events.PlayerUpdateEvent(Events.PlayerUpdateEvent.Phase.POST));
    }

    /** Intercept jump – lets NoJumpDelay/Velocity modules cancel it. */
    @Inject(method = "jump", at = @At("HEAD"), cancellable = true)
    private void onJump(CallbackInfo ci) {
        Events.PlayerJumpEvent ev = CreepySalHack.getEventManager()
                .post(new Events.PlayerJumpEvent());
        if (ev.isCancelled()) ci.cancel();
    }

    /** Intercept chat sends – command processor. */
    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void onSendChat(String message, CallbackInfo ci) {
        Events.SendChatEvent ev = CreepySalHack.getEventManager()
                .post(new Events.SendChatEvent(message));
        if (ev.isCancelled()) { ci.cancel(); return; }
        if (CreepySalHack.getCommandManager().handle(ev.getMessage())) ci.cancel();
    }
}

package rlmixins.mixin.icenfire;

import com.github.alexthe666.iceandfire.event.EventLiving;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({EventLiving.class})
public abstract class EventLivingMixin {
   public EventLivingMixin() {
   }

   @Inject(
      method = {"onPlayerAttackMob"},
      at = {@At("HEAD")},
      remap = false,
      cancellable = true
   )
   public void rlmixins_iceAndFireEventLiving_onPlayerAttackMob(AttackEntityEvent event, CallbackInfo ci) {
      ci.cancel();
   }
}

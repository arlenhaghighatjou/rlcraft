package rlmixins.mixin.bountifulbaubles;

import cursedflames.bountifulbaubles.event.EventHandler;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({EventHandler.class})
public abstract class EventHandlerMixin {
   public EventHandlerMixin() {
   }

   @Redirect(
      method = {"onDamage(Lnet/minecraftforge/event/entity/living/LivingAttackEvent;)V"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/util/DamageSource;isFireDamage()Z"
)
   )
   private static boolean rlmixins_bountifulBaublesEventHandler_onDamageLivingAttack(DamageSource instance) {
      return false;
   }

   @Redirect(
      method = {"onDamage(Lnet/minecraftforge/event/entity/living/LivingHurtEvent;)V"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/util/DamageSource;isFireDamage()Z"
)
   )
   private static boolean rlmixins_bountifulBaublesEventHandler_onDamageLivingHurt(DamageSource instance) {
      return false;
   }
}

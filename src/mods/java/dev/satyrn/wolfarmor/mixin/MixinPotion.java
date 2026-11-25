package dev.satyrn.wolfarmor.mixin;

import dev.satyrn.wolfarmor.common.event.PotionEffectEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Potion.class})
public class MixinPotion {
   public MixinPotion() {
   }

   @Inject(
      method = {"performEffect"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void beforePerformEffect(EntityLivingBase entityLivingBaseIn, int amplifier, CallbackInfo ci) {
      if (MinecraftForge.EVENT_BUS.post(new PotionEffectEvent((Potion)this, entityLivingBaseIn, amplifier))) {
         ci.cancel();
      }

   }
}

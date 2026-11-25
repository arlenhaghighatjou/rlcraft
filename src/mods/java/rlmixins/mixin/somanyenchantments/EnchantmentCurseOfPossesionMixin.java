package rlmixins.mixin.somanyenchantments;

import com.Shultrea.Rin.Ench0_4_0.EnchantmentCurseofPossession;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({EnchantmentCurseofPossession.class})
public abstract class EnchantmentCurseOfPossesionMixin {
   public EnchantmentCurseOfPossesionMixin() {
   }

   @Inject(
      method = {"onExist"},
      at = {@At("HEAD")},
      cancellable = true,
      remap = false
   )
   public void rlmixins_soManyEnchantmentsEnchantmentCurseOfPossesion_onExist(WorldTickEvent event, CallbackInfo ci) {
      ci.cancel();
   }
}

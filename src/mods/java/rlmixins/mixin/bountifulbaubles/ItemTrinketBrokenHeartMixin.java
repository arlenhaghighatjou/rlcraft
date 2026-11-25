package rlmixins.mixin.bountifulbaubles;

import cursedflames.bountifulbaubles.item.ItemTrinketBrokenHeart;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ItemTrinketBrokenHeart.class})
public abstract class ItemTrinketBrokenHeartMixin {
   public ItemTrinketBrokenHeartMixin() {
   }

   @Inject(
      method = {"onDamage"},
      at = {@At("HEAD")},
      cancellable = true,
      remap = false
   )
   private static void rlmixins_bountifulBaublesEventHandler_onDamageLivingDamage(LivingDamageEvent event, CallbackInfo ci) {
      ci.cancel();
   }
}

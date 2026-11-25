package dev.satyrn.wolfarmor.mixin;

import dev.satyrn.wolfarmor.api.entity.IFoodStatsCreature;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.passive.EntityTameable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({EntityAISit.class})
public class MixinEntityAISit {
   @Shadow
   private boolean isSitting;
   private IFoodStatsCreature creacher;

   public MixinEntityAISit() {
   }

   @Inject(
      method = {"<init>"},
      at = {@At("RETURN")}
   )
   private void onInit(EntityTameable entityIn, CallbackInfo ci) {
      if (entityIn instanceof IFoodStatsCreature) {
         this.creacher = (IFoodStatsCreature)entityIn;
      }

   }

   @Inject(
      method = {"setSitting"},
      at = {@At("HEAD")}
   )
   private void onSetSitting(boolean sitting, CallbackInfo ci) {
      if (this.creacher != null && this.isSitting != sitting) {
         this.creacher.addExhaustion(0.01F * (this.isSitting ? 5.0F : 1.0F));
      }

   }
}

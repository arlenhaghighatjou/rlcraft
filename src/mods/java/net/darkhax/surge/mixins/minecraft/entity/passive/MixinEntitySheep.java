package net.darkhax.surge.mixins.minecraft.entity.passive;

import net.darkhax.surge.core.SurgeConfiguration;
import net.darkhax.surge.lib.DyeBlending;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.EnumDyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({EntitySheep.class})
public class MixinEntitySheep {
   public MixinEntitySheep() {
   }

   @Inject(
      method = {"getDyeColorMixFromParents(Lnet/minecraft/entity/passive/EntityAnimal;Lnet/minecraft/entity/passive/EntityAnimal;)Lnet/minecraft/item/EnumDyeColor;"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void getDyeColorMixFromParents(EntityAnimal father, EntityAnimal mother, CallbackInfoReturnable<EnumDyeColor> info) {
      if (SurgeConfiguration.sheepDyeBlendTable && father instanceof EntitySheep && mother instanceof EntitySheep) {
         EnumDyeColor fatherColor = ((EntitySheep)father).getFleeceColor();
         EnumDyeColor motherColor = ((EntitySheep)mother).getFleeceColor();
         EnumDyeColor blended = DyeBlending.getBlendedColor(fatherColor, motherColor);
         if (blended != null) {
            info.setReturnValue(blended);
         } else {
            info.setReturnValue(father.world.rand.nextBoolean() ? fatherColor : motherColor);
         }
      }

   }
}

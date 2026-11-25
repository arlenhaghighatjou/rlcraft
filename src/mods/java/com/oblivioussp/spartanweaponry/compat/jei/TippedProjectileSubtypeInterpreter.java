package com.oblivioussp.spartanweaponry.compat.jei;

import mezz.jei.api.ISubtypeRegistry.ISubtypeInterpreter;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;

public class TippedProjectileSubtypeInterpreter implements ISubtypeInterpreter {
   public static final TippedProjectileSubtypeInterpreter INSTANCE = new TippedProjectileSubtypeInterpreter();

   private TippedProjectileSubtypeInterpreter() {
   }

   public String apply(ItemStack itemStack) {
      if (!itemStack.hasTagCompound()) {
         return null;
      } else {
         PotionType potionType = PotionUtils.getPotionFromItem(itemStack);
         String potionTypeString = potionType.getNamePrefixed("");
         StringBuilder stringBuilder = new StringBuilder(potionTypeString);

         for(PotionEffect effect : PotionUtils.getEffectsFromStack(itemStack)) {
            stringBuilder.append(";").append(effect);
         }

         return stringBuilder.toString();
      }
   }
}

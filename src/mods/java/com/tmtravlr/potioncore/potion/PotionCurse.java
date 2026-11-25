package com.tmtravlr.potioncore.potion;

import com.tmtravlr.potioncore.PotionCoreHelper;
import net.minecraft.entity.EntityLivingBase;

public class PotionCurse extends PotionCorePotion {
   public static final String NAME = "curse";
   public static final PotionCurse INSTANCE = new PotionCurse();

   public PotionCurse() {
      super("curse", true, 0);
   }

   public boolean isInstant() {
      return true;
   }

   public void performEffect(EntityLivingBase entity, int amplifier) {
      if (!entity.world.isRemote) {
         entity.world.getMinecraftServer().addScheduledTask(() -> {
            for(int i = 0; i <= amplifier; ++i) {
               PotionCoreHelper.addPotionEffectNegative(entity);
            }

         });
      }

   }
}

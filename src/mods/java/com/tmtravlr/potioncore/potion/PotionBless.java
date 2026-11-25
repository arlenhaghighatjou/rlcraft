package com.tmtravlr.potioncore.potion;

import com.tmtravlr.potioncore.PotionCoreHelper;
import net.minecraft.entity.EntityLivingBase;

public class PotionBless extends PotionCorePotion {
   public static final String NAME = "bless";
   public static final PotionBless INSTANCE = new PotionBless();

   public PotionBless() {
      super("bless", false, 6737151);
   }

   public boolean isInstant() {
      return true;
   }

   public void performEffect(EntityLivingBase entity, int amplifier) {
      if (!entity.world.isRemote) {
         entity.world.getMinecraftServer().addScheduledTask(() -> {
            for(int i = 0; i <= amplifier; ++i) {
               PotionCoreHelper.addPotionEffectPositive(entity);
            }

         });
      }

   }
}

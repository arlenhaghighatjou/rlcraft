package com.tmtravlr.potioncore.potion;

import com.tmtravlr.potioncore.PotionCoreHelper;
import net.minecraft.entity.EntityLivingBase;

public class PotionChance extends PotionCorePotion {
   public static final String NAME = "chance";
   public static final PotionChance INSTANCE = new PotionChance();

   public PotionChance() {
      super("chance", false, 255);
   }

   public boolean isInstant() {
      return true;
   }

   public void performEffect(EntityLivingBase entity, int amplifier) {
      if (!entity.world.isRemote) {
         entity.world.getMinecraftServer().addScheduledTask(() -> {
            for(int i = 0; i <= amplifier; ++i) {
               if (entity.getRNG().nextBoolean()) {
                  PotionCoreHelper.addPotionEffectPositive(entity);
               } else {
                  PotionCoreHelper.addPotionEffectNegative(entity);
               }
            }

         });
      }

   }
}

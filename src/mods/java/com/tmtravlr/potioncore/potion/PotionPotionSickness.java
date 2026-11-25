package com.tmtravlr.potioncore.potion;

import com.tmtravlr.potioncore.PotionCoreHelper;
import net.minecraft.entity.EntityLivingBase;

public class PotionPotionSickness extends PotionCorePotion {
   public static final String NAME = "potion_sickness";
   public static final PotionPotionSickness INSTANCE = new PotionPotionSickness();
   public static int potionsForSickness = 8;
   public static int chanceForSickness = 1000;

   public PotionPotionSickness() {
      super("potion_sickness", true, 16711935);
      this.setEnabled(false);
   }

   public void performEffect(EntityLivingBase entity, int amplifier) {
      if (!entity.world.isRemote && amplifier + 1 > 0 && entity.getRNG().nextInt(chanceForSickness / (amplifier + 1)) == 0) {
         entity.world.getMinecraftServer().addScheduledTask(() -> {
            PotionCoreHelper.addPotionEffectNegative(entity);
         });
      }

   }
}

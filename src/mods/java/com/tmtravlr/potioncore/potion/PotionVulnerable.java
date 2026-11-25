package com.tmtravlr.potioncore.potion;

import com.tmtravlr.potioncore.PotionCoreAttributes;

public class PotionVulnerable extends PotionCorePotion {
   public static final String NAME = "vulnerable";
   public static final PotionVulnerable INSTANCE = new PotionVulnerable();
   public static float damageMultiplier = -0.5F;

   public PotionVulnerable() {
      super("vulnerable", true, 5601024);
   }

   public void registerPotionAttributeModifiers() {
      this.registerPotionAttributeModifier(PotionCoreAttributes.DAMAGE_RESISTANCE, "6b165744-5259-44e1-b600-e169bc06173b", (double)damageMultiplier, 2);
   }
}

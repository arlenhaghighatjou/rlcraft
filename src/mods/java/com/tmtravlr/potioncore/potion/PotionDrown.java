package com.tmtravlr.potioncore.potion;

import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;

public class PotionDrown extends PotionCorePotion {
   public static final String NAME = "drown";
   public static final String TAG_NAME = "potioncore - drown air";
   public static final String TAG_BOOLEAN = "potioncore - doing drown";
   public static final PotionDrown INSTANCE = new PotionDrown();

   public PotionDrown() {
      super("drown", true, 65535);
   }

   public void performEffect(EntityLivingBase entity, int amplifier) {
      if (!entity.getEntityData().hasKey("potioncore - drown air")) {
         entity.getEntityData().setInteger("potioncore - drown air", 300);
      }

      if (entity.isInsideOfMaterial(Material.WATER)) {
         entity.setAir(300);
         entity.getEntityData().setInteger("potioncore - drown air", 300);
      }

      int respiration = EnchantmentHelper.getRespirationModifier(entity);
      int air = entity.getEntityData().getInteger("potioncore - drown air");
      air = respiration > 0 && entity.getRNG().nextInt(respiration + 1) > 0 ? air : air - 1;
      if (air == -20) {
         air = 0;
         if (!entity.world.isRemote) {
            entity.attackEntityFrom(DamageSource.DROWN, 2.0F);
         }
      }

      entity.getEntityData().setInteger("potioncore - drown air", air);
   }
}

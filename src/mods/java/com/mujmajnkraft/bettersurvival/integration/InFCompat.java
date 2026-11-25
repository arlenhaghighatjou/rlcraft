package com.mujmajnkraft.bettersurvival.integration;

import com.github.alexthe666.iceandfire.core.ModItems;
import com.github.alexthe666.iceandfire.entity.EntityDeathWorm;
import com.github.alexthe666.iceandfire.entity.EntityFireDragon;
import com.github.alexthe666.iceandfire.entity.EntityIceDragon;
import com.github.alexthe666.iceandfire.entity.FrozenEntityProperties;
import com.github.alexthe666.iceandfire.entity.StoneEntityProperties;
import javax.annotation.Nullable;
import net.ilexiconn.llibrary.server.entity.EntityPropertiesHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.util.EnumHelper;

public abstract class InFCompat {
   public static final ToolMaterial SILVER = ModItems.silverTools;
   public static final ToolMaterial DRAGON_BONE = ModItems.boneTools;
   public static final ToolMaterial DRAGON_BONE_FLAMED = ModItems.fireBoneTools;
   public static final ToolMaterial DRAGON_BONE_ICED = ModItems.iceBoneTools;
   public static final ToolMaterial JUNGLE_CHITIN = EnumHelper.addToolMaterial("JungleChitin", ModItems.myrmexChitin.getHarvestLevel(), ModItems.myrmexChitin.getMaxUses(), ModItems.myrmexChitin.getEfficiency(), ModItems.myrmexChitin.getAttackDamage(), ModItems.myrmexChitin.getEnchantability());
   public static final ToolMaterial DESERT_CHITIN = EnumHelper.addToolMaterial("DesertChitin", ModItems.myrmexChitin.getHarvestLevel(), ModItems.myrmexChitin.getMaxUses(), ModItems.myrmexChitin.getEfficiency(), ModItems.myrmexChitin.getAttackDamage(), ModItems.myrmexChitin.getEnchantability());

   public InFCompat() {
   }

   public static float getMaterialModifier(ToolMaterial mat, EntityLivingBase target, @Nullable EntityPlayer player) {
      return getMaterialModifier(mat, target, player, true);
   }

   public static float getMaterialModifier(ToolMaterial mat, EntityLivingBase target, @Nullable EntityPlayer player, boolean effect) {
      if (mat == SILVER) {
         if (target.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD) {
            return 2.0F;
         }
      } else if (mat != DESERT_CHITIN && mat != JUNGLE_CHITIN) {
         if (mat == DRAGON_BONE_ICED) {
            if (effect) {
               FrozenEntityProperties frozenProps = (FrozenEntityProperties)EntityPropertiesHandler.INSTANCE.getProperties(target, FrozenEntityProperties.class);
               frozenProps.setFrozenFor(200);
               target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 100, 2));
               target.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 100, 2));
               if (player != null) {
                  target.knockBack(target, 1.0F, player.posX - target.posX, player.posZ - target.posZ);
               }
            }

            if (isFireDragon(target)) {
               return 8.0F;
            }
         } else if (mat == DRAGON_BONE_FLAMED) {
            if (effect) {
               target.setFire(5);
               if (player != null) {
                  target.knockBack(target, 1.0F, player.posX - target.posX, player.posZ - target.posZ);
               }
            }

            if (isIceDragon(target)) {
               return 8.0F;
            }
         }
      } else {
         if (target.getCreatureAttribute() != EnumCreatureAttribute.ARTHROPOD) {
            return 4.0F;
         }

         if (isDeathworm(target)) {
            return 4.0F;
         }
      }

      return 0.0F;
   }

   public static boolean isDeathworm(Entity entity) {
      return entity instanceof EntityDeathWorm;
   }

   public static boolean isFireDragon(Entity entity) {
      return entity instanceof EntityFireDragon;
   }

   public static boolean isIceDragon(Entity entity) {
      return entity instanceof EntityIceDragon;
   }

   public static boolean isStoned(EntityLivingBase entity) {
      StoneEntityProperties properties = (StoneEntityProperties)EntityPropertiesHandler.INSTANCE.getProperties(entity, StoneEntityProperties.class);
      return properties != null && properties.isStone;
   }
}

package com.Shultrea.Rin.Ench0_4_0;

import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Enum.EnumList;
import com.Shultrea.Rin.Interfaces.ISubjectEnchantment;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

public class EnchantmentSubjectEnchantments extends EnchantmentBase implements ISubjectEnchantment {
   private static final String[] DAMAGE_NAMES = new String[]{"Mathematics", "Science", "History", "Physics", "English", "PE"};
   private static final int[] MIN_COST = new int[]{8, 9, 7, 11, 5, 6};
   private static final int[] LEVEL_COST = new int[]{13, 14, 10, 15, 9, 10};
   private static final int[] LEVEL_COST_SPAN = new int[]{25, 28, 23, 30, 21, 20};
   public final int damageType;

   public EnchantmentSubjectEnchantments(Rarity rarityIn, EnumEnchantmentType type, int damageTypeIn, EntityEquipmentSlot... slots) {
      super(rarityIn, EnumList.SWORD, slots);
      this.damageType = damageTypeIn;
      this.type = type;
   }

   public boolean isConfigEnabled() {
      switch(this.damageType) {
      case 0:
         return ModConfig.enabled.Mathematics;
      case 1:
         return ModConfig.enabled.Science;
      case 2:
         return ModConfig.enabled.History;
      case 3:
         return false;
      case 4:
         return ModConfig.enabled.English;
      case 5:
         return ModConfig.enabled.PE;
      default:
         return false;
      }
   }

   public int getMaxLevel() {
      switch(this.damageType) {
      case 0:
         return ModConfig.level.Mathematics;
      case 1:
         return ModConfig.level.Science;
      case 2:
         return ModConfig.level.History;
      case 3:
         return 4;
      case 4:
         return ModConfig.level.English;
      case 5:
         return ModConfig.level.PE;
      default:
         return 4;
      }
   }

   public int getMinEnchantability(int enchantmentLevel) {
      return MIN_COST[this.damageType] + (enchantmentLevel - 1) * LEVEL_COST[this.damageType];
   }

   public int getMaxEnchantability(int enchantmentLevel) {
      return this.getMinEnchantability(enchantmentLevel) + LEVEL_COST_SPAN[this.damageType];
   }

   public float calcDamageByCreature(int level, EnumCreatureAttribute creatureType) {
      if (!this.isConfigEnabled()) {
         return 0.0F;
      } else {
         return this.damageType == 5 ? 0.75F + (float)level * 0.25F : 0.8F + (float)level * 0.3F;
      }
   }

   public String getName() {
      return "enchantment." + DAMAGE_NAMES[this.damageType];
   }

   public boolean canApplyTogether(Enchantment ench) {
      return !(ench instanceof EnchantmentSubjectEnchantments);
   }

   public boolean canApply(ItemStack stack) {
      return stack.getItem() instanceof ItemAxe ? true : super.canApply(stack);
   }

   public void onEntityDamagedAlt(EntityLivingBase user, Entity target, ItemStack stack, int level) {
      if (target instanceof EntityLivingBase) {
         EntityLivingBase entitylivingbase = (EntityLivingBase)target;
         if (this.damageType == 1 && ModConfig.enabled.Science && user.getRNG().nextDouble() * 100.0D < 20.0D) {
            user.getEntityWorld().newExplosion(user, target.posX, target.posY - 1.5D, target.posZ, 1.1F + (float)level * 0.4F, false, false);
         }

         if (this.damageType == 3 && user.getRNG().nextDouble() * 180.0D < 15.0D) {
            user.getEntityWorld().newExplosion(user, target.posX, target.posY - 1.5D, target.posZ, 1.0F + (float)level * 0.65F, true, false);
            user.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 60, 0, true, true));
         }

         if (this.damageType == 5 && ModConfig.enabled.PE && user.getRNG().nextDouble() * 100.0D < 8.5D) {
            if (level == 1 || level == 2) {
               user.addPotionEffect(new PotionEffect(MobEffects.HASTE, 150 + level * 30, level - 1));
               user.addPotionEffect(new PotionEffect(MobEffects.SPEED, 50 + level * 5, level - 1));
            }

            if (level == 3 || level == 4) {
               user.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 10 + level * 5, level - 4));
               user.addPotionEffect(new PotionEffect(MobEffects.HASTE, 150 + level * 30, level - 2));
               user.addPotionEffect(new PotionEffect(MobEffects.SPEED, 50 + level * 5, level - 2));
               user.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 20 + level * 5, level - 3));
            }

            if (level >= 5) {
               user.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 10 + level * 5, level - 1));
               user.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 20 + level * 5, level - 2));
               user.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 10 + level * 5, level - 4));
               user.addPotionEffect(new PotionEffect(MobEffects.HASTE, 150 + level * 5, level - 2));
               user.addPotionEffect(new PotionEffect(MobEffects.SPEED, 50 + level * 5, level - 2));
               user.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 20 + level * 5, level - 3));
            }
         }
      }

   }

   public boolean isTreasureEnchantment() {
      return true;
   }
}

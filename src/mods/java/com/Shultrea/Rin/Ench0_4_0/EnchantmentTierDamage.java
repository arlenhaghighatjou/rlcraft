package com.Shultrea.Rin.Ench0_4_0;

import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Interfaces.IEnchantmentDamage;
import com.Shultrea.Rin.Interfaces.IEnhancedEnchantment;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentDamage;
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

public class EnchantmentTierDamage extends EnchantmentBase implements IEnchantmentDamage, IEnhancedEnchantment {
   private static final String[] DAMAGE_NAMES = new String[]{"LesserSharpness", "SupremeSharpness", "LesserSmite", "SupremeSmite", "LesserBaneOfArthropods", "SupremeBaneOfArthropods"};
   private static final int[] MIN_COST = new int[]{1, 120, 1, 120, 1, 120};
   private static final int[] LEVEL_COST = new int[]{4, 80, 5, 80, 4, 80};
   private static final int[] LEVEL_COST_SPAN = new int[]{20, 180, 20, 180, 20, 180};
   public final int damageType;

   public EnchantmentTierDamage(Rarity rarityIn, EnumEnchantmentType type, int damageTypeIn, EntityEquipmentSlot... slots) {
      super(rarityIn, EnumEnchantmentType.WEAPON, slots);
      this.damageType = damageTypeIn;
      this.type = type;
   }

   public boolean isConfigEnabled() {
      switch(this.damageType) {
      case 0:
         return ModConfig.enabled.LesserSharpness;
      case 1:
         return ModConfig.enabled.SupremeSharpness;
      case 2:
         return ModConfig.enabled.LesserSmite;
      case 3:
         return ModConfig.enabled.SupremeSmite;
      case 4:
         return ModConfig.enabled.LesserBaneOfArthropods;
      case 5:
         return ModConfig.enabled.SupremeBaneOfArthropods;
      default:
         return false;
      }
   }

   public int getMaxLevel() {
      switch(this.damageType) {
      case 0:
         return ModConfig.level.LesserSharpness;
      case 1:
         return ModConfig.level.SupremeSharpness;
      case 2:
         return ModConfig.level.LesserSmite;
      case 3:
         return ModConfig.level.SupremeSmite;
      case 4:
         return ModConfig.level.LesserBaneOfArthropods;
      case 5:
         return ModConfig.level.SupremeBaneOfArthropods;
      default:
         return 5;
      }
   }

   public int getMinEnchantability(int enchantmentLevel) {
      return MIN_COST[this.damageType] + (enchantmentLevel - 1) * LEVEL_COST[this.damageType];
   }

   public int getMaxEnchantability(int enchantmentLevel) {
      return this.getMinEnchantability(enchantmentLevel) + LEVEL_COST_SPAN[this.damageType];
   }

   public float calcDamageByCreature(int level, EnumCreatureAttribute creatureType) {
      return this.damageType == 0 ? 0.25F + (float)level * 0.25F : (this.damageType == 1 ? 4.0F + (float)level * 1.6F : (this.damageType == 2 && creatureType == EnumCreatureAttribute.UNDEAD ? 1.25F * (float)level : (this.damageType == 3 && creatureType == EnumCreatureAttribute.UNDEAD ? 5.0F * (float)level : (this.damageType == 4 && creatureType == EnumCreatureAttribute.ARTHROPOD ? 1.25F * (float)level : (this.damageType == 5 && creatureType == EnumCreatureAttribute.ARTHROPOD ? 5.0F * (float)level : 0.0F)))));
   }

   public String getName() {
      return "enchantment." + DAMAGE_NAMES[this.damageType];
   }

   public boolean canApplyTogether(Enchantment ench) {
      return !(ench instanceof EnchantmentTierDamage) && !(ench instanceof EnchantmentDamage) && !(ench instanceof IEnchantmentDamage);
   }

   public boolean canApply(ItemStack stack) {
      return stack.getItem() instanceof ItemAxe ? true : super.canApply(stack);
   }

   public void onEntityDamagedAlt(EntityLivingBase user, Entity target, ItemStack stack, int level) {
      if (target instanceof EntityLivingBase) {
         EntityLivingBase entitylivingbase = (EntityLivingBase)target;
         if (this.damageType == 5 && entitylivingbase.getCreatureAttribute() == EnumCreatureAttribute.ARTHROPOD) {
            int i = 10 + user.getRNG().nextInt(5 * level);
            entitylivingbase.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, i, 1));
         }

         if (this.damageType == 6 && entitylivingbase.getCreatureAttribute() == EnumCreatureAttribute.ARTHROPOD) {
            int i = 40 + user.getRNG().nextInt(20 * level);
            entitylivingbase.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, i, 5));
         }
      }

   }

   public boolean isTreasureEnchantment() {
      switch(this.damageType) {
      case 0:
         return false;
      case 1:
         return true;
      case 2:
         return false;
      case 3:
         return true;
      case 4:
         return false;
      case 5:
         return true;
      default:
         return false;
      }
   }
}

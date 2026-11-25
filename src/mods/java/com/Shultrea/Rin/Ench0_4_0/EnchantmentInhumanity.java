package com.Shultrea.Rin.Ench0_4_0;

import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Interfaces.IEnchantmentDamage;
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

public class EnchantmentInhumanity extends EnchantmentBase implements IEnchantmentDamage {
   public EnchantmentInhumanity() {
      super(Rarity.RARE, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
      this.setName("Inhumane");
      this.setRegistryName("Inhumane");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.Inhumane;
   }

   public int getMaxLevel() {
      return ModConfig.level.Inhumane;
   }

   public int getMinEnchantability(int par1) {
      return 15 + 15 * (par1 - 1);
   }

   public int getMaxEnchantability(int par1) {
      return super.getMinEnchantability(par1) + 40;
   }

   public boolean canApplyTogether(Enchantment fTest) {
      return super.canApplyTogether(fTest) && !(fTest instanceof EnchantmentDamage) && !(fTest instanceof IEnchantmentDamage);
   }

   public boolean canApply(ItemStack fTest) {
      return fTest.getItem() instanceof ItemAxe ? true : super.canApply(fTest);
   }

   public void onEntityDamagedAlt(EntityLivingBase user, Entity entiti, ItemStack stack, int level) {
      if (ModConfig.enabled.Inhumane) {
         if (entiti instanceof EntityLivingBase) {
            EntityLivingBase entity = (EntityLivingBase)entiti;
            if (entity.getCreatureAttribute() == EnumCreatureAttribute.ILLAGER) {
               entity.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 70 + level * 10, 1));
            }

         }
      }
   }

   public float calcDamageByCreature(int level, EnumCreatureAttribute creatureType) {
      return creatureType == EnumCreatureAttribute.ILLAGER ? (float)level * 2.5F : 0.0F;
   }
}

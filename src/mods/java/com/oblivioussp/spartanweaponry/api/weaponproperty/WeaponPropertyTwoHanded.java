package com.oblivioussp.spartanweaponry.api.weaponproperty;

import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.api.SpartanWeaponryAPI;
import com.oblivioussp.spartanweaponry.api.ToolMaterialEx;
import com.oblivioussp.spartanweaponry.compat.trinkets.TrinketsHelper;
import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class WeaponPropertyTwoHanded extends WeaponPropertyWithCallback {
   public WeaponPropertyTwoHanded(String propType, String propModId, int propLevel, float propMagnitude) {
      super(propType, propModId, propLevel, propMagnitude);
   }

   public WeaponProperty.PropertyQuality getQuality() {
      return WeaponProperty.PropertyQuality.NEGATIVE;
   }

   protected void addTooltipDescription(ItemStack stack, List<String> tooltip) {
      if (ModSpartanWeaponry.isTrinketsLoaded) {
         tooltip.add(TextFormatting.ITALIC + "  " + SpartanWeaponryAPI.internalHandler.translateFormattedString(this.type + ".desc.xat", "tooltip", this.modId, this.magnitude * 100.0F));
      } else {
         tooltip.add(TextFormatting.ITALIC + "  " + SpartanWeaponryAPI.internalHandler.translateFormattedString(this.type + ".desc", "tooltip", this.modId, this.magnitude * 100.0F));
      }

   }

   public void onItemUpdate(ToolMaterialEx material, ItemStack stack, World world, EntityLivingBase entity, int itemSlot, boolean isSelected) {
      ItemStack mainHand = entity.getHeldItemMainhand();
      ItemStack offHand = entity.getHeldItemOffhand();
      PotionEffect mfEffect = entity.getActivePotionEffect(MobEffects.MINING_FATIGUE);
      if ((ItemStack.areItemsEqualIgnoreDurability(stack, mainHand) || ItemStack.areItemsEqualIgnoreDurability(stack, offHand)) && !mainHand.isEmpty() && !offHand.isEmpty()) {
         boolean isImmune = ModSpartanWeaponry.isTrinketsLoaded && TrinketsHelper.getEntityRace(entity) == "Titan";
         int mfLevel = this.level == 2 ? 3 : 2;
         if (!isImmune && (mfEffect == null || mfEffect.getDuration() <= 1)) {
            entity.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 20, mfLevel, false, false));
         }
      } else if (mfEffect != null && mfEffect.getDuration() <= 0) {
         entity.removePotionEffect(MobEffects.MINING_FATIGUE);
      }

   }

   public float modifyDamageDealt(ToolMaterialEx material, float baseDamage, DamageSource source, EntityLivingBase attacker, EntityLivingBase victim) {
      float resultDamage = baseDamage;
      ItemStack mainHand = attacker.getHeldItemMainhand();
      ItemStack offHand = attacker.getHeldItemOffhand();
      if (ModSpartanWeaponry.isTrinketsLoaded && TrinketsHelper.getEntityRace(attacker) == "Titan") {
         return baseDamage;
      } else {
         if (!mainHand.isEmpty() && !offHand.isEmpty()) {
            resultDamage = baseDamage * (1.0F - this.magnitude);
         }

         return resultDamage;
      }
   }
}

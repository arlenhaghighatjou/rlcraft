package com.Shultrea.Rin.Ench0_4_0;

import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Enchantments_Sector.Smc_040;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class EnchantmentStrengthenedVitality extends EnchantmentBase {
   public static final EaglercraftUUID CACHED_UUID = EaglercraftUUID.fromString("e681-134f-4c54-a535-29c3ae5c7a21");

   public EnchantmentStrengthenedVitality() {
      super(Rarity.VERY_RARE, EnumEnchantmentType.ARMOR_CHEST, new EntityEquipmentSlot[]{EntityEquipmentSlot.CHEST});
      this.setName("strengthenedvitality");
      this.setRegistryName("strengthenedvitality");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.StrengthenedVitality;
   }

   public int getMaxLevel() {
      return ModConfig.level.StrengthenedVitality;
   }

   public int getMinEnchantability(int par1) {
      return 25 + 15 * (par1 - 1);
   }

   public int getMaxEnchantability(int par1) {
      return this.getMinEnchantability(par1) + 75;
   }

   public boolean canApplyTogether(Enchantment e) {
      return super.canApplyTogether(e);
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void updateHealth(PlayerTickEvent fEvent) {
      if (fEvent.phase == Phase.END) {
         int i = EnchantmentHelper.getMaxEnchantmentLevel(Smc_040.strengthenedvitality, fEvent.player);
         if (i <= 0) {
            this.RemoveHealth(fEvent.player);
         } else {
            this.AddHealth(fEvent.player);
         }
      }
   }

   private void AddHealth(EntityPlayer fEntity) {
      int level = EnchantmentHelper.getMaxEnchantmentLevel(Smc_040.strengthenedvitality, fEntity);
      IAttributeInstance speedAttr = fEntity.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH);
      AttributeModifier modSpeed = new AttributeModifier(CACHED_UUID, "StrengthenedHealthBoost", 0.1D * (double)level, 2);
      speedAttr.removeModifier(modSpeed);
      speedAttr.applyModifier(modSpeed);
      if (speedAttr.getModifier(CACHED_UUID) == null) {
         ;
      }
   }

   private void RemoveHealth(EntityLivingBase fEntity) {
      int level = EnchantmentHelper.getMaxEnchantmentLevel(Smc_040.strengthenedvitality, fEntity);
      IAttributeInstance speedAttr = fEntity.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH);
      if (speedAttr.getModifier(CACHED_UUID) != null) {
         AttributeModifier modSpeed = new AttributeModifier(CACHED_UUID, "StrengthenedHealthBoost", 0.2D * (double)level, 2);
         speedAttr.removeModifier(modSpeed);
      }
   }
}

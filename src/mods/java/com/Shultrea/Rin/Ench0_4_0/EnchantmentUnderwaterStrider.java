package com.Shultrea.Rin.Ench0_4_0;

import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentWaterWalker;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class EnchantmentUnderwaterStrider extends EnchantmentBase {
   public static final EaglercraftUUID CACHED_UUID = EaglercraftUUID.fromString("a612fe81-132f-4c58-a335-13c4ae5cba21");

   public EnchantmentUnderwaterStrider() {
      super(Rarity.RARE, EnumEnchantmentType.ARMOR_FEET, new EntityEquipmentSlot[]{EntityEquipmentSlot.FEET});
      this.setName("UnderwaterStrider");
      this.setRegistryName("UnderwaterStrider");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.UnderwaterStrider;
   }

   public int getMaxLevel() {
      return ModConfig.level.UnderwaterStrider;
   }

   public int getMinEnchantability(int par1) {
      return 15 + 15 * (par1 - 1);
   }

   public int getMaxEnchantability(int par1) {
      return this.getMinEnchantability(par1) + 30;
   }

   public boolean canApplyTogether(Enchantment e) {
      return super.canApplyTogether(e) && !(e instanceof EnchantmentWaterWalker);
   }

   @SubscribeEvent
   public void onUnder(PlayerTickEvent e) {
      if (e.phase != Phase.END) {
         EntityPlayer player = e.player;
         int level = EnchantmentHelper.getMaxEnchantmentLevel(this, player);
         IAttributeInstance s = player.getEntityAttribute(EntityPlayer.SWIM_SPEED);
         if (level <= 0) {
            this.removeSpeed(s, player);
         } else {
            IBlockState water = player.world.getBlockState(new BlockPos(player.posX, player.posY + 1.0D, player.posZ));
            if (player.isInWater() && water.getMaterial() == Material.WATER) {
               this.removeSpeed(s, player);
               this.addSpeed(s, player);
            } else {
               this.removeSpeed(s, player);
            }

         }
      }
   }

   private void addSpeed(IAttributeInstance s, EntityLivingBase p) {
      int level = EnchantmentHelper.getMaxEnchantmentLevel(this, p);
      if (s.getModifier(CACHED_UUID) == null) {
         AttributeModifier modSpeed = new AttributeModifier(CACHED_UUID, "moveSpeed", 1.3D + (double)level * 0.4D, 1);
         s.removeModifier(modSpeed);
         s.applyModifier(modSpeed);
      }
   }

   private void removeSpeed(IAttributeInstance s, EntityLivingBase p) {
      int level = EnchantmentHelper.getMaxEnchantmentLevel(this, p);
      if (s.getModifier(CACHED_UUID) != null) {
         AttributeModifier modSpeed = new AttributeModifier(CACHED_UUID, "moveSpeed", 1.3D + (double)level * 0.4D, 1);
         s.removeModifier(modSpeed);
      }
   }
}

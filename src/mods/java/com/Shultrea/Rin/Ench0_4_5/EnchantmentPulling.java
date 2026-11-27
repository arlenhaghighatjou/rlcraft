package com.Shultrea.Rin.Ench0_4_5;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.Shultrea.Rin.Ench0_3_0.EnchantmentAdvancedPunch;
import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import com.Shultrea.Rin.Prop_Sector.ArrowPropertiesProvider;
import com.Shultrea.Rin.Prop_Sector.IArrowProperties;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;

public class EnchantmentPulling extends EnchantmentBase {
   public EnchantmentPulling() {
      super(Rarity.VERY_RARE, EnumEnchantmentType.BOW, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
      this.setName("Pulling");
      this.setRegistryName("Pulling");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.Pulling;
   }

   public int getMaxLevel() {
      return ModConfig.level.Pulling;
   }

   public int getMinEnchantability(int par1) {
      return 10 + (par1 - 1) * 8;
   }

   public int getMaxEnchantability(int par1) {
      return this.getMinEnchantability(par1) + 30;
   }

   public boolean canApplyTogether(Enchantment fTest) {
      return fTest != Enchantments.PUNCH && !(fTest instanceof EnchantmentAdvancedPunch) ? super.canApplyTogether(fTest) : false;
   }

   public void register(EventBus bus) {
      bus.register(EntityJoinWorldEvent.class, EventPriority.HIGHEST, true, this::onEvent);
   }

   public void onEvent(EntityJoinWorldEvent event) {
      if (event.getEntity() instanceof EntityArrow) {
         EntityArrow arrow = (EntityArrow)event.getEntity();
         EntityLivingBase shooter = (EntityLivingBase)arrow.shootingEntity;
         if (shooter == null) {
            return;
         }

         ItemStack bow = shooter instanceof EntityPlayer ? shooter.getActiveItemStack() : (!shooter.getHeldItemMainhand().isEmpty() ? shooter.getHeldItemMainhand() : shooter.getHeldItemOffhand());
         if (bow == null || bow == ItemStack.EMPTY) {
            bow = shooter.getHeldItemOffhand();
            if (bow == null || bow == ItemStack.EMPTY) {
               return;
            }
         }

         int p = EnchantmentHelper.getEnchantmentLevel(this, bow);
         if (p > 0) {
            if (!arrow.hasCapability(ArrowPropertiesProvider.ARROWPROPERTIES_CAP, (EnumFacing)null)) {
               return;
            }

            IArrowProperties properties = (IArrowProperties)arrow.getCapability(ArrowPropertiesProvider.ARROWPROPERTIES_CAP, (EnumFacing)null);
            properties.setPullPower(1.25F + (float)p * 1.75F);
         }
      }

   }
}

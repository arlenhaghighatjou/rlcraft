package com.Shultrea.Rin.Enchantment_Base_Sector;

import com.Shultrea.Rin.Main_Sector.ModConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.eventhandler.EventBus;

public abstract class EnchantmentBase extends Enchantment {
   private boolean registered = false;

   public EnchantmentBase(Rarity rarityIn, EnumEnchantmentType typeIn, EntityEquipmentSlot[] slots) {
      super(rarityIn, typeIn, slots);
   }

   public abstract boolean isConfigEnabled();

   public void register(EventBus bus) {
   }

   public boolean isEnabled() {
      return this.isRegistered() && this.isConfigEnabled();
   }

   public void setRegistered() {
      this.registered = true;
   }

   public boolean isRegistered() {
      return this.registered;
   }

   public int getMaxLevel() {
      return 1;
   }

   public boolean isAllowedOnBooks() {
      return this.isEnabled();
   }

   public boolean canApplyAtEnchantingTable(ItemStack stack) {
      return this.isEnabled() && stack.getItem().canApplyAtEnchantingTable(stack, this);
   }

   public boolean canApply(ItemStack stack) {
      return this.isEnabled() && super.canApply(stack);
   }

   public String getTranslatedName(int level) {
      String s = I18n.translateToLocal(this.getName());
      if (!this.isEnabled()) {
         s = TextFormatting.DARK_RED + "" + TextFormatting.STRIKETHROUGH + s;
      } else if (this.isCurse()) {
         s = TextFormatting.RED + s;
      } else {
         s = this.getPrefix() + s;
      }

      return level == 1 && this.getMaxLevel() == 1 ? s : s + " " + I18n.translateToLocal("enchantment.level." + level);
   }

   public String getPrefix() {
      return "";
   }

   public void onEntityDamagedAlt(EntityLivingBase user, Entity target, ItemStack weapon, int level) {
   }

   /** @deprecated */
   @Deprecated
   public final void onEntityDamaged(EntityLivingBase user, Entity target, int level) {
      super.onEntityDamaged(user, target, level);
   }

   public static boolean isOffensivePetDisallowed(Entity immediateSource, Entity trueSource) {
      if (immediateSource != null && trueSource != null) {
         return !ModConfig.miscellaneous.enablePetAttacks && immediateSource != trueSource && immediateSource instanceof EntityLivingBase && trueSource instanceof EntityPlayer && !(immediateSource instanceof EntityPlayer);
      } else {
         return false;
      }
   }
}

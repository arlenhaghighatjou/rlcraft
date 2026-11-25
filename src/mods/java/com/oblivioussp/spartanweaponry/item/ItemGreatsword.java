package com.oblivioussp.spartanweaponry.item;

import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.api.ToolMaterialEx;
import com.oblivioussp.spartanweaponry.api.WeaponProperties;
import com.oblivioussp.spartanweaponry.util.ConfigHandler;
import com.oblivioussp.spartanweaponry.util.WeaponHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;

public class ItemGreatsword extends ItemSwordBase {
   public ItemGreatsword(String unlocName, ToolMaterialEx material) {
      super(unlocName, material, ConfigHandler.damageBaseGreatsword, ConfigHandler.damageMultiplierGreatsword, (double)ConfigHandler.speedGreatsword, WeaponProperties.TWO_HANDED_2, WeaponProperties.REACH_1, WeaponProperties.SWEEP_DAMAGE_FULL);
      this.displayName = "greatsword_custom";
   }

   public ItemGreatsword(String unlocName, String externalModId, ToolMaterialEx material) {
      this(unlocName, material);
      this.modId = externalModId;
   }

   /** @deprecated */
   @Deprecated
   public ItemGreatsword(String unlocName, String externalModId, ToolMaterialEx material, float weaponDamage) {
      this(unlocName, externalModId, material);
   }

   public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity targetEntity) {
      return ModSpartanWeaponry.isRLCombatLoaded ? false : WeaponHelper.inflictAttackDamage(this, stack, player, targetEntity, WeaponProperties.REACH_1.getMagnitude());
   }

   public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
      return enchantment != Enchantments.SWEEPING && super.canApplyAtEnchantingTable(stack, enchantment);
   }
}

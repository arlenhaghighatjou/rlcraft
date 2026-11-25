package com.oblivioussp.spartanweaponry.item;

import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.api.ToolMaterialEx;
import com.oblivioussp.spartanweaponry.api.weaponproperty.WeaponProperty;
import com.oblivioussp.spartanweaponry.util.WeaponHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemWeaponBase extends ItemSwordBase {
   protected float weaponReach;

   public ItemWeaponBase(String unlocName, ToolMaterialEx material, float weaponDamageBase, float weaponDamageMultiplier, double weaponSpeed, WeaponProperty... weaponProperties) {
      super(unlocName, material, weaponDamageBase, weaponDamageMultiplier, weaponSpeed, weaponProperties);
      WeaponProperty reachProp = this.getFirstWeaponPropertyWithType("reach");
      if (reachProp != null) {
         this.weaponReach = reachProp.getMagnitude();
      } else {
         this.weaponReach = 3.0F;
      }

   }

   public float getDestroySpeed(ItemStack stack, IBlockState state) {
      for(String type : this.getToolClasses(stack)) {
         if (state.getBlock().isToolEffective(type, state)) {
            return this.materialEx.getEfficiency();
         }
      }

      return 1.0F;
   }

   public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity targetEntity) {
      return ModSpartanWeaponry.isRLCombatLoaded ? false : WeaponHelper.inflictAttackDamage(this, stack, player, targetEntity, this.weaponReach);
   }
}

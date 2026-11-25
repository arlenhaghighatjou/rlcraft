package com.oblivioussp.spartanweaponry.api.weaponproperty;

import com.oblivioussp.spartanweaponry.api.ToolMaterialEx;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class WeaponPropertyWithCallback extends WeaponProperty implements IPropertyCallback {
   public WeaponPropertyWithCallback(String propType, String propModId, int propLevel, float propMagnitude) {
      super(propType, propModId, propLevel, propMagnitude);
   }

   public WeaponPropertyWithCallback(String propType, String propModId, int propLevel) {
      super(propType, propModId, propLevel);
   }

   public WeaponPropertyWithCallback(String propType, String propModId, float propMagnitude) {
      super(propType, propModId, propMagnitude);
   }

   public WeaponPropertyWithCallback(String propType, String propModId) {
      super(propType, propModId);
   }

   public IPropertyCallback getCallback() {
      return this;
   }

   public void onItemUpdate(ToolMaterialEx material, ItemStack stack, World world, EntityLivingBase entity, int itemSlot, boolean isSelected) {
   }

   public void onHitEntity(ToolMaterialEx material, ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, Entity projectile) {
   }

   public void onCreateItem(ToolMaterialEx material, ItemStack stack) {
   }
}

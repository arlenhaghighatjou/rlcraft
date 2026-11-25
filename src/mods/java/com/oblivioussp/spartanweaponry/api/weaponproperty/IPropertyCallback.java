package com.oblivioussp.spartanweaponry.api.weaponproperty;

import com.oblivioussp.spartanweaponry.api.ToolMaterialEx;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public interface IPropertyCallback {
   default float modifyDamageDealt(ToolMaterialEx material, float baseDamage, DamageSource source, EntityLivingBase attacker, EntityLivingBase victim) {
      return baseDamage;
   }

   default float modifyDamageDealt(ToolMaterialEx material, float baseDamage, float initialDamage, DamageSource source, EntityLivingBase attacker, EntityLivingBase victim) {
      return this.modifyDamageDealt(material, baseDamage, source, attacker, victim);
   }

   default float modifyDamageTaken(ToolMaterialEx material, float baseDamage, DamageSource source, EntityLivingBase attacker, EntityLivingBase victim) {
      return baseDamage;
   }

   default void onItemUpdate(ToolMaterialEx material, ItemStack stack, World world, EntityLivingBase entity, int itemSlot, boolean isSelected) {
   }

   default void onHitEntity(ToolMaterialEx material, ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, Entity projectile) {
   }

   default void onCreateItem(ToolMaterialEx material, ItemStack stack) {
   }
}

package com.oblivioussp.spartanweaponry.item;

import com.oblivioussp.spartanweaponry.api.ToolMaterialEx;
import com.oblivioussp.spartanweaponry.api.WeaponProperties;
import com.oblivioussp.spartanweaponry.util.ConfigHandler;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemDagger extends ItemWeaponBase {
   public ItemDagger(String unlocName, ToolMaterialEx material) {
      super(unlocName, material, ConfigHandler.damageBaseDagger, ConfigHandler.damageMultiplierDagger, (double)ConfigHandler.speedDagger, WeaponProperties.THROWABLE, WeaponProperties.EXTRA_DAMAGE_BACKSTAB);
      this.displayName = "dagger_custom";
   }

   public ItemDagger(String unlocName, String externalModId, ToolMaterialEx material) {
      this(unlocName, material);
      this.modId = externalModId;
   }

   /** @deprecated */
   @Deprecated
   public ItemDagger(String unlocName, String externalModId, ToolMaterialEx material, float weaponDamage) {
      this(unlocName, externalModId, material);
   }

   public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
      super.onUpdate(stack, world, entity, itemSlot, isSelected);
   }
}

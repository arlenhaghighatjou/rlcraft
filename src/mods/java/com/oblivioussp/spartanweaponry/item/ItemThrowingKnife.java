package com.oblivioussp.spartanweaponry.item;

import com.oblivioussp.spartanweaponry.api.ToolMaterialEx;
import com.oblivioussp.spartanweaponry.api.WeaponProperties;
import com.oblivioussp.spartanweaponry.entity.projectile.EntityThrowingKnife;
import com.oblivioussp.spartanweaponry.entity.projectile.EntityThrownWeapon;
import com.oblivioussp.spartanweaponry.util.ConfigHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class ItemThrowingKnife extends ItemThrowingWeapon {
   public ItemThrowingKnife(String unlocName, ToolMaterialEx material) {
      super(unlocName, material, ConfigHandler.damageBaseThrowingKnife, ConfigHandler.damageMultiplierThrowingKnife, ConfigHandler.meleeSpeedThrowingKnife, 16, WeaponProperties.THROWABLE, WeaponProperties.EXTRA_DAMAGE_2_THROWN);
      this.throwDamageMultiplier = 2.0F;
      this.throwVelocity = 2.1F;
      this.displayName = "throwing_knife_custom";
      this.maxChargeTicks = ConfigHandler.chargeTicksThrowingKnife;
   }

   public ItemThrowingKnife(String unlocName, String externalModId, ToolMaterialEx material) {
      this(unlocName, material);
      this.modId = externalModId;
   }

   public EntityThrownWeapon createThrownWeaponEntity(World world, EntityPlayer player) {
      return new EntityThrowingKnife(world, player);
   }

   /** @deprecated */
   @Deprecated
   public ItemThrowingKnife(String unlocName, String externalModId, ToolMaterialEx material, float weaponDamage) {
      this(unlocName, externalModId, material);
   }
}

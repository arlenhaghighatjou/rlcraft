package com.oblivioussp.spartanweaponry.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.oblivioussp.spartanweaponry.api.ToolMaterialEx;
import com.oblivioussp.spartanweaponry.api.WeaponProperties;
import com.oblivioussp.spartanweaponry.entity.projectile.EntityBoomerang;
import com.oblivioussp.spartanweaponry.entity.projectile.EntityThrownWeapon;
import com.oblivioussp.spartanweaponry.init.EnchantmentRegistrySW;
import com.oblivioussp.spartanweaponry.init.SoundRegistry;
import com.oblivioussp.spartanweaponry.util.ConfigHandler;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow.PickupStatus;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class ItemBoomerang extends ItemThrowingWeapon {
   public ItemBoomerang(String unlocName, ToolMaterialEx toolMaterial) {
      super(unlocName, toolMaterial, ConfigHandler.damageBaseBoomerang, ConfigHandler.damageMultiplierBoomerang, ConfigHandler.meleeSpeedBoomerang, 1, WeaponProperties.THROWABLE);
      this.displayName = "boomerang_custom";
      this.setMaxDamage(this.materialEx.getMaxUses());
      this.maxChargeTicks = ConfigHandler.chargeTicksBoomerang;
   }

   public ItemBoomerang(String unlocName, String externalModId, ToolMaterialEx material) {
      this(unlocName, material);
      this.modId = externalModId;
   }

   /** @deprecated */
   @Deprecated
   public ItemBoomerang(String unlocName, String externalModId, ToolMaterialEx material, float weaponDamage) {
      this(unlocName, externalModId, material);
   }

   public EntityThrownWeapon createThrownWeaponEntity(World world, EntityPlayer player) {
      return new EntityBoomerang(world, player);
   }

   public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
      if (entityLiving instanceof EntityPlayer) {
         int maxAmmo = this.getMaxAmmo(stack);
         int ammoCount = maxAmmo - stack.getTagCompound().getInteger("AmmoUsed");
         EntityPlayer player = (EntityPlayer)entityLiving;
         this.throwVelocity = 2.0F;
         int charge = this.getMaxItemUseDuration(stack) - timeLeft;
         if (charge >= 5) {
            charge = 5;
         }

         if (!worldIn.isRemote && charge > 2) {
            EntityBoomerang thrown = (EntityBoomerang)this.createThrownWeaponEntity(worldIn, player);
            if (thrown == null) {
               return;
            }

            thrown.setWeapon(stack);
            int i = EnchantmentHelper.getEnchantmentLevel(EnchantmentRegistrySW.THROWING_RANGE, stack);
            thrown.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, this.throwVelocity * ((float)i * 0.2F + 1.0F) * ((float)charge / 10.0F + 0.5F), 0.5F);
            thrown.setPosition(player.posX, player.posY + (double)player.getEyeHeight() * 0.9D - 0.10000000149011612D, player.posZ);
            thrown.setDamage((double)this.getDirectAttackDamage() + 1.0D);
            thrown.setMaxDistance((double)charge / 5.0D * 2.0D + 3.0D + (double)i * 3.0D);
            int j = EnchantmentHelper.getEnchantmentLevel(EnchantmentRegistrySW.THROWING_DAMAGE, stack);
            if (j > 0) {
               thrown.setDamage(thrown.getDamage() + (double)j * 0.5D + 0.5D);
            }

            int k = EnchantmentHelper.getEnchantmentLevel(Enchantments.KNOCKBACK, stack);
            if (k > 0) {
               thrown.setKnockbackStrength(k);
            }

            if (EnchantmentHelper.getEnchantmentLevel(EnchantmentRegistrySW.THROWING_FIRE, stack) > 0) {
               thrown.setFire(100);
            }

            if (player.capabilities.isCreativeMode) {
               thrown.pickupStatus = PickupStatus.CREATIVE_ONLY;
            } else if (thrown.isValidThrowingWeapon()) {
               --ammoCount;
               stack.getTagCompound().setInteger("AmmoUsed", maxAmmo - ammoCount);
               if (ammoCount == 0 && !stack.getTagCompound().getBoolean("Original")) {
                  stack.shrink(1);
                  if (stack.getCount() <= 0) {
                     player.inventory.deleteStack(stack);
                  }
               }
            }

            if (thrown.isValidThrowingWeapon()) {
               stack.setItemDamage(0);
               worldIn.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundRegistry.BOOMERANG_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
               worldIn.spawnEntity(thrown);
            }
         }

         player.addStat(StatList.getObjectUseStats(this));
      }

   }

   public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
      Multimap<String, AttributeModifier> multimap = HashMultimap.create();
      return multimap;
   }
}

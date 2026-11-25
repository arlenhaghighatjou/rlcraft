package com.lycanitesmobs.core.dispenser;

import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class DispenserBehaviorBase extends BehaviorProjectileDispense {
   ProjectileInfo projectileInfo;

   public DispenserBehaviorBase(ProjectileInfo projectileInfo) {
      this.projectileInfo = projectileInfo;
   }

   public DispenserBehaviorBase() {
   }

   public ItemStack dispenseStack(IBlockSource blockSource, ItemStack stack) {
      World world = blockSource.getWorld();
      IPosition position = BlockDispenser.getDispensePosition(blockSource);
      EnumFacing facing = (EnumFacing)blockSource.getBlockState().getValue(BlockDispenser.FACING);
      IProjectile iprojectile = this.getProjectileEntity(world, position, stack);
      if (iprojectile == null) {
         return stack;
      } else {
         iprojectile.shoot((double)facing.getFrontOffsetX(), (double)facing.getFrontOffsetY(), (double)facing.getFrontOffsetZ(), this.getProjectileVelocity(), this.getProjectileInaccuracy());
         world.spawnEntity((Entity)iprojectile);
         stack.splitStack(1);
         return stack;
      }
   }

   protected IProjectile getProjectileEntity(World world, IPosition pos, ItemStack stack) {
      return this.projectileInfo == null ? null : this.projectileInfo.createProjectile(world, pos.getX(), pos.getY(), pos.getZ());
   }

   protected float getProjectileInaccuracy() {
      return 0.0F;
   }

   protected float getProjectileVelocity() {
      return this.projectileInfo != null ? (float)this.projectileInfo.velocity : 1.1F;
   }

   protected void playDispenseSound(IBlockSource blockSource) {
      SoundEvent soundEvent = this.getDispenseSound();
      if (soundEvent != null && blockSource != null) {
         blockSource.getWorld().playSound((EntityPlayer)null, blockSource.getBlockPos(), soundEvent, SoundCategory.AMBIENT, 1.0F, 1.0F / ((new EaglercraftRandom()).nextFloat() * 0.4F + 0.8F));
      }
   }

   protected SoundEvent getDispenseSound() {
      return this.projectileInfo == null ? null : this.projectileInfo.getLaunchSound();
   }
}

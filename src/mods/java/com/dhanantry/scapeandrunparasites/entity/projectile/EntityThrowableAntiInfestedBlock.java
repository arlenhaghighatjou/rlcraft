package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityThrowableAntiInfestedBlock extends EntityThrowable {
   public EntityThrowableAntiInfestedBlock(World worldIn) {
      super(worldIn);
   }

   public EntityThrowableAntiInfestedBlock(World worldIn, EntityLivingBase throwerIn) {
      super(worldIn, throwerIn);
   }

   public EntityThrowableAntiInfestedBlock(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
   }

   protected void onImpact(RayTraceResult result) {
      if (result.entityHit != null) {
         result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), 0.0F);
      }

      if (!this.world.isRemote) {
         int i1 = MathHelper.floor(this.posY + 0.1D);
         double l1 = this.posX;
         double i2 = this.posZ;
         boolean flag = false;
         int offsetT = 5;
         int range = 7;

         for(int k2 = -1 * range; k2 <= 1 * range; ++k2) {
            for(int l2 = -1 * range; l2 <= 1 * range; ++l2) {
               for(int j = -1 * offsetT; j <= 1 * offsetT; ++j) {
                  double i3 = l1 + (double)k2;
                  double k = (double)(i1 + j);
                  double l = i2 + (double)l2;
                  BlockPos blockpos = new BlockPos(i3, k, l);
                  IBlockState iblockstate = this.world.getBlockState(blockpos);
                  Block block = iblockstate.getBlock();
                  if (block == SRPBlocks.InfestedStain) {
                     this.world.setBlockState(blockpos, SRPBlocks.optionalDirt.getDefaultState());
                  }

                  if (block == SRPBlocks.InfestedRubble) {
                     this.world.setBlockState(blockpos, SRPBlocks.optionalRub.getDefaultState());
                  }
               }
            }
         }

         this.setDead();
      } else {
         double d0 = this.rand.nextGaussian() * 0.02D;
         double d1 = this.rand.nextGaussian() * 0.02D;
         double d2 = this.rand.nextGaussian() * 0.02D;
         this.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2, new int[0]);
      }

   }
}

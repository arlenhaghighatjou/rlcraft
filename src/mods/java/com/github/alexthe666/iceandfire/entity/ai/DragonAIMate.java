package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.core.ModBlocks;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.entity.EntityDragonEgg;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DragonAIMate extends EntityAIBase {
   private final EntityDragonBase dragon;
   World theWorld;
   int spawnBabyDelay;
   double moveSpeed;
   private EntityDragonBase targetMate;
   private static final IBlockState NEST = ModBlocks.nest.getDefaultState();

   public DragonAIMate(EntityDragonBase dragon, double speedIn) {
      this.dragon = dragon;
      this.theWorld = dragon.world;
      this.moveSpeed = speedIn;
      this.setMutexBits(1);
   }

   public boolean shouldExecute() {
      if (!this.dragon.isInLove()) {
         return false;
      } else {
         this.targetMate = this.getNearbyMate();
         return this.targetMate != null;
      }
   }

   public boolean continueExecuting() {
      return this.targetMate.isEntityAlive() && this.targetMate.isInLove() && this.spawnBabyDelay < 60;
   }

   public void resetTask() {
      this.targetMate = null;
      this.spawnBabyDelay = 0;
   }

   public void updateTask() {
      this.dragon.getLookHelper().setLookPositionWithEntity(this.targetMate, 10.0F, (float)this.dragon.getVerticalFaceSpeed());
      this.dragon.getNavigator().tryMoveToXYZ(this.targetMate.posX, this.targetMate.posY, this.targetMate.posZ, this.moveSpeed);
      this.dragon.setFlying(false);
      this.dragon.setHovering(false);
      ++this.spawnBabyDelay;
      if (this.spawnBabyDelay >= 60 && this.dragon.getDistance(this.targetMate) < 35.0F) {
         this.spawnBaby();
      }

   }

   private EntityDragonBase getNearbyMate() {
      List<EntityDragonBase> list = this.theWorld.getEntitiesWithinAABB(this.dragon.getClass(), this.dragon.getEntityBoundingBox().grow(180.0D, 180.0D, 180.0D));
      double d0 = Double.MAX_VALUE;
      EntityDragonBase mate = null;

      for(EntityDragonBase partner : list) {
         if (this.dragon.canMateWith(partner) && this.dragon.getDistanceSq(partner) < d0) {
            mate = partner;
            break;
         }
      }

      return mate;
   }

   private void spawnBaby() {
      EntityDragonEgg egg = this.dragon.createEgg(this.targetMate);
      if (egg != null) {
         EntityPlayer entityplayer = this.dragon.getLoveCause();
         if (entityplayer == null && this.targetMate.getLoveCause() != null) {
            entityplayer = this.targetMate.getLoveCause();
         }

         if (entityplayer != null) {
            entityplayer.addStat(StatList.ANIMALS_BRED);
         }

         this.dragon.setGrowingAge(6000);
         this.targetMate.setGrowingAge(6000);
         this.dragon.resetInLove();
         this.targetMate.resetInLove();
         int nestX = (int)(this.dragon.isMale() ? this.targetMate.posX : this.dragon.posX);
         int nestY = (int)(this.dragon.isMale() ? this.targetMate.posY : this.dragon.posY) - 1;
         int nestZ = (int)(this.dragon.isMale() ? this.targetMate.posZ : this.dragon.posZ);
         egg.setLocationAndAngles((double)((float)nestX - 0.5F), (double)((float)nestY + 1.0F), (double)((float)nestZ - 0.5F), 0.0F, 0.0F);
         this.theWorld.spawnEntity(egg);
         Random random = this.dragon.getRNG();

         for(int i = 0; i < 17; ++i) {
            double d0 = random.nextGaussian() * 0.02D;
            double d1 = random.nextGaussian() * 0.02D;
            double d2 = random.nextGaussian() * 0.02D;
            double d3 = random.nextDouble() * (double)this.dragon.width * 2.0D - (double)this.dragon.width;
            double d4 = 0.5D + random.nextDouble() * (double)this.dragon.height;
            double d5 = random.nextDouble() * (double)this.dragon.width * 2.0D - (double)this.dragon.width;
            this.theWorld.spawnParticle(EnumParticleTypes.HEART, this.dragon.posX + d3, this.dragon.posY + d4, this.dragon.posZ + d5, d0, d1, d2, new int[0]);
         }

         BlockPos eggPos = new BlockPos(nestX - 2, nestY, nestZ - 2);
         BlockPos dirtPos = eggPos.add(1, 0, 1);

         for(int x = 0; x < 3; ++x) {
            for(int z = 0; z < 3; ++z) {
               BlockPos add = eggPos.add(x, 0, z);
               if (this.theWorld.getBlockState(add).getBlock().isReplaceable(this.theWorld, add) || this.theWorld.getBlockState(add).getMaterial() == Material.GROUND || this.theWorld.getBlockState(add).getBlockHardness(this.theWorld, add) < 5.0F || this.theWorld.getBlockState(add).getBlockHardness(this.theWorld, add) >= 0.0F) {
                  this.theWorld.setBlockState(add, NEST);
               }
            }
         }

         if (this.theWorld.getBlockState(dirtPos).getBlock().isReplaceable(this.theWorld, dirtPos) || this.theWorld.getBlockState(dirtPos) == NEST) {
            this.theWorld.setBlockState(dirtPos, Blocks.GRASS_PATH.getDefaultState());
         }

         if (this.theWorld.getGameRules().getBoolean("doMobLoot")) {
            this.theWorld.spawnEntity(new EntityXPOrb(this.theWorld, this.dragon.posX, this.dragon.posY, this.dragon.posZ, random.nextInt(15) + 10));
         }
      }

   }
}

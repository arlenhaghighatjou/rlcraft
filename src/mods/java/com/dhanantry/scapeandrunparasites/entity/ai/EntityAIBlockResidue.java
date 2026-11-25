package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityAIBlockResidue extends EntityAIBase {
   private final EntityParasiteBase parent;
   private int ticks = 0;
   private int range;
   private int blockBreakCounter;

   public EntityAIBlockResidue(EntityParasiteBase ghast, int range) {
      this.parent = ghast;
      this.range = range;
      this.blockBreakCounter = 200;
   }

   public boolean shouldExecute() {
      return this.parent.getAttackTarget() == null && !this.parent.isInWater();
   }

   public void resetTask() {
      this.parent.setParasiteStatus(0);
      this.blockBreakCounter = 200;
   }

   public void updateTask() {
      if (this.blockBreakCounter > 0) {
         if (this.parent.world.rand.nextInt(6) == 0) {
            --this.blockBreakCounter;
         }
      } else {
         --this.blockBreakCounter;
         if (this.blockBreakCounter == -1) {
            this.parent.setParasiteStatus(25);
            this.parent.getNavigator().clearPath();
            this.parent.playSound(SRPSounds.ADAPTED_V, 2.0F, 1.0F);
         }

         if (this.blockBreakCounter == -40) {
            this.parent.playSound(SRPSounds.ADAPTED_V, 2.0F, 1.0F);
         }

         this.parent.particleStatus((byte)13);
         if (this.blockBreakCounter == -60) {
            double i1 = (double)MathHelper.floor(this.parent.posY + 0.1D);
            double l1 = this.parent.posX;
            double i2 = this.parent.posZ;
            World world = this.parent.world;
            if (this.blockBreakCounter == -63) {
               ;
            }

            for(int k2 = -1 * this.range; k2 <= 1 * this.range; ++k2) {
               for(int l2 = -1 * this.range; l2 <= 1 * this.range; ++l2) {
                  double i3 = l1 + (double)k2;
                  double l = i2 + (double)l2;
                  BlockPos blockpos = new BlockPos(i3, i1, l);
                  Block block = this.parent.world.getBlockState(blockpos).getBlock();
                  Block blockDown = this.parent.world.getBlockState(blockpos.down()).getBlock();
                  if (block == Blocks.AIR && blockDown != Blocks.AIR && world.getBlockState(blockpos.down()).isFullBlock() && blockDown != SRPBlocks.InfestedStain && this.parent.world.rand.nextInt(2) == 0) {
                     this.parent.world.setBlockState(blockpos, SRPBlocks.InfestRemain.getStateFromMeta(1));
                  }
               }
            }
         }

         if (this.blockBreakCounter == -100) {
            this.parent.setParasiteStatus(0);
            this.blockBreakCounter = 200;
         }
      }

   }
}

package svenhjol.charm.crafting.ai;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import svenhjol.charm.crafting.feature.EnderPearlBlock;

public class AIFormEndermite extends EntityAIWander {
   private final EntitySilverfish silverfish;
   private boolean doMerge;
   private EnumFacing facing;

   public AIFormEndermite(EntitySilverfish silverfish) {
      super(silverfish, 1.0D, 20);
      this.silverfish = silverfish;
      this.setMutexBits(1);
   }

   public boolean shouldExecute() {
      if (!this.silverfish.getEntityWorld().getGameRules().getBoolean("mobGriefing")) {
         return false;
      } else if (this.silverfish.getAttackTarget() != null) {
         return false;
      } else if (!this.silverfish.getNavigator().noPath()) {
         return false;
      } else {
         Random random = this.silverfish.getRNG();
         if ((double)random.nextFloat() < EnderPearlBlock.endermiteChance) {
            this.facing = EnumFacing.random(random);
            BlockPos blockpos = (new BlockPos(this.silverfish.posX, this.silverfish.posY + 0.5D, this.silverfish.posZ)).offset(this.facing);
            IBlockState iblockstate = this.silverfish.getEntityWorld().getBlockState(blockpos);
            if (iblockstate.getBlock() == EnderPearlBlock.block) {
               this.doMerge = true;
               return true;
            }
         }

         this.doMerge = false;
         return super.shouldExecute();
      }
   }

   public boolean shouldContinueExecuting() {
      return !this.doMerge && super.shouldContinueExecuting();
   }

   public void startExecuting() {
      if (!this.doMerge) {
         super.startExecuting();
      } else {
         World world = this.silverfish.getEntityWorld();
         BlockPos blockpos = (new BlockPos(this.silverfish.posX, this.silverfish.posY + 0.5D, this.silverfish.posZ)).offset(this.facing);
         IBlockState iblockstate = world.getBlockState(blockpos);
         if (iblockstate.getBlock() == EnderPearlBlock.block) {
            world.setBlockToAir(blockpos);
            this.silverfish.spawnExplosionParticle();
            this.silverfish.setDead();
            EntityEndermite endermite = new EntityEndermite(world);
            endermite.setPosition((double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.5D, (double)blockpos.getZ() + 0.5D);
            world.spawnEntity(endermite);
         }
      }

   }
}

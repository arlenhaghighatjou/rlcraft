package vazkii.quark.misc.ai;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.quark.misc.feature.EndermitesIntoShulkers;

public class EntityAIFormShulker extends EntityAIWander {
   private final EntityEndermite endermite;
   private EnumFacing facing;
   private boolean doMerge;

   public EntityAIFormShulker(EntityEndermite endermite) {
      super(endermite, 1.0D, 10);
      this.endermite = endermite;
      this.setMutexBits(1);
   }

   public boolean shouldExecute() {
      if (!this.endermite.getEntityWorld().getGameRules().getBoolean("mobGriefing")) {
         return false;
      } else if (this.endermite.getAttackTarget() != null) {
         return false;
      } else if (!this.endermite.getNavigator().noPath()) {
         return false;
      } else {
         EaglercraftRandom random = this.endermite.getRNG();
         if (random.nextDouble() < EndermitesIntoShulkers.chance) {
            this.facing = EnumFacing.random(random);
            BlockPos blockpos = (new BlockPos(this.endermite.posX, this.endermite.posY + 0.5D, this.endermite.posZ)).offset(this.facing);
            IBlockState iblockstate = this.endermite.getEntityWorld().getBlockState(blockpos);
            if (iblockstate.getBlock() == Blocks.PURPUR_BLOCK) {
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
         World world = this.endermite.getEntityWorld();
         BlockPos blockpos = (new BlockPos(this.endermite.posX, this.endermite.posY + 0.5D, this.endermite.posZ)).offset(this.facing);
         IBlockState iblockstate = world.getBlockState(blockpos);
         if (iblockstate.getBlock() == Blocks.PURPUR_BLOCK) {
            world.setBlockToAir(blockpos);
            this.endermite.spawnExplosionParticle();
            this.endermite.setDead();
            EntityShulker shulker = new EntityShulker(world);
            shulker.setAttachmentPos(blockpos);
            shulker.setPosition((double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.5D, (double)blockpos.getZ() + 0.5D);
            world.spawnEntity(shulker);
         }
      }

   }
}

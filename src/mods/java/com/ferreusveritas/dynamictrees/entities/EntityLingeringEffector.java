package com.ferreusveritas.dynamictrees.entities;

import com.ferreusveritas.dynamictrees.api.substances.ISubstanceEffect;
import com.ferreusveritas.dynamictrees.blocks.BlockRooty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityLingeringEffector extends Entity {
   public BlockPos blockPos;
   public ISubstanceEffect effect;
   public boolean extended;
   private byte invalidTicks = 0;

   public EntityLingeringEffector(World world, BlockPos pos, ISubstanceEffect effect) {
      super(world);
      this.width = 1.0F;
      this.height = 1.0F;
      this.noClip = true;
      this.setBlockPos(pos);
      this.setEffect(effect);
      if (this.effect != null) {
         for(EntityLingeringEffector effector : world.getEntitiesWithinAABB(EntityLingeringEffector.class, new AxisAlignedBB(pos))) {
            if (effector.getBlockPos().equals(pos) && effector.getEffect().getName().equals(effect.getName())) {
               effector.setDead();
            }
         }
      }

   }

   public static boolean treeHasEffectorForEffect(World world, BlockPos pos, ISubstanceEffect effect) {
      for(EntityLingeringEffector effector : world.getEntitiesWithinAABB(EntityLingeringEffector.class, new AxisAlignedBB(pos))) {
         if (effector.getEffect() != null && effector.getEffect().getName().equals(effect.getName())) {
            return true;
         }
      }

      return false;
   }

   public void setBlockPos(BlockPos pos) {
      this.blockPos = pos;
      this.setPosition((double)this.blockPos.getX() + 0.5D, (double)this.blockPos.getY(), (double)this.blockPos.getZ() + 0.5D);
   }

   public BlockPos getBlockPos() {
      return this.blockPos;
   }

   public void setEffect(ISubstanceEffect effect) {
      this.effect = effect;
   }

   public ISubstanceEffect getEffect() {
      return this.effect;
   }

   protected void entityInit() {
   }

   protected void readEntityFromNBT(NBTTagCompound compound) {
   }

   protected void writeEntityToNBT(NBTTagCompound compound) {
   }

   public void onUpdate() {
      super.onUpdate();
      if (this.effect == null) {
         if (++this.invalidTicks > 20) {
            this.setDead();
         }

      } else {
         if (this.effect != null) {
            IBlockState state = this.world.getBlockState(this.blockPos);
            if (state.getBlock() instanceof BlockRooty) {
               if (!this.effect.update(this.world, this.blockPos, this.ticksExisted, ((Integer)state.getValue(BlockRooty.LIFE)).intValue())) {
                  this.setDead();
               }
            } else {
               this.setDead();
            }
         }

      }
   }

   public boolean shouldRenderInPass(int pass) {
      return false;
   }
}

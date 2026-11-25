package com.endertech.minecraft.forge.entities;

import com.endertech.minecraft.forge.blocks.ForgeBlock;
import com.endertech.minecraft.forge.math.Vect3d;
import com.google.common.base.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockStateCarrier extends ForgeEntity {
   private static final DataParameter<Optional<IBlockState>> CARRIED_BLOCK_STATE = EntityDataManager.createKey(BlockStateCarrier.class, DataSerializers.OPTIONAL_BLOCK_STATE);

   public BlockStateCarrier(World world, BlockPos pos, IBlockState carriedState) {
      super(world);
      Vect3d centeredPos = getCenteredPosTo(pos);
      this.setAllPositions(centeredPos);
      this.setCarriedBlockState(carriedState);
   }

   public BlockStateCarrier(World world) {
      super(world);
   }

   protected void entityInit() {
      super.entityInit();
      this.getDataManager().register(CARRIED_BLOCK_STATE, Optional.absent());
      this.setSize(0.98F, 0.98F);
   }

   @Nullable
   public IBlockState getCarriedBlockState() {
      return (IBlockState)((Optional)this.getDataManager().get(CARRIED_BLOCK_STATE)).orNull();
   }

   protected void setCarriedBlockState(@Nullable IBlockState newState) {
      this.getDataManager().set(CARRIED_BLOCK_STATE, Optional.fromNullable(newState));
   }

   @Nullable
   public ResourceLocation getTextureLocation() {
      IBlockState state = this.getCarriedBlockState();
      if (state != null && state.getBlock() instanceof ForgeBlock) {
         ForgeBlock block = (ForgeBlock)state.getBlock();
         ResourceLocation location = block.getTextureLocationFor(state);
         return location;
      } else {
         return null;
      }
   }

   public void setDead() {
      super.setDead();
   }

   protected void writeEntityToNBT(NBTTagCompound tag) {
      super.writeEntityToNBT(tag);
      IBlockState state = this.getCarriedBlockState();
      if (state != null) {
         tag.setInteger(BlockStateCarrier.Tags.CARRIED_STATE.name(), Block.getStateId(state));
      }

   }

   protected void readEntityFromNBT(NBTTagCompound tag) {
      super.readEntityFromNBT(tag);
      if (tag.hasKey(BlockStateCarrier.Tags.CARRIED_STATE.name())) {
         int id = tag.getInteger(BlockStateCarrier.Tags.CARRIED_STATE.name());
         IBlockState state = Block.getStateById(id);
         this.getDataManager().set(CARRIED_BLOCK_STATE, Optional.of(state));
      }

   }

   private static enum Tags {
      CARRIED_STATE;

      private Tags() {
      }
   }
}

package com.teamacronymcoders.contenttweaker.api.ctobjects.blockstate;

import com.teamacronymcoders.contenttweaker.api.ctobjects.blockpos.IBlockPos;
import com.teamacronymcoders.contenttweaker.api.ctobjects.enums.Facing;
import com.teamacronymcoders.contenttweaker.api.ctobjects.enums.PushReaction;
import com.teamacronymcoders.contenttweaker.api.ctobjects.world.IWorld;
import crafttweaker.api.block.IBlock;
import crafttweaker.mc1120.block.MCSpecificBlock;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;

public class MCBlockState implements ICTBlockState {
   private IBlockState blockState;
   private IBlock block;

   public MCBlockState(IBlock block) {
      this.block = block;
      Block actualBlock = (Block)ForgeRegistries.BLOCKS.getValue(new ResourceLocation(block.getDefinition().getId()));
      if (actualBlock != null) {
         this.blockState = actualBlock.getStateFromMeta(block.getMeta());
      }

   }

   public MCBlockState(IBlockState blockState) {
      this.blockState = blockState;
      Block block = blockState.getBlock();
      int meta = block.getMetaFromState(blockState);
      this.block = new MCSpecificBlock(block, meta);
   }

   public IBlock getBlock() {
      return this.block;
   }

   public int getMeta() {
      return this.getBlock().getMeta();
   }

   public boolean isReplaceable(IWorld world, IBlockPos blockPos) {
      return this.blockState.getBlock().isReplaceable((IBlockAccess)world.getInternal(), (BlockPos)blockPos.getInternal());
   }

   public int getLightValue(IWorld world, IBlockPos blockPos) {
      return this.blockState.getLightValue((IBlockAccess)world.getInternal(), (BlockPos)blockPos.getInternal());
   }

   public boolean canProvidePower() {
      return this.blockState.canProvidePower();
   }

   public int getWeakPower(IWorld world, IBlockPos blockPos, Facing facing) {
      return this.blockState.getWeakPower((IBlockAccess)world.getInternal(), (BlockPos)blockPos.getInternal(), facing.getInternal());
   }

   public int getComparatorInputOverride(IWorld world, IBlockPos blockPos) {
      return this.blockState.getComparatorInputOverride((World)world.getInternal(), (BlockPos)blockPos.getInternal());
   }

   public PushReaction getMobilityFlag() {
      return PushReaction.of(this.blockState.getMobilityFlag());
   }

   public int compare(ICTBlockState other) {
      int result = 0;
      if (!this.getInternal().equals(other.getInternal())) {
         if (this.getInternal().getBlock().equals(((IBlockState)other.getInternal()).getBlock())) {
            result = Integer.compare(this.getMeta(), other.getMeta());
         } else {
            int blockId = ((ForgeRegistry)ForgeRegistries.BLOCKS).getID(this.getInternal().getBlock());
            int otherBlockId = ((ForgeRegistry)ForgeRegistries.BLOCKS).getID(((IBlockState)other.getInternal()).getBlock());
            result = Integer.compare(blockId, otherBlockId);
         }
      }

      return result;
   }

   public IBlockState getInternal() {
      return this.blockState;
   }
}

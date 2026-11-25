package com.teamacronymcoders.base.recipesystem.output;

import com.google.gson.annotations.JsonAdapter;
import com.teamacronymcoders.base.json.deserializer.BlockPosDeserializer;
import com.teamacronymcoders.base.json.deserializer.BlockStateDeserializer;
import com.teamacronymcoders.base.json.deserializer.NBTDeserializer;
import com.teamacronymcoders.base.recipesystem.RecipeContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class BlockStateOutput implements IOutput {
   @JsonAdapter(BlockStateDeserializer.class)
   private final IBlockState blockState;
   @JsonAdapter(BlockPosDeserializer.class)
   private final BlockPos offset;
   @JsonAdapter(NBTDeserializer.class)
   private final NBTTagCompound tileEntityNBT;

   public BlockStateOutput(IBlockState blockState, BlockPos offset, NBTTagCompound tileEntityNBT) {
      this.blockState = blockState;
      this.offset = offset;
      this.tileEntityNBT = tileEntityNBT;
   }

   public boolean canOutput(RecipeContainer recipeContainer) {
      return true;
   }

   public void output(RecipeContainer recipeContainer) {
      BlockPos pos = this.offset != null ? recipeContainer.getPos().add(this.offset) : recipeContainer.getPos();
      recipeContainer.getWorld().setBlockState(pos, this.blockState, 3);
      if (this.tileEntityNBT != null && !this.tileEntityNBT.hasNoTags()) {
         TileEntity tileEntity = recipeContainer.getWorld().getTileEntity(pos);
         if (tileEntity != null) {
            NBTTagCompound currentNBT = tileEntity.writeToNBT(new NBTTagCompound());
            NBTTagCompound originalNBT = currentNBT.copy();
            currentNBT.merge(this.tileEntityNBT.copy());
            currentNBT.setInteger("x", pos.getX());
            currentNBT.setInteger("y", pos.getY());
            currentNBT.setInteger("z", pos.getZ());
            if (!currentNBT.equals(originalNBT)) {
               tileEntity.readFromNBT(currentNBT);
               tileEntity.markDirty();
            }
         }
      }

   }
}

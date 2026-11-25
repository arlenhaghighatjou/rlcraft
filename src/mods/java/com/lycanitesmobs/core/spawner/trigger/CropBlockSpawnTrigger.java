package com.lycanitesmobs.core.spawner.trigger;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.spawner.Spawner;
import net.minecraft.block.Block;
import net.minecraft.block.BlockVine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

public class CropBlockSpawnTrigger extends BlockSpawnTrigger {
   public CropBlockSpawnTrigger(Spawner spawner) {
      super(spawner);
   }

   public void loadFromJSON(JsonObject json) {
      super.loadFromJSON(json);
   }

   public boolean isTriggerBlock(IBlockState blockState, World world, BlockPos blockPos, int fortune) {
      Block block = blockState.getBlock();
      return block instanceof IPlantable || block instanceof BlockVine;
   }

   public int getBlockLevel(IBlockState blockState, World world, BlockPos blockPos) {
      return 0;
   }
}

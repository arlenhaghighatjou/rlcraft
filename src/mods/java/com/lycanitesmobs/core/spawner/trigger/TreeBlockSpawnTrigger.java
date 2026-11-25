package com.lycanitesmobs.core.spawner.trigger;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.core.spawner.Spawner;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TreeBlockSpawnTrigger extends BlockSpawnTrigger {
   public TreeBlockSpawnTrigger(Spawner spawner) {
      super(spawner);
   }

   public void loadFromJSON(JsonObject json) {
      super.loadFromJSON(json);
   }

   public boolean isTriggerBlock(IBlockState blockState, World world, BlockPos blockPos, int fortune) {
      return this.isTreeLogBlock(blockState.getBlock(), world, blockPos) || this.isTreeLeavesBlock(blockState.getBlock(), world, blockPos);
   }

   public boolean isTreeLogBlock(Block block, World world, BlockPos pos) {
      if (this.isLog(world.getBlockState(pos))) {
         int x = pos.getX();
         int y = pos.getY();
         int z = pos.getZ();

         for(int searchX = x - 1; searchX <= x + 1; ++searchX) {
            for(int searchZ = z - 1; searchZ <= z + 1; ++searchZ) {
               for(int searchY = y; searchY <= Math.min(world.getHeight(), y + 32); ++searchY) {
                  if (this.isLeaves(world.getBlockState(new BlockPos(searchX, searchY, searchZ)))) {
                     return true;
                  }

                  if (!world.isAirBlock(new BlockPos(x, searchY, z))) {
                     break;
                  }
               }
            }
         }
      }

      String blockName = block.getRegistryName().toString();
      return (blockName.contains("tree") || blockName.contains("traverse")) && blockName.contains("branch");
   }

   public boolean isTreeLeavesBlock(Block block, World world, BlockPos pos) {
      if (this.isLeaves(world.getBlockState(pos))) {
         int x = pos.getX();
         int y = pos.getY();
         int z = pos.getZ();

         for(int searchX = x - 1; searchX <= x + 1; ++searchX) {
            for(int searchZ = z - 1; searchZ <= z + 1; ++searchZ) {
               for(int searchY = y; searchY >= Math.max(0, y - 32); --searchY) {
                  if (this.isLog(world.getBlockState(new BlockPos(searchX, searchY, searchZ)))) {
                     return true;
                  }

                  if (!world.isAirBlock(new BlockPos(x, searchY, z))) {
                     break;
                  }
               }
            }
         }
      }

      String blockName = block.getRegistryName().toString();
      return (blockName.contains("tree") || blockName.contains("traverse")) && blockName.contains("leaves");
   }

   public boolean isLog(IBlockState blockState) {
      Block block = blockState.getBlock();
      return block instanceof BlockLog || ObjectLists.isInOreDictionary("logWood", block);
   }

   public boolean isLeaves(IBlockState blockState) {
      Block block = blockState.getBlock();
      return block instanceof BlockLeaves || ObjectLists.isInOreDictionary("treeLeaves", block);
   }
}

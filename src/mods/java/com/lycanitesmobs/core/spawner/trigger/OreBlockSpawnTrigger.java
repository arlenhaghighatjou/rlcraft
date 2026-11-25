package com.lycanitesmobs.core.spawner.trigger;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.spawner.Spawner;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class OreBlockSpawnTrigger extends BlockSpawnTrigger {
   public boolean ores = true;
   public boolean gems = false;

   public OreBlockSpawnTrigger(Spawner spawner) {
      super(spawner);
   }

   public void loadFromJSON(JsonObject json) {
      super.loadFromJSON(json);
      if (json.has("ores")) {
         this.ores = json.get("ores").getAsBoolean();
      }

      if (json.has("gems")) {
         this.gems = json.get("gems").getAsBoolean();
      }

   }

   public boolean isTriggerBlock(IBlockState blockState, World world, BlockPos blockPos, int fortune) {
      Block block = blockState.getBlock();
      if (block == Blocks.MONSTER_EGG) {
         return this.ores;
      } else if (block == Blocks.COAL_ORE) {
         return this.ores;
      } else if (block.getRegistryName() == null) {
         return false;
      } else {
         String blockName = block.getRegistryName().getResourcePath();
         String[] blockNameParts = blockName.split("\\.");

         for(String blockNamePart : blockNameParts) {
            int blockNamePartLength = blockNamePart.length();
            boolean nameMatch = false;
            if (blockNamePartLength >= 3 && (blockNamePart.substring(0, 3).equalsIgnoreCase("ore") || blockNamePart.substring(blockNamePartLength - 3, blockNamePartLength).equalsIgnoreCase("ore"))) {
               nameMatch = true;
            }

            if (!nameMatch && blockNamePartLength >= 7 && (blockNamePart.substring(0, 7).equalsIgnoreCase("crystal") || blockNamePart.substring(blockNamePartLength - 7, blockNamePartLength).equalsIgnoreCase("crystal"))) {
               nameMatch = true;
            }

            if (nameMatch) {
               if (this.ores && this.gems) {
                  return true;
               }

               Item dropItem = block.getItemDropped(blockState, world.rand, fortune);
               if (!(dropItem instanceof ItemBlock) && !blockName.contains("coal")) {
                  return this.gems;
               }

               return this.ores;
            }
         }

         return false;
      }
   }

   public int getBlockLevel(IBlockState blockState, World world, BlockPos blockPos) {
      Block block = blockState.getBlock();
      if (block == Blocks.DIAMOND_ORE) {
         return 3;
      } else if (block == Blocks.EMERALD_ORE) {
         return 3;
      } else if (block == Blocks.LAPIS_ORE) {
         return 2;
      } else if (block == Blocks.GOLD_ORE) {
         return 2;
      } else {
         return block == Blocks.IRON_ORE ? 1 : 0;
      }
   }
}

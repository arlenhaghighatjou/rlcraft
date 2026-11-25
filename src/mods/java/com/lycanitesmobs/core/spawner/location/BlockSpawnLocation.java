package com.lycanitesmobs.core.spawner.location;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.helpers.JSONHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;

public class BlockSpawnLocation extends SpawnLocation {
   public List<String> blockIds = new ArrayList();
   public String listType = "whitelist";
   public boolean surface = true;
   public boolean underground = true;
   public int blockCost = -1;
   public int requiredBlockTypes = 0;
   public Vec3i offset = new Vec3i(0, 0, 0);

   public BlockSpawnLocation() {
   }

   public void loadFromJSON(JsonObject json) {
      if (json.has("blocks")) {
         this.blockIds = JSONHelper.getJsonStrings(json.get("blocks").getAsJsonArray());
      }

      if (json.has("listType")) {
         this.listType = json.get("listType").getAsString();
      }

      if (json.has("surface")) {
         this.surface = json.get("surface").getAsBoolean();
      }

      if (json.has("underground")) {
         this.underground = json.get("underground").getAsBoolean();
      }

      if (json.has("blockCost")) {
         this.blockCost = json.get("blockCost").getAsInt();
      }

      if (json.has("requiredBlockTypes")) {
         this.requiredBlockTypes = json.get("requiredBlockTypes").getAsInt();
      }

      this.offset = JSONHelper.getVec3i(json, "offset");
      super.loadFromJSON(json);
   }

   public List<BlockPos> getSpawnPositions(World world, EntityPlayer player, BlockPos triggerPos) {
      List<BlockPos> spawnPositions = new ArrayList();
      Map<Block, Integer> validBlocksFound = new HashMap();

      for(int y = triggerPos.getY() - this.rangeMax.getY(); y <= triggerPos.getY() + this.rangeMax.getY(); ++y) {
         int yMin = 0;
         if (this.yMin >= 0) {
            yMin = this.yMin;
         }

         if (y < yMin) {
            y = yMin;
         }

         int yMax = world.getActualHeight();
         if (this.yMax >= 0) {
            yMax = Math.min(this.yMax, yMax);
         }

         if (y >= yMax) {
            break;
         }

         for(int x = triggerPos.getX() - this.rangeMax.getX(); x <= triggerPos.getX() + this.rangeMax.getX(); ++x) {
            for(int z = triggerPos.getZ() - this.rangeMax.getZ(); z <= triggerPos.getZ() + this.rangeMax.getZ(); ++z) {
               BlockPos spawnPos = new BlockPos(x, y, z);
               IBlockState blockState = world.getBlockState(spawnPos);
               if (blockState.getBlock() instanceof IFluidBlock) {
                  float filled = ((IFluidBlock)blockState.getBlock()).getFilledPercentage(world, spawnPos);
                  if (filled != 1.0F && filled != -1.0F) {
                     continue;
                  }
               }

               if ((!(blockState.getBlock() instanceof BlockLiquid) || blockState.getBlock().getMetaFromState(blockState) == 0) && this.isValidBlock(world, spawnPos)) {
                  spawnPositions.add(spawnPos.add(this.offset));
                  if (this.requiredBlockTypes > 0) {
                     Block block = world.getBlockState(spawnPos).getBlock();
                     if (!validBlocksFound.containsKey(block)) {
                        validBlocksFound.put(block, Integer.valueOf(1));
                     } else {
                        validBlocksFound.put(block, Integer.valueOf(((Integer)validBlocksFound.get(block)).intValue() + 1));
                     }
                  }
               }
            }
         }
      }

      if (this.blockCost > 0 && spawnPositions.size() < this.blockCost) {
         return new ArrayList();
      } else {
         if (this.requiredBlockTypes > 0) {
            if (validBlocksFound.size() < this.blockIds.size()) {
               return new ArrayList();
            }

            Iterator var14 = validBlocksFound.values().iterator();

            while(var14.hasNext()) {
               int blocksFoundOfType = ((Integer)var14.next()).intValue();
               if (blocksFoundOfType < this.requiredBlockTypes) {
                  return new ArrayList();
               }
            }
         }

         return this.sortSpawnPositions(spawnPositions, world, triggerPos);
      }
   }

   public boolean isValidBlock(World world, BlockPos blockPos) {
      if (!this.surface || !this.underground) {
         if (world.canSeeSky(blockPos)) {
            if (!this.surface) {
               return false;
            }
         } else if (!this.underground) {
            return false;
         }
      }

      if (this.blockIds.isEmpty()) {
         return true;
      } else {
         Block block = world.getBlockState(blockPos).getBlock();
         if ("blacklist".equalsIgnoreCase(this.listType)) {
            return !this.blockIds.contains(block.getRegistryName().toString());
         } else {
            return this.blockIds.contains(block.getRegistryName().toString());
         }
      }
   }
}

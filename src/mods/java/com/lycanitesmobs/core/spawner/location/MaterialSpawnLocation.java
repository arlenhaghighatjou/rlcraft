package com.lycanitesmobs.core.spawner.location;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.helpers.JSONHelper;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MaterialSpawnLocation extends BlockSpawnLocation {
   public List<Material> materials = new ArrayList();

   public MaterialSpawnLocation() {
   }

   public void loadFromJSON(JsonObject json) {
      this.materials = JSONHelper.getJsonMaterials(json);
      super.loadFromJSON(json);
   }

   public boolean isValidBlock(World world, BlockPos blockPos) {
      IBlockState blockState = world.getBlockState(blockPos);
      if (blockState == null) {
         return false;
      } else {
         if (!this.surface || !this.underground) {
            if (world.canSeeSky(blockPos)) {
               if (!this.surface) {
                  return false;
               }
            } else if (!this.underground) {
               return false;
            }
         }

         if ("blacklist".equalsIgnoreCase(this.listType)) {
            return !this.materials.contains(blockState);
         } else {
            return this.materials.contains(blockState);
         }
      }
   }
}

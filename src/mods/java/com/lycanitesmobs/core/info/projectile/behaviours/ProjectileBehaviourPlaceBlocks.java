package com.lycanitesmobs.core.info.projectile.behaviours;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ProjectileBehaviourPlaceBlocks extends ProjectileBehaviour {
   public String blockName;
   public double chance = 1.0D;
   public int radius = 1;
   public int height = 1;
   public boolean solidSurface = true;

   public ProjectileBehaviourPlaceBlocks() {
   }

   public void loadFromJSON(JsonObject json) {
      this.blockName = json.get("block").getAsString();
      if (json.has("chance")) {
         this.chance = json.get("chance").getAsDouble();
      }

      if (json.has("radius")) {
         this.radius = json.get("radius").getAsInt();
      }

      if (json.has("height")) {
         this.height = json.get("height").getAsInt();
      }

      if (json.has("solidSurface")) {
         this.solidSurface = json.get("solidSurface").getAsBoolean();
      }

   }

   public void onProjectileImpact(BaseProjectileEntity projectile, World world, BlockPos pos) {
      Block block = (Block)GameRegistry.findRegistry(Block.class).getValue(new ResourceLocation(this.blockName));
      if (block != null) {
         if (block == Blocks.WATER) {
            block = Blocks.FLOWING_WATER;
         } else if (block == Blocks.LAVA) {
            block = Blocks.FLOWING_LAVA;
         }

         IBlockState blockState = block.getDefaultState();
         if (block instanceof BlockDynamicLiquid || block instanceof BlockFluidBase) {
            blockState = blockState.withProperty(BlockLiquid.LEVEL, Integer.valueOf(2));
         }

         for(int x = -this.radius + 1; x < this.radius; ++x) {
            for(int y = this.height - 1; y < this.height; ++y) {
               for(int z = -this.radius + 1; z < this.radius; ++z) {
                  BlockPos placePos = pos.add(x, y, z);
                  if ((!this.solidSurface || world.getBlockState(placePos.down()).getMaterial().isSolid()) && projectile.canDestroyBlock(placePos) && (this.chance >= 1.0D || this.chance >= world.rand.nextDouble())) {
                     world.setBlockState(placePos, blockState);
                  }
               }
            }
         }

      }
   }
}

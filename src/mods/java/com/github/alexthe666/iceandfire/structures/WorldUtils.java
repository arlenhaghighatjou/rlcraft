package com.github.alexthe666.iceandfire.structures;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldUtils {
   public static Random rand = new Random();

   public WorldUtils() {
   }

   /** @deprecated */
   @Deprecated
   public static void setBlock(World world, int x, int y, int z, Block block, int meta, int flags) {
      BlockPos pos = new BlockPos(x, y, z);
      world.setBlockState(pos, block.getStateFromMeta(meta), flags);
   }

   /** @deprecated */
   @Deprecated
   public static Block getBlock(World world, int x, int y, int z) {
      BlockPos pos = new BlockPos(x, y, z);
      return world.getBlockState(pos).getBlock();
   }

   /** @deprecated */
   @Deprecated
   public static int getBlockMeta(World world, int x, int y, int z) {
      BlockPos pos = new BlockPos(x, y, z);
      return world.getBlockState(pos).getBlock().getMetaFromState(world.getBlockState(pos));
   }

   public void generateSphere(World world, int radius, BlockPos blockpos, Block block, int meta) {
      for(float i = 0.0F; i < (float)radius; i = (float)((double)i + 0.5D)) {
         for(float j = 0.0F; (double)j < 6.283185307179586D * (double)i; j = (float)((double)j + 0.5D)) {
            for(float k = 0.0F; (double)k < 6.283185307179586D * (double)i; k = (float)((double)k + 0.5D)) {
               setBlock(world, (int)Math.floor((double)blockpos.getX() + Math.sin((double)j) * (double)i), (int)Math.floor((double)blockpos.getY() + Math.sin((double)k) * (double)i), (int)Math.floor((double)blockpos.getZ() + Math.cos((double)j) * (double)i), block, meta, 3);
            }
         }
      }

   }
}

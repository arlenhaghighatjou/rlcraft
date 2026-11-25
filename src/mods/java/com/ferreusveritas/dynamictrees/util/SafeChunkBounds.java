package com.ferreusveritas.dynamictrees.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class SafeChunkBounds {
   public static final SafeChunkBounds ANY = new SafeChunkBounds() {
      public boolean inBounds(BlockPos pos, boolean gap) {
         return true;
      }
   };
   protected static final SafeChunkBounds.Tile[] tiles = new SafeChunkBounds.Tile[16];
   private final ChunkPos center;
   private final BlockBounds[] chunkBounds = new BlockBounds[16];

   protected SafeChunkBounds() {
      this.center = null;
   }

   public SafeChunkBounds(World world, ChunkPos pos) {
      this.center = pos;

      for(SafeChunkBounds.Tile t : tiles) {
         ChunkPos cp = new ChunkPos(pos.x + t.pos.x, pos.z + t.pos.z);
         Chunk c = world.getChunkProvider().getLoadedChunk(cp.x, cp.z);
         this.chunkBounds[t.index] = c != null ? new BlockBounds(cp) : BlockBounds.INVALID;
      }

      for(SafeChunkBounds.Tile t : tiles) {
         BlockBounds curr = this.chunkBounds[t.index];
         if (curr != BlockBounds.INVALID) {
            for(EnumFacing dir : EnumFacing.HORIZONTALS) {
               boolean validDir = false;
               if ((t.borders & 1 << dir.getIndex()) != 0) {
                  BlockBounds adjTile = this.chunkBounds[t.index + dir.getFrontOffsetX() + dir.getFrontOffsetZ() * 4];
                  validDir = adjTile != BlockBounds.INVALID;
               }

               if (!validDir) {
                  curr.shrink(dir, 1);
               }
            }
         }
      }

   }

   public boolean inBounds(BlockPos pos, boolean gap) {
      int chunkX = pos.getX() >> 4;
      int chunkZ = pos.getZ() >> 4;
      int tileX = chunkX - this.center.x + 1;
      int tileZ = chunkZ - this.center.z + 1;
      if (((tileX | tileZ) & -4) != 0) {
         return false;
      } else {
         int index = tileX + tileZ * 4;
         return !gap && this.chunkBounds[index] != BlockBounds.INVALID || this.chunkBounds[index].inBounds(pos);
      }
   }

   public boolean inBounds(BlockBounds bounds, boolean gap) {
      BlockPos min = bounds.getMin();
      BlockPos max = bounds.getMax();
      return this.inBounds(min, gap) && this.inBounds(max, gap) && this.inBounds(new BlockPos(min.getX(), 0, max.getZ()), gap) && this.inBounds(new BlockPos(max.getX(), 0, min.getZ()), gap);
   }

   public void setBlockState(World world, BlockPos pos, IBlockState state, boolean gap) {
      this.setBlockState(world, pos, state, 3, gap);
   }

   public void setBlockState(World world, BlockPos pos, IBlockState state, int flags, boolean gap) {
      if (this.inBounds(pos, gap)) {
         world.setBlockState(pos, state, flags);
      }

   }

   static {
      for(int z = 0; z < 4; ++z) {
         for(int x = 0; x < 4; ++x) {
            int index = z * 4 + x;
            tiles[index] = new SafeChunkBounds.Tile(index, new ChunkPos(x - 1, z - 1), (z != 0 ? 4 : 0) | (z != 3 ? 8 : 0) | (x != 0 ? 16 : 0) | (x != 3 ? 32 : 0));
         }
      }

   }

   protected static class Tile {
      public final ChunkPos pos;
      public final int borders;
      public final int index;

      public Tile(int index, ChunkPos pos, int borderFlags) {
         this.index = index;
         this.pos = pos;
         this.borders = borderFlags;
      }
   }
}

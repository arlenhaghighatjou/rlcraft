package org.orecruncher.lib.chunk;

import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;

class PassThroughChunkCache implements IChunkCache, IBlockAccessEx {
   protected final MutableBlockPos pos = new MutableBlockPos();
   protected int minCX;
   protected int minCZ;
   protected int maxCX;
   protected int maxCZ;
   protected World world;
   protected int worldRef;
   protected int ref;
   protected ChunkCache cache;
   protected boolean anyEmpty = true;

   public PassThroughChunkCache() {
   }

   public void update(@Nonnull World world, @Nonnull BlockPos min, @Nonnull BlockPos max) {
      ChunkPos from = new ChunkPos(min);
      ChunkPos to = new ChunkPos(max);
      if (this.anyEmpty || this.world != world || from.x < this.minCX || from.z < this.minCZ || to.x > this.maxCX || to.z > this.maxCZ) {
         if (this.world != world) {
            ++this.worldRef;
         }

         ++this.ref;
         this.world = world;
         this.minCX = from.x;
         this.minCZ = from.z;
         this.maxCX = to.x;
         this.maxCZ = to.z;
         this.cache = new ChunkCache(world, min, max, 0);
         this.anyEmpty = false;

         for(int cX = this.minCX; cX <= this.maxCX; ++cX) {
            for(int cZ = this.minCZ; cZ <= this.maxCZ; ++cZ) {
               if (!this.world.isChunkGeneratedAt(cX, cZ)) {
                  this.anyEmpty = true;
                  break;
               }
            }
         }
      }

   }

   public void clear() {
      this.cache = null;
      this.world = null;
   }

   public TileEntity getTileEntity(@Nonnull BlockPos pos) {
      return this.cache == null ? null : this.cache.getTileEntity(pos);
   }

   public int getCombinedLight(@Nonnull BlockPos pos, int lightValue) {
      return this.cache == null ? lightValue : this.cache.getCombinedLight(pos, lightValue);
   }

   public IBlockState getBlockState(@Nonnull BlockPos pos) {
      return this.cache == null ? Blocks.AIR.getDefaultState() : this.cache.getBlockState(pos);
   }

   public boolean isAirBlock(@Nonnull BlockPos pos) {
      return this.cache != null && this.cache.isAirBlock(pos);
   }

   public Biome getBiome(@Nonnull BlockPos pos) {
      if (this.cache != null) {
         try {
            return this.cache.getBiome(pos);
         } catch (Throwable var3) {
            ;
         }
      }

      return Biomes.PLAINS;
   }

   public int getStrongPower(@Nonnull BlockPos pos, @Nonnull EnumFacing direction) {
      return this.cache == null ? 0 : this.cache.getStrongPower(pos, direction);
   }

   public WorldType getWorldType() {
      return this.cache == null ? WorldType.DEFAULT : this.cache.getWorldType();
   }

   public boolean isSideSolid(@Nonnull BlockPos pos, @Nonnull EnumFacing side, boolean _default) {
      return this.cache != null && this.cache.isSideSolid(pos, side, _default);
   }

   public int reference() {
      return this.ref;
   }

   public int worldReference() {
      return this.worldRef;
   }

   public World getWorld() {
      return this.world;
   }

   public IBlockState getBlockState(int x, int y, int z) {
      return this.cache == null ? Blocks.AIR.getDefaultState() : this.cache.getBlockState(this.pos.setPos(x, y, z));
   }

   public int getLightFor(@Nonnull EnumSkyBlock type, @Nonnull BlockPos pos) {
      return this.cache == null ? 0 : this.cache.getLightFor(type, pos);
   }

   public BlockPos getTopSolidOrLiquidBlock(@Nonnull BlockPos pos) {
      return this.world == null ? pos : this.world.getTopSolidOrLiquidBlock(pos);
   }

   public boolean isAvailable(int x, int z) {
      return this.world != null && this.world.isChunkGeneratedAt(x >> 4, z >> 4);
   }

   public BlockPos getPrecipitationHeight(@Nonnull BlockPos pos) {
      return this.world == null ? pos : this.world.getPrecipitationHeight(pos);
   }
}

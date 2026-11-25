package meldexun.entityculling.util;

import javax.annotation.Nullable;
import meldexun.entityculling.EntityCulling;
import meldexun.entityculling.integration.CubicChunks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class CachedBlockAccess implements IBlockAccess {
   private World level;
   private Chunk cachedChunk;
   private ExtendedBlockStorage cachedSection;

   public CachedBlockAccess() {
   }

   public void setupCached(World level) {
      this.level = level;
      this.cachedChunk = null;
      this.cachedSection = null;
   }

   public void clearCache() {
      this.level = null;
      this.cachedChunk = null;
      this.cachedSection = null;
   }

   private Chunk getChunk(int chunkX, int chunkZ) {
      if (this.cachedChunk == null || this.cachedChunk.x != chunkX || this.cachedChunk.z != chunkZ) {
         this.cachedChunk = this.level.getChunkFromChunkCoords(chunkX, chunkZ);
         this.cachedSection = null;
      }

      return this.cachedChunk;
   }

   @Nullable
   private ExtendedBlockStorage getChunkSection(int chunkX, int chunkY, int chunkZ) {
      if (EntityCulling.isCubicChunksInstalled) {
         this.cachedSection = CubicChunks.getBlockStorage(this.level, chunkX, chunkY, chunkZ);
      } else {
         Chunk chunk = this.getChunk(chunkX, chunkZ);
         if (this.cachedSection == null || this.cachedSection.getYLocation() >> 4 != chunkY) {
            this.cachedSection = chunk.getBlockStorageArray()[chunkY];
         }
      }

      return this.cachedSection;
   }

   public TileEntity getTileEntity(BlockPos pos) {
      throw new UnsupportedOperationException();
   }

   public int getCombinedLight(BlockPos pos, int lightValue) {
      throw new UnsupportedOperationException();
   }

   public IBlockState getBlockState(BlockPos pos) {
      if (this.level.isOutsideBuildHeight(pos)) {
         return Blocks.AIR.getDefaultState();
      } else {
         ExtendedBlockStorage section = this.getChunkSection(pos.getX() >> 4, pos.getY() >> 4, pos.getZ() >> 4);
         return section == null ? Blocks.AIR.getDefaultState() : section.get(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
      }
   }

   public boolean isAirBlock(BlockPos pos) {
      throw new UnsupportedOperationException();
   }

   public Biome getBiome(BlockPos pos) {
      throw new UnsupportedOperationException();
   }

   public int getStrongPower(BlockPos pos, EnumFacing direction) {
      throw new UnsupportedOperationException();
   }

   public WorldType getWorldType() {
      throw new UnsupportedOperationException();
   }

   public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
      throw new UnsupportedOperationException();
   }
}

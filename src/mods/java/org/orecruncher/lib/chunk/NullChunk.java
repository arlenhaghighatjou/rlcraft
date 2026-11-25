package org.orecruncher.lib.chunk;

import com.google.common.base.Predicate;
import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.Chunk.EnumCreateEntityType;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

class NullChunk extends Chunk {
   private static final ExtendedBlockStorage[] NO_STORAGE = new ExtendedBlockStorage[0];

   public NullChunk() {
      super((World)null, Integer.MAX_VALUE, Integer.MAX_VALUE);
   }

   public boolean isAtLocation(int x, int z) {
      return false;
   }

   public int getHeightValue(int x, int z) {
      return 0;
   }

   public void generateHeightMap() {
   }

   public void generateSkylightMap() {
   }

   public IBlockState getBlockState(BlockPos pos) {
      return Blocks.AIR.getDefaultState();
   }

   public int getBlockLightOpacity(BlockPos pos) {
      return 255;
   }

   public int getLightFor(EnumSkyBlock type, BlockPos pos) {
      return type.defaultLightValue;
   }

   public void setLightFor(EnumSkyBlock type, BlockPos pos, int value) {
   }

   public int getLightSubtracted(BlockPos pos, int amount) {
      return 0;
   }

   public void addEntity(Entity entityIn) {
   }

   public void removeEntity(Entity entityIn) {
   }

   public void removeEntityAtIndex(Entity entityIn, int index) {
   }

   public boolean canSeeSky(BlockPos pos) {
      return false;
   }

   @Nullable
   public TileEntity getTileEntity(BlockPos pos, EnumCreateEntityType creationMode) {
      return null;
   }

   public void addTileEntity(TileEntity tileEntityIn) {
   }

   public void addTileEntity(BlockPos pos, TileEntity tileEntityIn) {
   }

   public void removeTileEntity(BlockPos pos) {
   }

   public void onLoad() {
   }

   public void onUnload() {
   }

   public void markDirty() {
   }

   public void getEntitiesWithinAABBForEntity(@Nullable Entity entityIn, AxisAlignedBB aabb, List<Entity> listToFill, Predicate<? super Entity> filter) {
   }

   public <T extends Entity> void getEntitiesOfTypeWithinAABB(Class<? extends T> entityClass, AxisAlignedBB aabb, List<T> listToFill, Predicate<? super T> filter) {
   }

   public boolean needsSaving(boolean p_76601_1_) {
      return false;
   }

   @Nonnull
   public Random getRandomWithSeed(long seed) {
      throw new UnsupportedOperationException();
   }

   public boolean isEmpty() {
      return true;
   }

   public boolean isEmptyBetween(int startY, int endY) {
      return true;
   }

   public int getTopFilledSegment() {
      return -512;
   }

   public Biome getBiome(@Nonnull BlockPos pos, @Nonnull BiomeProvider provider) {
      return Biomes.PLAINS;
   }

   public ExtendedBlockStorage[] getBlockStorageArray() {
      return NO_STORAGE;
   }
}

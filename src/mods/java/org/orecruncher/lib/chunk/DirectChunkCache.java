package org.orecruncher.lib.chunk;

import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;

class DirectChunkCache implements IChunkCache, IBlockAccessEx {
   protected final MutableBlockPos pos = new MutableBlockPos();
   protected World world;
   protected int worldRef;

   public DirectChunkCache() {
   }

   public void update(@Nonnull World world, @Nonnull BlockPos min, @Nonnull BlockPos max) {
      if (this.world != world) {
         ++this.worldRef;
         this.world = world;
      }

   }

   public void clear() {
      this.world = null;
   }

   public TileEntity getTileEntity(@Nonnull BlockPos pos) {
      return this.world == null ? null : this.world.getTileEntity(pos);
   }

   public int getCombinedLight(@Nonnull BlockPos pos, int lightValue) {
      return this.world == null ? lightValue : this.world.getCombinedLight(pos, lightValue);
   }

   public IBlockState getBlockState(@Nonnull BlockPos pos) {
      return this.world == null ? Blocks.AIR.getDefaultState() : this.world.getBlockState(pos);
   }

   public boolean isAirBlock(@Nonnull BlockPos pos) {
      return this.world != null && this.world.isAirBlock(pos);
   }

   public Biome getBiome(@Nonnull BlockPos pos) {
      if (this.world != null) {
         try {
            return this.world.getBiome(pos);
         } catch (Throwable var3) {
            ;
         }
      }

      return Biomes.PLAINS;
   }

   public int getStrongPower(@Nonnull BlockPos pos, @Nonnull EnumFacing direction) {
      return this.world == null ? 0 : this.world.getStrongPower(pos, direction);
   }

   public WorldType getWorldType() {
      return this.world == null ? WorldType.DEFAULT : this.world.getWorldType();
   }

   public boolean isSideSolid(@Nonnull BlockPos pos, @Nonnull EnumFacing side, boolean _default) {
      return this.world != null && this.world.isSideSolid(pos, side, _default);
   }

   public int reference() {
      return 0;
   }

   public int worldReference() {
      return this.worldRef;
   }

   public World getWorld() {
      return this.world;
   }

   public IBlockState getBlockState(int x, int y, int z) {
      return this.world == null ? Blocks.AIR.getDefaultState() : this.world.getBlockState(this.pos.setPos(x, y, z));
   }

   public int getLightFor(@Nonnull EnumSkyBlock type, @Nonnull BlockPos pos) {
      return this.world == null ? 0 : this.world.getLightFor(type, pos);
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

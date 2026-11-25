package rtg.api.world.surface;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import rtg.api.RTGAPI;
import rtg.api.config.BiomeConfig;
import rtg.api.util.BlockUtil;
import rtg.api.world.RTGWorld;

public abstract class SurfaceBase {
   public IBlockState shadowStoneBlock;
   public IBlockState shadowDesertBlock;
   protected IBlockState topBlock;
   protected IBlockState fillerBlock;
   protected IBlockState cliffStoneBlock;
   protected IBlockState cliffCobbleBlock;
   protected BiomeConfig biomeConfig;

   public SurfaceBase(BiomeConfig config, Block top, Block fill) {
      this(config, top.getDefaultState(), fill.getDefaultState());
   }

   public SurfaceBase(BiomeConfig config, IBlockState top, IBlockState fill) {
      if (config == null) {
         throw new RuntimeException("Biome config in SurfaceBase is NULL.");
      } else {
         this.biomeConfig = config;
         this.topBlock = top;
         this.fillerBlock = fill;
         this.initCustomBlocks();
         this.assignUserConfigs(config, top, fill);
      }
   }

   public void paintTerrain(ChunkPrimer primer, int i, int j, int x, int z, int depth, RTGWorld rtgWorld, float[] noise, float river, Biome[] base) {
   }

   protected IBlockState getShadowStoneBlock() {
      return this.shadowStoneBlock;
   }

   protected IBlockState getShadowDesertBlock() {
      return this.shadowDesertBlock;
   }

   protected IBlockState hcStone() {
      return this.cliffStoneBlock;
   }

   protected IBlockState hcCobble() {
      return this.cliffCobbleBlock;
   }

   public IBlockState getTopBlock() {
      return this.topBlock;
   }

   public IBlockState getFillerBlock() {
      return this.fillerBlock;
   }

   private void assignUserConfigs(BiomeConfig config, IBlockState top, IBlockState fill) {
      this.topBlock = this.getConfigBlock(config.SURFACE_TOP_BLOCK.get(), top);
      this.fillerBlock = this.getConfigBlock(config.SURFACE_FILLER_BLOCK.get(), fill);
   }

   protected IBlockState getConfigBlock(String configString, IBlockState defaultState) {
      return BlockUtil.getBlockStateFromCfgString(configString, defaultState);
   }

   private void initCustomBlocks() {
      this.cliffStoneBlock = this.getConfigBlock(this.biomeConfig.SURFACE_CLIFF_STONE_BLOCK.get(), Blocks.STONE.getDefaultState());
      this.cliffCobbleBlock = this.getConfigBlock(this.biomeConfig.SURFACE_CLIFF_COBBLE_BLOCK.get(), Blocks.COBBLESTONE.getDefaultState());
      this.shadowStoneBlock = RTGAPI.getShadowStoneBlock();
      this.shadowDesertBlock = RTGAPI.getShadowDesertBlock();
   }
}

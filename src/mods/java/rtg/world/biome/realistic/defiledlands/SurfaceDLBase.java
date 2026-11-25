package rtg.world.biome.realistic.defiledlands;

import net.minecraft.block.state.IBlockState;
import rtg.api.config.BiomeConfig;
import rtg.api.util.BlockUtil;
import rtg.api.world.surface.SurfaceBase;

public abstract class SurfaceDLBase extends SurfaceBase {
   public SurfaceDLBase(BiomeConfig config, IBlockState top, IBlockState fill) {
      super(config, top, fill);
   }

   protected IBlockState hcStone() {
      return BlockUtil.getBlockStateFromCfgString("defiledlands:stone_defiled", super.hcStone());
   }

   protected IBlockState hcCobble() {
      return BlockUtil.getBlockStateFromCfgString("defiledlands:stone_defiled", super.hcCobble());
   }

   protected IBlockState getShadowStoneBlock() {
      return BlockUtil.getBlockStateFromCfgString("defiledlands:stone_defiled", super.getShadowStoneBlock());
   }

   protected IBlockState getShadowDesertBlock() {
      return BlockUtil.getBlockStateFromCfgString("defiledlands:sandstone_defiled", super.getShadowDesertBlock());
   }
}

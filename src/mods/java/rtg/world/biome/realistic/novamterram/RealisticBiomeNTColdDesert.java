package rtg.world.biome.realistic.novamterram;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import rtg.api.util.BlockUtil;
import rtg.api.world.biome.RealisticBiomeBase;
import rtg.api.world.surface.SurfaceBase;

public class RealisticBiomeNTColdDesert extends RealisticBiomeNTBaseDesert {
   public RealisticBiomeNTColdDesert(Biome biome) {
      super(biome, RealisticBiomeBase.RiverType.FROZEN, RealisticBiomeBase.BeachType.COLD);
   }

   protected IBlockState getMixBlock1() {
      return BlockUtil.getBlockStateFromCfgString("nt:cold_sand", Blocks.SAND.getDefaultState());
   }

   protected IBlockState getMixBlock2() {
      return Blocks.SAND.getDefaultState();
   }

   protected IBlockState getMixBlock3() {
      return BlockUtil.getBlockStateFromCfgString("nt:cold_sand", Blocks.SAND.getDefaultState());
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeNTBase.SurfaceNTGeneric(this.getConfig(), Blocks.SAND.getDefaultState(), this.baseBiome().fillerBlock, 0.0F, 1.5F, 60.0F, 65.0F, 1.5F, this.getMixBlock1(), 0.6F, this.getMixBlock2(), -0.2F, this.getMixBlock3(), -0.4F);
   }
}

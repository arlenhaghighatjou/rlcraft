package rtg.world.biome.realistic.novamterram;

import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import rtg.api.world.surface.SurfaceBase;

public class RealisticBiomeNTSandyMountains extends RealisticBiomeNTBaseMountain {
   public RealisticBiomeNTSandyMountains(Biome biome) {
      super(biome);
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeNTBaseMountain.SurfaceBOPMountainPeaks(this.getConfig(), Blocks.SAND.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), Blocks.SAND.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), 80.0F, -0.15F, 10.0F, 0.5F);
   }
}

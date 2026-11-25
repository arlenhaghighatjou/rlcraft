package rtg.world.biome.realistic.novamterram;

import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import rtg.api.world.biome.RealisticBiomeBase;
import rtg.api.world.surface.SurfaceBase;

public class RealisticBiomeNTGlacier extends RealisticBiomeNTBaseGlacier {
   public RealisticBiomeNTGlacier(Biome biome) {
      super(biome, RealisticBiomeBase.RiverType.FROZEN, RealisticBiomeBase.BeachType.COLD);
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeNTBaseGlacier.SurfaceBOPGlacier(this.getConfig(), Blocks.SNOW.getDefaultState(), Blocks.ICE.getDefaultState(), Blocks.PACKED_ICE.getDefaultState(), Blocks.ICE.getDefaultState(), Blocks.PACKED_ICE.getDefaultState(), Blocks.ICE.getDefaultState(), 60.0F, -0.14F, 14.0F, 0.25F);
   }
}

package rtg.world.biome.realistic.novamterram;

import net.minecraft.world.biome.Biome;
import rtg.api.world.biome.RealisticBiomeBase;

public class RealisticBiomeNTAlps extends RealisticBiomeNTBaseAlps {
   public RealisticBiomeNTAlps(Biome biome) {
      super(biome, RealisticBiomeBase.RiverType.FROZEN, RealisticBiomeBase.BeachType.COLD);
   }
}

package rtg.world.biome.realistic.novamterram;

import net.minecraft.world.biome.Biome;
import rtg.api.world.biome.RealisticBiomeBase;

public class RealisticBiomeNTIcyTundra extends RealisticBiomeNTBaseForest {
   public RealisticBiomeNTIcyTundra(Biome biome) {
      super(biome, RealisticBiomeBase.RiverType.FROZEN, RealisticBiomeBase.BeachType.COLD);
   }
}

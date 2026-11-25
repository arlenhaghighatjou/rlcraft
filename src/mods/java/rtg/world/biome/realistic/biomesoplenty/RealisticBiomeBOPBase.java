package rtg.world.biome.realistic.biomesoplenty;

import biomesoplenty.api.biome.IExtendedBiome;
import javax.annotation.Nonnull;
import net.minecraft.world.biome.Biome;
import rtg.api.world.biome.RealisticBiomeBase;

public abstract class RealisticBiomeBOPBase extends RealisticBiomeBase {
   public RealisticBiomeBOPBase(@Nonnull Biome baseBiome, @Nonnull RealisticBiomeBase.RiverType riverType, @Nonnull RealisticBiomeBase.BeachType beachType) {
      super(baseBiome, riverType, beachType);
      if (!(baseBiome instanceof IExtendedBiome)) {
         throw new ClassCastException(String.format("Tried creating an instance of a IRealisticBiome for a BOPBiome without a %s.", baseBiome.getClass().getName()));
      }
   }

   public RealisticBiomeBOPBase(@Nonnull Biome baseBiome) {
      this(baseBiome, RealisticBiomeBase.RiverType.NORMAL, RealisticBiomeBase.BeachType.NORMAL);
   }

   public RealisticBiomeBOPBase(@Nonnull Biome baseBiome, @Nonnull RealisticBiomeBase.RiverType riverType) {
      this(baseBiome, riverType, RealisticBiomeBase.BeachType.NORMAL);
   }

   public RealisticBiomeBOPBase(@Nonnull Biome baseBiome, @Nonnull RealisticBiomeBase.BeachType beachType) {
      this(baseBiome, RealisticBiomeBase.RiverType.NORMAL, beachType);
   }

   public void initDecos() {
   }
}

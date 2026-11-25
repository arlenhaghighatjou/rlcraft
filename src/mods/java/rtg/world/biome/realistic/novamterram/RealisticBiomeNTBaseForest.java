package rtg.world.biome.realistic.novamterram;

import javax.annotation.Nonnull;
import net.minecraft.world.biome.Biome;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.RealisticBiomeBase;
import rtg.api.world.terrain.TerrainBase;

public abstract class RealisticBiomeNTBaseForest extends RealisticBiomeNTBase {
   public RealisticBiomeNTBaseForest(@Nonnull Biome baseBiome, @Nonnull RealisticBiomeBase.RiverType riverType, @Nonnull RealisticBiomeBase.BeachType beachType) {
      super(baseBiome, riverType, beachType);
   }

   public RealisticBiomeNTBaseForest(@Nonnull Biome baseBiome) {
      this(baseBiome, RealisticBiomeBase.RiverType.NORMAL, RealisticBiomeBase.BeachType.NORMAL);
   }

   public RealisticBiomeNTBaseForest(@Nonnull Biome baseBiome, @Nonnull RealisticBiomeBase.RiverType riverType) {
      this(baseBiome, riverType, RealisticBiomeBase.BeachType.NORMAL);
   }

   public RealisticBiomeNTBaseForest(@Nonnull Biome baseBiome, @Nonnull RealisticBiomeBase.BeachType beachType) {
      this(baseBiome, RealisticBiomeBase.RiverType.NORMAL, beachType);
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeNTBaseForest.TerrainBOPBrushland();
   }

   public static class TerrainBOPBrushland extends TerrainBase {
      private float baseHeight = 65.0F;
      private float hillStrength = 12.0F;

      public TerrainBOPBrushland() {
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         this.groundNoise = groundNoise(x, y, this.groundNoiseAmplitudeHills, rtgWorld);
         float m = hills((float)x, (float)y, this.hillStrength, rtgWorld);
         return riverized(this.baseHeight + this.groundNoise + m, river);
      }
   }
}

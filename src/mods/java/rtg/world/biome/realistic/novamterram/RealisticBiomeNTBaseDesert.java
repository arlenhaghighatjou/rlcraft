package rtg.world.biome.realistic.novamterram;

import javax.annotation.Nonnull;
import net.minecraft.world.biome.Biome;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.RealisticBiomeBase;
import rtg.api.world.gen.RTGChunkGenSettings;
import rtg.api.world.terrain.TerrainBase;

public abstract class RealisticBiomeNTBaseDesert extends RealisticBiomeNTBase {
   public RealisticBiomeNTBaseDesert(@Nonnull Biome baseBiome, @Nonnull RealisticBiomeBase.RiverType riverType, @Nonnull RealisticBiomeBase.BeachType beachType) {
      super(baseBiome, riverType, beachType);
   }

   public RealisticBiomeNTBaseDesert(@Nonnull Biome baseBiome) {
      this(baseBiome, RealisticBiomeBase.RiverType.NORMAL, RealisticBiomeBase.BeachType.NORMAL);
   }

   public RealisticBiomeNTBaseDesert(@Nonnull Biome baseBiome, @Nonnull RealisticBiomeBase.RiverType riverType) {
      this(baseBiome, riverType, RealisticBiomeBase.BeachType.NORMAL);
   }

   public RealisticBiomeNTBaseDesert(@Nonnull Biome baseBiome, @Nonnull RealisticBiomeBase.BeachType beachType) {
      this(baseBiome, RealisticBiomeBase.RiverType.NORMAL, beachType);
   }

   public void initConfig() {
      super.initConfig();
      this.getConfig().SURFACE_WATER_LAKE_MULT.set(0.0F);
      this.getConfig().ALLOW_SCENIC_LAKES.set(false);
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeNTBaseDesert.TerrainVanillaDesert();
   }

   public static class TerrainVanillaDesert extends TerrainBase {
      public TerrainVanillaDesert() {
         super(64.0F);
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         RTGChunkGenSettings settings = rtgWorld.getGeneratorSettings();
         float duneHeight = this.minDuneHeight + (float)settings.sandDuneHeight;
         duneHeight = duneHeight * ((1.0F + rtgWorld.simplexInstance(2).noise2f((float)x / 330.0F, (float)y / 330.0F)) / 2.0F);
         float stPitch = 200.0F;
         float hPitch = 70.0F;
         float hDivisor = 40.0F;
         return terrainPolar((float)x, (float)y, rtgWorld, river, stPitch, duneHeight, hPitch, hDivisor, this.base) + groundNoise(x, y, 1.0F, rtgWorld);
      }
   }
}

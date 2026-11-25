package rtg.world.biome.realistic.rockhounding;

import net.minecraft.world.biome.Biome;
import rtg.api.world.RTGWorld;
import rtg.api.world.gen.RTGChunkGenSettings;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeRHWhiteSands extends RealisticBiomeRHBase {
   public RealisticBiomeRHWhiteSands(Biome biome) {
      super(biome);
   }

   public void initConfig() {
      this.getConfig().ALLOW_SCENIC_LAKES.set(false);
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeRHWhiteSands.TerrainVanillaDesert();
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

package rtg.world.biome.realistic.environs;

import net.minecraft.world.biome.Biome;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.RealisticBiomeBase;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeENVIcyHills extends RealisticBiomeENVBase {
   public RealisticBiomeENVIcyHills(Biome biome) {
      super(biome, RealisticBiomeBase.RiverType.FROZEN, RealisticBiomeBase.BeachType.COLD);
   }

   public void initConfig() {
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeENVIcyHills.TerrainVanillaForestHills();
   }

   public static class TerrainVanillaForestHills extends TerrainBase {
      private float hillStrength;

      public TerrainVanillaForestHills() {
         this(72.0F, 30.0F);
      }

      public TerrainVanillaForestHills(float bh, float hs) {
         this.hillStrength = 30.0F;
         this.base = bh;
         this.hillStrength = hs;
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return terrainHighland((float)x, (float)y, rtgWorld, river, 10.0F, 68.0F, this.hillStrength, this.base - 62.0F);
      }
   }
}

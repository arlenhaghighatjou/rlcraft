package rtg.world.biome.realistic.environs;

import net.minecraft.world.biome.Biome;
import rtg.api.world.RTGWorld;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeENVTallOakForest extends RealisticBiomeENVBase {
   public RealisticBiomeENVTallOakForest(Biome biome) {
      super(biome);
   }

   public void initConfig() {
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeENVTallOakForest.TerrainVanillaForest();
   }

   public static class TerrainVanillaForest extends TerrainBase {
      private float hillStrength = 10.0F;

      public TerrainVanillaForest() {
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         this.groundNoise = groundNoise(x, y, this.groundVariation, rtgWorld);
         float m = hills((float)x, (float)y, this.hillStrength, rtgWorld);
         float floNoise = 65.0F + this.groundNoise + m;
         return riverized(floNoise, river);
      }
   }
}

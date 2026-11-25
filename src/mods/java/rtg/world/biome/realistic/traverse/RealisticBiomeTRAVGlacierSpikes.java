package rtg.world.biome.realistic.traverse;

import net.minecraft.world.biome.Biome;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.RealisticBiomeBase;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeTRAVGlacierSpikes extends RealisticBiomeTRAVBase {
   public RealisticBiomeTRAVGlacierSpikes(Biome biome) {
      super(biome, RealisticBiomeBase.RiverType.FROZEN, RealisticBiomeBase.BeachType.COLD);
   }

   public void initConfig() {
      this.getConfig().ALLOW_RIVERS.set(false);
      this.getConfig().ALLOW_SCENIC_LAKES.set(false);
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeTRAVGlacierSpikes.TerrainBOPGlacier(230.0F, 40.0F, 68.0F);
   }

   public static class TerrainBOPGlacier extends TerrainBase {
      private float width;
      private float strength;

      public TerrainBOPGlacier(float mountainWidth, float mountainStrength, float height) {
         this.width = mountainWidth;
         this.strength = mountainStrength;
         this.base = height;
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return terrainLonelyMountain(x, y, rtgWorld, river, this.strength, this.width, this.base);
      }
   }
}

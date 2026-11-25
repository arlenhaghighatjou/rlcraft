package rtg.world.biome.realistic.traverse;

import net.minecraft.world.biome.Biome;
import rtg.api.world.RTGWorld;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeTRAVMiniJungle extends RealisticBiomeTRAVBase {
   public RealisticBiomeTRAVMiniJungle(Biome biome) {
      super(biome);
   }

   public void initConfig() {
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeTRAVMiniJungle.TerrainVanillaJungle();
   }

   public static class TerrainVanillaJungle extends TerrainBase {
      public TerrainVanillaJungle() {
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return terrainFlatLakes(x, y, rtgWorld, river, 66.0F);
      }
   }
}

package rtg.world.biome.realistic.traverse;

import net.minecraft.world.biome.Biome;
import rtg.api.world.RTGWorld;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeTRAVMeadow extends RealisticBiomeTRAVBase {
   public RealisticBiomeTRAVMeadow(Biome biome) {
      super(biome);
   }

   public void initConfig() {
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeTRAVMeadow.TerrainBOPMeadow();
   }

   public static class TerrainBOPMeadow extends TerrainBase {
      public TerrainBOPMeadow() {
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return terrainPlains(x, y, rtgWorld, river, 160.0F, 10.0F, 60.0F, 200.0F, 66.0F);
      }
   }
}

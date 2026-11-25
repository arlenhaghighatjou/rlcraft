package rtg.world.biome.realistic.environs;

import net.minecraft.world.biome.Biome;
import rtg.api.world.RTGWorld;
import rtg.api.world.terrain.TerrainBase;
import rtg.api.world.terrain.heighteffect.GroundEffect;

public class RealisticBiomeENVWastelandEroded extends RealisticBiomeENVBase {
   public RealisticBiomeENVWastelandEroded(Biome biome) {
      super(biome);
   }

   public void initConfig() {
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeENVWastelandEroded.TerrainVanillaPlains();
   }

   public static class TerrainVanillaPlains extends TerrainBase {
      private GroundEffect groundEffect = new GroundEffect(4.0F);

      public TerrainVanillaPlains() {
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return riverized(65.0F + this.groundEffect.added(rtgWorld, (float)x, (float)y), river);
      }
   }
}

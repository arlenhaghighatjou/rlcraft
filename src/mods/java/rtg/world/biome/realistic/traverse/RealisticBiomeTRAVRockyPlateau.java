package rtg.world.biome.realistic.traverse;

import net.minecraft.world.biome.Biome;
import rtg.api.util.PlateauUtil;
import rtg.api.util.noise.ISimplexData2D;
import rtg.api.util.noise.SimplexData2D;
import rtg.api.world.RTGWorld;
import rtg.api.world.terrain.TerrainBase;
import rtg.api.world.terrain.heighteffect.VoronoiPlateauEffect;

public class RealisticBiomeTRAVRockyPlateau extends RealisticBiomeTRAVBase {
   public RealisticBiomeTRAVRockyPlateau(Biome biome) {
      super(biome);
   }

   public void initConfig() {
      this.getConfig().ALLOW_SCENIC_LAKES.set(false);
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeTRAVRockyPlateau.TerrainRTGMesaPlateau(67.0F);
   }

   public static class TerrainRTGMesaPlateau extends TerrainBase {
      private static final float stepStart = 0.25F;
      private static final float stepFinish = 0.4F;
      private static final float stepHeight = 32.0F;
      final VoronoiPlateauEffect plateau = new VoronoiPlateauEffect();
      final int groundNoise;
      private float jitterWavelength = 30.0F;
      private float jitterAmplitude = 10.0F;
      private float bumpinessMultiplier = 0.05F;
      private float bumpinessWavelength = 10.0F;

      public TerrainRTGMesaPlateau(float base) {
         this.plateau.pointWavelength = 200.0F;
         this.base = base;
         this.groundNoise = 4;
      }

      public float generateNoise(RTGWorld rtgWorld, int passedX, int passedY, float border, float river) {
         ISimplexData2D jitterData = SimplexData2D.newDisk();
         rtgWorld.simplexInstance(1).multiEval2D((double)((float)passedX / this.jitterWavelength), (double)((float)passedY / this.jitterWavelength), jitterData);
         float x = (float)((double)passedX + jitterData.getDeltaX() * (double)this.jitterAmplitude);
         float y = (float)((double)passedY + jitterData.getDeltaY() * (double)this.jitterAmplitude);
         float bordercap;
         bordercap = (bordercap = border * 3.5F - 2.5F) > 1.0F ? 1.0F : bordercap;
         float rivercap;
         rivercap = (rivercap = 3.0F * river) > 1.0F ? 1.0F : rivercap;
         float bumpiness = rtgWorld.simplexInstance(2).noise2f(x / this.bumpinessWavelength, y / this.bumpinessWavelength) * this.bumpinessMultiplier;
         float simplex = this.plateau.added(rtgWorld, x, y) * bordercap * rivercap + bumpiness;
         float added = PlateauUtil.stepIncrease(simplex, 0.25F, 0.4F, 32.0F) / border;
         return riverized(this.base + TerrainBase.groundNoise(x, y, (float)this.groundNoise, rtgWorld), river) + added;
      }
   }
}

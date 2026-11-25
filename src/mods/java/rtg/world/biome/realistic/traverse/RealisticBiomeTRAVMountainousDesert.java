package rtg.world.biome.realistic.traverse;

import net.minecraft.world.biome.Biome;
import rtg.api.util.noise.ISimplexData2D;
import rtg.api.util.noise.SimplexData2D;
import rtg.api.util.noise.SimplexNoise;
import rtg.api.world.RTGWorld;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeTRAVMountainousDesert extends RealisticBiomeTRAVBase {
   public RealisticBiomeTRAVMountainousDesert(Biome biome) {
      super(biome);
   }

   public void initConfig() {
      this.getConfig().ALLOW_RIVERS.set(false);
      this.getConfig().ALLOW_SCENIC_LAKES.set(false);
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeTRAVMountainousDesert.TerrainHLGreyMountains(230.0F, 100.0F, 68.0F);
   }

   public static class TerrainHLGreyMountains extends TerrainBase {
      private float width;
      private float strength;
      private float terrainHeight;
      private int wavelength = 39;
      private ISimplexData2D jitter = SimplexData2D.newDisk();
      private double amplitude = 12.0D;

      public TerrainHLGreyMountains(float mountainWidth, float mountainStrength, float height) {
         this.width = mountainWidth;
         this.strength = mountainStrength;
         this.terrainHeight = height;
         this.terrainHeight = 30.0F;
         this.width = 120.0F;
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         SimplexNoise simplex = rtgWorld.simplexInstance(0);
         simplex.multiEval2D((double)((float)x / (float)this.wavelength), (double)((float)y / (float)this.wavelength), this.jitter);
         float pX = (float)((double)((float)x) + this.jitter.getDeltaX() * this.amplitude);
         float pY = (float)((double)((float)y) + this.jitter.getDeltaY() * this.amplitude);
         float h = simplex.noise2f(pX / 19.0F, pY / 19.0F);
         h = h * h * 2.0F;
         float h2 = simplex.noise2f(pX / 13.0F, pY / 13.0F);
         h2 = h2 * h2 * 1.3F;
         h = Math.max(h, h2);
         h = h + h2;
         float h3 = simplex.noise2f(pX / 53.0F, pY / 53.0F);
         h3 = h3 * h3 * 5.0F;
         h = h + h3;
         float m = unsignedPower(simplex.noise2f(pX / this.width, pY / this.width), 1.4F) * this.strength * river;
         float m2 = unsignedPower(simplex.noise2f(pY / (this.width * 1.5F), pX / (this.width * 1.5F)), 1.4F) * this.strength * river;
         m = Math.max(m, m2);
         h = m > 10.0F ? h * m / 10.0F : h;
         m = above(m, -50.0F);
         return this.terrainHeight + h + m;
      }
   }
}

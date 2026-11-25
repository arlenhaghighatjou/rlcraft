package paulevs.betternether.noise;

public class WorleyNoiseIDDistorted3D {
   private WorleyNoiseID3D idNoise;
   private WorleyNoiseOctaved noiseX;
   private WorleyNoiseOctaved noiseY;
   private WorleyNoiseOctaved noiseZ;

   public WorleyNoiseIDDistorted3D(long seed, int maxID) {
      this.idNoise = new WorleyNoiseID3D(seed, maxID);
      this.noiseX = new WorleyNoiseOctaved(seed + 9L, 3);
      this.noiseY = new WorleyNoiseOctaved(seed + 19L, 3);
      this.noiseZ = new WorleyNoiseOctaved(seed + 29L, 3);
   }

   public int GetValue(double x, double y, double z) {
      double nx = x;
      double ny = y;
      double nz = z;
      x = x * 0.5D;
      y = y * 0.5D;
      z = z * 0.5D;
      nx = nx + this.noiseX.GetValue(y, z);
      ny = ny + this.noiseY.GetValue(x, -z);
      nz = nz + this.noiseZ.GetValue(-x, -y);
      return this.idNoise.GetValue(nx, ny, nz);
   }
}

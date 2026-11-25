package com.bloodnbonesgaming.lib.util.noise;

public class OpenSimplexNoiseGeneratorOctaves {
   protected final OpenSimplexNoiseGenerator generator;

   public OpenSimplexNoiseGeneratorOctaves(long seed) {
      this.generator = new OpenSimplexNoiseGenerator(seed);
   }

   public double eval(double x, double y, double z, int octaves, double persistence) {
      double total = 0.0D;
      double frequency = 1.0D;
      double amplitude = 1.0D;
      double maxValue = 0.0D;

      for(int i = 0; i < octaves; ++i) {
         total += (this.generator.eval(x * frequency, y * frequency, z * frequency) + 1.0D) / 2.0D * amplitude;
         maxValue += amplitude;
         amplitude *= persistence;
         frequency *= 2.0D;
      }

      return total / maxValue;
   }

   public double eval(double x, double y, double z, double startingFeatureSize, int octaves, double persistence) {
      return this.eval(x / startingFeatureSize, y / startingFeatureSize, z / startingFeatureSize, octaves, persistence);
   }

   public double[] eval(double[] array, int arraySizeX, int arraySizeY, int arraySizeZ, double x, double y, double z, int octaves, double persistence, int xCoordinateScale, int yCoordinateScale, int zCoordinateScale, double xScale, double yScale, double zScale) {
      for(int xI = 0; xI < arraySizeX; ++xI) {
         for(int zI = 0; zI < arraySizeZ; ++zI) {
            for(int yI = 0; yI < arraySizeY; ++yI) {
               int index = (xI * arraySizeX + zI) * arraySizeY + yI;
               array[index] = this.eval((x + (double)(xI * xCoordinateScale)) / xScale, yScale == 0.0D ? 0.0D : (y + (double)(yI * yCoordinateScale)) / yScale, (z + (double)(zI * zCoordinateScale)) / zScale, octaves, persistence) * 2.0D - 1.0D;
            }
         }
      }

      return array;
   }

   public double eval(double x, double z, int octaves, double persistence) {
      double total = 0.0D;
      double frequency = 1.0D;
      double amplitude = 1.0D;
      double maxValue = 0.0D;

      for(int i = 0; i < octaves; ++i) {
         total += (this.generator.eval(x * frequency, z * frequency) + 1.0D) / 2.0D * amplitude;
         maxValue += amplitude;
         amplitude *= persistence;
         frequency *= 2.0D;
      }

      return total / maxValue;
   }
}

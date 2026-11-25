package com.yungnickyoung.minecraft.bettercaves.noise;

import com.yungnickyoung.minecraft.bettercaves.config.BCSettings;
import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Vector3f;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NoiseGen {
   private long seed;
   private int numGenerators;
   private NoiseSettings noiseSettings;
   private float yCompression;
   private float xzCompression;
   private List<INoiseLibrary> listNoiseGens = new ArrayList();

   public NoiseGen(World world, boolean isFastNoise, NoiseSettings noiseSettings, int numGenerators, float yComp, float xzComp) {
      this.seed = world.getSeed();
      this.noiseSettings = noiseSettings;
      this.numGenerators = numGenerators;
      this.yCompression = yComp;
      this.xzCompression = xzComp;
      this.initializeNoiseGens(isFastNoise);
   }

   public NoiseColumn generateNoiseColumn(BlockPos blockPos, int minHeight, int maxHeight) {
      int x = blockPos.getX();
      int z = blockPos.getZ();
      NoiseColumn noiseColumn = new NoiseColumn();

      for(int y = minHeight; y <= maxHeight; ++y) {
         Vector3f f = new Vector3f((float)x * this.xzCompression, (float)y * this.yCompression, (float)z * this.xzCompression);
         NoiseTuple newTuple = new NoiseTuple(new double[0]);

         for(int i = 0; i < this.numGenerators; ++i) {
            newTuple.put((double)((INoiseLibrary)this.listNoiseGens.get(i)).GetNoise(f.x, f.y, f.z));
         }

         noiseColumn.put(y, newTuple);
      }

      return noiseColumn;
   }

   public NoiseColumn interpolateNoiseColumn(BlockPos blockPos, int minHeight, int maxHeight, int subChunkSize) {
      int x = blockPos.getX();
      int z = blockPos.getZ();
      NoiseColumn noiseColumn = new NoiseColumn();

      for(int startY = minHeight; startY <= maxHeight; startY += subChunkSize) {
         int endY = Math.min(startY + subChunkSize - 1, maxHeight);
         Vector3f subChunkStart = new Vector3f((float)x * this.xzCompression, (float)startY * this.yCompression, (float)z * this.xzCompression);
         Vector3f subChunkEnd = new Vector3f((float)x * this.xzCompression, (float)endY * this.yCompression, (float)z * this.xzCompression);
         NoiseTuple startTuple = new NoiseTuple(new double[0]);
         NoiseTuple endTuple = new NoiseTuple(new double[0]);

         for(int i = 0; i < this.numGenerators; ++i) {
            startTuple.put((double)((INoiseLibrary)this.listNoiseGens.get(i)).GetNoise(subChunkStart.x, subChunkStart.y, subChunkStart.z));
            endTuple.put((double)((INoiseLibrary)this.listNoiseGens.get(i)).GetNoise(subChunkEnd.x, subChunkEnd.y, subChunkEnd.z));
         }

         noiseColumn.put(startY, startTuple);
         noiseColumn.put(endY, endTuple);

         for(int y = startY + 1; y < endY; ++y) {
            float startCoeff;
            float endCoeff;
            if (endY == maxHeight) {
               startCoeff = (float)(endY - startY - y - startY) / (float)(endY - startY);
               endCoeff = (float)(y - startY) / (float)(endY - startY);
            } else {
               startCoeff = BCSettings.START_COEFFS[y - startY];
               endCoeff = BCSettings.END_COEFFS[y - startY];
            }

            NoiseTuple newTuple = startTuple.times(startCoeff).plus(endTuple.times(endCoeff));
            noiseColumn.put(y, newTuple);
         }
      }

      return noiseColumn;
   }

   public NoiseCube interpolateNoiseCube(BlockPos startPos, BlockPos endPos, int minHeight, int maxHeight) {
      int startX = startPos.getX();
      int endX = endPos.getX();
      int startZ = startPos.getZ();
      int endZ = endPos.getZ();
      int subChunkSize = endX - startX + 1;
      NoiseColumn noisesX0Z0 = this.generateNoiseColumn(new BlockPos(startX, 1, startZ), minHeight, maxHeight);
      NoiseColumn noisesX0Z1 = this.generateNoiseColumn(new BlockPos(startX, 1, endZ), minHeight, maxHeight);
      NoiseColumn noisesX1Z0 = this.generateNoiseColumn(new BlockPos(endX, 1, startZ), minHeight, maxHeight);
      NoiseColumn noisesX1Z1 = this.generateNoiseColumn(new BlockPos(endX, 1, endZ), minHeight, maxHeight);
      NoiseCube cube = new NoiseCube(subChunkSize);
      cube.get(0).set(0, noisesX0Z0);
      cube.get(0).set(subChunkSize - 1, noisesX0Z1);
      cube.get(subChunkSize - 1).set(0, noisesX1Z0);
      cube.get(subChunkSize - 1).set(subChunkSize - 1, noisesX1Z1);

      for(int x = 1; x < subChunkSize - 1; ++x) {
         float startCoeff = BCSettings.START_COEFFS[x];
         float endCoeff = BCSettings.END_COEFFS[x];
         NoiseColumn xz0 = (NoiseColumn)cube.get(x).get(0);

         for(int y = minHeight; y <= maxHeight; ++y) {
            NoiseTuple startTuple = ((NoiseColumn)cube.get(0).get(0)).get(y);
            NoiseTuple endTuple = ((NoiseColumn)cube.get(subChunkSize - 1).get(0)).get(y);
            NoiseTuple newTuple = startTuple.times(startCoeff).plus(endTuple.times(endCoeff));
            xz0.put(y, newTuple);
         }

         NoiseColumn xz1 = (NoiseColumn)cube.get(x).get(subChunkSize - 1);

         for(int y = minHeight; y <= maxHeight; ++y) {
            NoiseTuple startTuple = ((NoiseColumn)cube.get(0).get(subChunkSize - 1)).get(y);
            NoiseTuple endTuple = ((NoiseColumn)cube.get(subChunkSize - 1).get(subChunkSize - 1)).get(y);
            NoiseTuple newTuple = startTuple.times(startCoeff).plus(endTuple.times(endCoeff));
            xz1.put(y, newTuple);
         }
      }

      for(int x = 0; x < subChunkSize; ++x) {
         for(int z = 1; z < subChunkSize - 1; ++z) {
            float startCoeff = BCSettings.START_COEFFS[z];
            float endCoeff = BCSettings.END_COEFFS[z];
            NoiseColumn xz = (NoiseColumn)cube.get(x).get(z);

            for(int y = minHeight; y <= maxHeight; ++y) {
               NoiseTuple startTuple = ((NoiseColumn)cube.get(x).get(0)).get(y);
               NoiseTuple endTuple = ((NoiseColumn)cube.get(x).get(subChunkSize - 1)).get(y);
               NoiseTuple newTuple = startTuple.times(startCoeff).plus(endTuple.times(endCoeff));
               xz.put(y, newTuple);
            }
         }
      }

      return cube;
   }

   public long getSeed() {
      return this.seed;
   }

   private void initializeNoiseGens(boolean isFastNoise) {
      if (isFastNoise) {
         for(int i = 0; i < this.numGenerators; ++i) {
            FastNoise noiseGen = new FastNoise();
            noiseGen.SetSeed((int)this.seed + 1111 * (i + 1));
            noiseGen.SetFractalType(this.noiseSettings.getFractalType());
            noiseGen.SetNoiseType(this.noiseSettings.getNoiseType());
            noiseGen.SetFractalOctaves(this.noiseSettings.getOctaves());
            noiseGen.SetFractalGain(this.noiseSettings.getGain());
            noiseGen.SetFrequency(this.noiseSettings.getFrequency());
            this.listNoiseGens.add(noiseGen);
         }
      } else {
         for(int i = 0; i < this.numGenerators; ++i) {
            OpenSimplex2S noiseGen = new OpenSimplex2S(this.seed + (long)(1111 * (i + 1)));
            noiseGen.setGain((double)this.noiseSettings.getGain());
            noiseGen.setOctaves(this.noiseSettings.getOctaves());
            noiseGen.setFrequency((double)this.noiseSettings.getFrequency());
            noiseGen.setLacunarity(2.0D);
            this.listNoiseGens.add(noiseGen);
         }
      }

   }
}

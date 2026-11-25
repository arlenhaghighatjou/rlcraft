package rtg.api.world.terrain;

import net.minecraft.block.BlockSnow;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ChunkPrimer;
import rtg.api.util.noise.CellularNoise;
import rtg.api.util.noise.ISimplexData2D;
import rtg.api.util.noise.SimplexData2D;
import rtg.api.util.noise.SimplexNoise;
import rtg.api.world.RTGWorld;
import rtg.api.world.terrain.heighteffect.VariableRuggednessEffect;

public abstract class TerrainBase {
   private static final float minimumOceanFloor = 20.01F;
   private static final float minimumDuneHeight = 21.0F;
   protected final float minDuneHeight;
   protected final float groundNoiseAmplitudeHills;
   protected final float groundVariation;
   protected final float rollingHillsMaxHeight;
   protected float base;
   protected float groundNoise;

   public TerrainBase() {
      this(68.0F);
   }

   public TerrainBase(float base) {
      this.base = base;
      this.minDuneHeight = 21.0F;
      this.groundVariation = 2.0F;
      this.groundNoise = this.base;
      this.groundNoiseAmplitudeHills = 6.0F;
      this.rollingHillsMaxHeight = 80.0F;
   }

   public static float blendedHillHeight(float simplex) {
      float result = simplex + 1.0F;
      result = result * result * result + 10.0F;
      result = (float)Math.pow((double)result, 0.33333333333333D);
      result = result / 0.46631F;
      result = result - 4.62021F;
      return result;
   }

   public static float blendedHillHeight(float simplex, float turnAt) {
      float adjusted = 1.0F - (1.0F - simplex) / (1.0F - turnAt);
      return blendedHillHeight(adjusted);
   }

   public static float above(float limited, float limit) {
      return limited > limit ? limited - limit : 0.0F;
   }

   public static float unsignedPower(float number, float power) {
      return number > 0.0F ? (float)Math.pow((double)number, (double)power) : -1.0F * (float)Math.pow((double)(-1.0F * number), (double)power);
   }

   public static float hills(float x, float y, float hillStrength, RTGWorld rtgWorld) {
      float m = rtgWorld.simplexInstance(0).noise2f(x / 150.0F, y / 150.0F);
      m = blendedHillHeight(m, 0.2F);
      float sm = rtgWorld.simplexInstance(2).noise2f(x / 55.0F, y / 55.0F);
      sm = blendedHillHeight(sm, 0.2F);
      sm = sm * sm * m;
      m = m + sm / 3.0F;
      return m * hillStrength;
   }

   public static float groundNoise(int x, int y, float amplitude, RTGWorld rtgWorld) {
      float h = blendedHillHeight(rtgWorld.simplexInstance(0).noise2f((float)x / 49.0F, (float)y / 49.0F), 0.2F) * amplitude;
      h = h + blendedHillHeight(rtgWorld.simplexInstance(1).noise2f((float)x / 23.0F, (float)y / 23.0F), 0.2F) * amplitude / 2.0F;
      h = h + blendedHillHeight(rtgWorld.simplexInstance(2).noise2f((float)x / 11.0F, (float)y / 11.0F), 0.2F) * amplitude / 4.0F;
      return h;
   }

   public static float groundNoise(float x, float y, float amplitude, RTGWorld rtgWorld) {
      float h = blendedHillHeight(rtgWorld.simplexInstance(0).noise2f(x / 49.0F, y / 49.0F), 0.2F) * amplitude;
      h = h + blendedHillHeight(rtgWorld.simplexInstance(1).noise2f(x / 23.0F, y / 23.0F), 0.2F) * amplitude / 2.0F;
      h = h + blendedHillHeight(rtgWorld.simplexInstance(2).noise2f(x / 11.0F, y / 11.0F), 0.2F) * amplitude / 4.0F;
      return h;
   }

   public static float getTerrainBase() {
      return 68.0F;
   }

   public static float getTerrainBase(float river) {
      return 62.0F + 6.0F * river;
   }

   public static float mountainCap(float m) {
      if (m > 160.0F) {
         m = 160.0F + (m - 160.0F) * 0.75F;
         if (m > 180.0F) {
            m = 180.0F + (m - 180.0F) * 0.75F;
         }
      }

      return m;
   }

   public static float riverized(float height, float river) {
      if (height < 62.45F) {
         return height;
      } else {
         float adjustment = (height - 62.45F) / 10.0F + 0.6F;
         river = bayesianAdjustment(river, adjustment);
         return 62.45F + (height - 62.45F) * river;
      }
   }

   public static float terrainBeach(int x, int y, RTGWorld rtgWorld, float river, float baseHeight) {
      return riverized(baseHeight + groundNoise(x, y, 4.0F, rtgWorld), river);
   }

   public static float terrainBryce(int x, int y, RTGWorld rtgWorld, float river, float height) {
      SimplexNoise simplex = rtgWorld.simplexInstance(0);
      float sn = simplex.noise2f((float)x / 2.0F, (float)y / 2.0F) * 0.5F + 0.5F;
      sn = (float)((double)sn + (double)simplex.noise2f((float)x, (float)y) * 0.2D + 0.2D);
      sn = sn + simplex.noise2f((float)x / 4.0F, (float)y / 4.0F) * 4.0F + 4.0F;
      sn = sn + simplex.noise2f((float)x / 8.0F, (float)y / 8.0F) * 2.0F + 2.0F;
      float n = height / sn * 2.0F;
      n = n + simplex.noise2f((float)x / 64.0F, (float)y / 64.0F) * 4.0F;
      n = sn < 6.0F ? n : 0.0F;
      return riverized(getTerrainBase() + n, river);
   }

   public static float terrainCanyon(int x, int y, RTGWorld rtgWorld, float river, float[] height, float border, float strength, int heightLength, boolean booRiver) {
      SimplexNoise simplex = rtgWorld.simplexInstance(0);
      float r = simplex.noise2f((float)x / 100.0F, (float)y / 100.0F) * 50.0F * river;
      r = r < -7.4F ? -7.4F : (r > 7.4F ? 7.4F : r);
      float b = (17.0F + r) * river;
      float hn = simplex.noise2f((float)x / 12.0F, (float)y / 12.0F) * 0.5F;
      float sb = 0.0F;
      if (b > 0.0F) {
         sb = b > 7.0F ? 7.0F : b;
         sb = hn * sb * river;
      }

      b = b + sb;
      float cTotal = 0.0F;

      for(int i = 0; i < heightLength; i += 2) {
         float cTemp = 0.0F;
         if (b > height[i] && border > 0.6F + height[i] * 0.015F + hn * 0.2F) {
            cTemp = b > height[i] + height[i + 1] ? height[i + 1] : b - height[i];
            cTemp = cTemp * strength;
         }

         cTotal += cTemp;
      }

      float bn = 0.0F;
      if (booRiver) {
         if (b < 5.0F) {
            bn = 5.0F - b;

            for(int i = 0; i < 3; ++i) {
               bn *= bn / 4.5F;
            }
         }
      } else if (b < 5.0F) {
         bn = (simplex.noise2f((float)x / 7.0F, (float)y / 7.0F) * 1.3F + simplex.noise2f((float)x / 15.0F, (float)y / 15.0F) * 2.0F) * (5.0F - b) * 0.2F;
      }

      b = b + (cTotal - bn);
      return getTerrainBase(river) + b;
   }

   public static float terrainFlatLakes(int x, int y, RTGWorld rtgWorld, float river, float baseHeight) {
      float ruggedNoise = rtgWorld.simplexInstance(1).noise2f((float)x / VariableRuggednessEffect.STANDARD_RUGGEDNESS_WAVELENGTH, (float)y / VariableRuggednessEffect.STANDARD_RUGGEDNESS_WAVELENGTH);
      ruggedNoise = blendedHillHeight(ruggedNoise);
      float h = groundNoise(x, y, 2.0F * (ruggedNoise + 1.0F), rtgWorld);
      return riverized(baseHeight + h, river);
   }

   public static float terrainForest(int x, int y, RTGWorld rtgWorld, float river, float baseHeight) {
      SimplexNoise simplex = rtgWorld.simplexInstance(0);
      double h = simplex.noise2d((double)x / 100.0D, (double)y / 100.0D) * 8.0D;
      h = h + simplex.noise2d((double)x / 30.0D, (double)y / 30.0D) * 4.0D;
      h = h + simplex.noise2d((double)x / 15.0D, (double)y / 15.0D) * 2.0D;
      h = h + simplex.noise2d((double)x / 7.0D, (double)y / 7.0D);
      return riverized(baseHeight + 20.0F + (float)h, river);
   }

   public static float terrainGrasslandFlats(int x, int y, RTGWorld rtgWorld, float river, float mPitch, float baseHeight) {
      SimplexNoise simplex = rtgWorld.simplexInstance(0);
      float h = simplex.noise2f((float)x / 100.0F, (float)y / 100.0F) * 7.0F;
      h = h + simplex.noise2f((float)x / 20.0F, (float)y / 20.0F) * 2.0F;
      float m = simplex.noise2f((float)x / 180.0F, (float)y / 180.0F) * 35.0F * river;
      m = m * (m / mPitch);
      float sm = blendedHillHeight(simplex.noise2f((float)x / 30.0F, (float)y / 30.0F)) * 8.0F;
      sm = sm * (m / 20.0F > 3.75F ? 3.75F : m / 20.0F);
      m = m + sm;
      return riverized(baseHeight + h + m, river);
   }

   public static float terrainGrasslandHills(int x, int y, RTGWorld rtgWorld, float river, float vWidth, float vHeight, float hWidth, float hHeight, float bHeight) {
      float h = rtgWorld.simplexInstance(0).noise2f((float)x / vWidth, (float)y / vWidth);
      h = blendedHillHeight(h, 0.3F);
      float m = rtgWorld.simplexInstance(1).noise2f((float)x / hWidth, (float)y / hWidth);
      m = blendedHillHeight(m, 0.3F) * h;
      m = m * m;
      h = h * vHeight * river;
      m = m * hHeight * river;
      h = h + groundNoise(x, y, 4.0F, rtgWorld);
      return riverized(bHeight + h, river) + m;
   }

   public static float terrainGrasslandMountains(int x, int y, RTGWorld rtgWorld, float river, float hFactor, float mFactor, float baseHeight) {
      SimplexNoise simplex0 = rtgWorld.simplexInstance(0);
      float h = simplex0.noise2f((float)x / 100.0F, (float)y / 100.0F) * hFactor;
      h = h + simplex0.noise2f((float)x / 20.0F, (float)y / 20.0F) * 2.0F;
      float m = simplex0.noise2f((float)x / 230.0F, (float)y / 230.0F) * mFactor * river;
      m = m * (m / 35.0F);
      m = m > 70.0F ? 70.0F + (m - 70.0F) / 2.5F : m;
      float c = rtgWorld.simplexInstance(4).noise3f((float)x / 30.0F, (float)y / 30.0F, 1.0F) * m * 0.3F;
      float sm = simplex0.noise2f((float)x / 30.0F, (float)y / 30.0F) * 8.0F + simplex0.noise2f((float)x / 8.0F, (float)y / 8.0F);
      sm = sm * (m / 20.0F > 2.5F ? 2.5F : m / 20.0F);
      m = m + sm;
      m = m + c;
      return riverized(baseHeight + h + m, river);
   }

   public static float terrainHighland(float x, float y, RTGWorld rtgWorld, float river, float start, float width, float height, float baseAdjust) {
      float h = rtgWorld.simplexInstance(0).noise2f(x / width, y / width) * height * river;
      h = h < start ? start + (h - start) / 4.5F : h;
      if (h < 0.0F) {
         h = 0.0F;
      }

      if (h > 0.0F) {
         float st = h * 1.5F > 15.0F ? 15.0F : h * 1.5F;
         h = h + rtgWorld.simplexInstance(4).noise3f(x / 70.0F, y / 70.0F, 1.0F) * st;
         h = h * river;
      }

      h = h + blendedHillHeight(rtgWorld.simplexInstance(0).noise2f(x / 20.0F, y / 20.0F), 0.0F) * 4.0F;
      h = h + blendedHillHeight(rtgWorld.simplexInstance(0).noise2f(x / 12.0F, y / 12.0F), 0.0F) * 2.0F;
      h = h + blendedHillHeight(rtgWorld.simplexInstance(0).noise2f(x / 5.0F, y / 5.0F), 0.0F) * 1.0F;
      if (h < 0.0F) {
         h /= 2.0F;
      }

      if (h < -3.0F) {
         h = (h + 3.0F) / 2.0F - 3.0F;
      }

      return getTerrainBase(river) + (h + baseAdjust) * river;
   }

   public static float terrainLonelyMountain(int x, int y, RTGWorld rtgWorld, float river, float strength, float width, float terrainHeight) {
      SimplexNoise simplex0 = rtgWorld.simplexInstance(0);
      float h = blendedHillHeight(simplex0.noise2f((float)x / 20.0F, (float)y / 20.0F), 0.0F) * 3.0F;
      h = h + blendedHillHeight(simplex0.noise2f((float)x / 7.0F, (float)y / 7.0F), 0.0F) * 1.3F;
      float m = simplex0.noise2f((float)x / width, (float)y / width) * strength * river;
      m = m * (m / 35.0F);
      m = m > 70.0F ? 70.0F + (m - 70.0F) / 2.5F : m;
      float st = m * 0.7F;
      st = st > 20.0F ? 20.0F : st;
      float c = rtgWorld.simplexInstance(4).noise3f((float)x / 30.0F, (float)y / 30.0F, 1.0F) * (5.0F + st);
      float sm = simplex0.noise2f((float)x / 30.0F, (float)y / 30.0F) * 8.0F + simplex0.noise2f((float)x / 8.0F, (float)y / 8.0F);
      sm = sm * ((m + 10.0F) / 20.0F > 2.5F ? 2.5F : (m + 10.0F) / 20.0F);
      m = m + sm;
      m = m + c;
      if (m > 90.0F) {
         m = 90.0F + (m - 90.0F) * 0.75F;
         if (m > 110.0F) {
            m = 110.0F + (m - 110.0F) * 0.75F;
         }
      }

      return riverized(terrainHeight + h + m, river);
   }

   public static float terrainMarsh(int x, int y, RTGWorld rtgWorld, float baseHeight, float river) {
      SimplexNoise simplex = rtgWorld.simplexInstance(0);
      float h = simplex.noise2f((float)x / 130.0F, (float)y / 130.0F) * 20.0F;
      h = h + simplex.noise2f((float)x / 12.0F, (float)y / 12.0F) * 2.0F;
      h = h + simplex.noise2f((float)x / 18.0F, (float)y / 18.0F) * 4.0F;
      h = h < 8.0F ? 0.0F : h - 8.0F;
      if (h == 0.0F) {
         h = h + simplex.noise2f((float)x / 20.0F, (float)y / 20.0F) + simplex.noise2f((float)x / 5.0F, (float)y / 5.0F);
         h = h * 2.0F;
      }

      return riverized(baseHeight + h, river);
   }

   public static float terrainOcean(int x, int y, RTGWorld rtgWorld, float river, float averageFloor) {
      SimplexNoise simplex = rtgWorld.simplexInstance(0);
      float h = simplex.noise2f((float)x / 300.0F, (float)y / 300.0F) * 8.0F * river;
      h = h + simplex.noise2f((float)x / 50.0F, (float)y / 50.0F) * 2.0F;
      h = h + simplex.noise2f((float)x / 15.0F, (float)y / 15.0F) * 1.0F;
      float floNoise = averageFloor + h;
      floNoise = floNoise < 20.01F ? 20.01F : floNoise;
      return floNoise;
   }

   public static float terrainOceanCanyon(int x, int y, RTGWorld rtgWorld, float river, float[] height, float border, float strength, int heightLength, boolean booRiver) {
      SimplexNoise simplex = rtgWorld.simplexInstance(0);
      river = river * 1.3F;
      river = river > 1.0F ? 1.0F : river;
      float r = simplex.noise2f((float)x / 100.0F, (float)y / 100.0F) * 50.0F;
      r = r < -7.4F ? -7.4F : (r > 7.4F ? 7.4F : r);
      float b = (17.0F + r) * river;
      float hn = simplex.noise2f((float)x / 12.0F, (float)y / 12.0F) * 0.5F;
      float sb = 0.0F;
      if (b > 0.0F) {
         sb = b > 7.0F ? 7.0F : b;
         sb = hn * sb;
      }

      b = b + sb;
      float cTotal = 0.0F;

      for(int i = 0; i < heightLength; i += 2) {
         float cTemp = 0.0F;
         if (b > height[i] && border > 0.6F + height[i] * 0.015F + hn * 0.2F) {
            cTemp = b > height[i] + height[i + 1] ? height[i + 1] : b - height[i];
            cTemp = cTemp * strength;
         }

         cTotal += cTemp;
      }

      float bn = 0.0F;
      if (booRiver) {
         if (b < 5.0F) {
            bn = 5.0F - b;

            for(int i = 0; i < 3; ++i) {
               bn *= bn / 4.5F;
            }
         }
      } else if (b < 5.0F) {
         bn = (simplex.noise2f((float)x / 7.0F, (float)y / 7.0F) * 1.3F + simplex.noise2f((float)x / 15.0F, (float)y / 15.0F) * 2.0F) * (5.0F - b) * 0.2F;
      }

      b = b + (cTotal - bn);
      float floNoise = 30.0F + b;
      floNoise = floNoise < 20.01F ? 20.01F : floNoise;
      return floNoise;
   }

   public static float terrainPlains(int x, int y, RTGWorld rtgWorld, float river, float stPitch, float stFactor, float hPitch, float hDivisor, float baseHeight) {
      SimplexNoise simplex = rtgWorld.simplexInstance(0);
      float st = (simplex.noise2f((float)x / stPitch, (float)y / stPitch) + 0.38F) * stFactor * river;
      st = st < 0.2F ? 0.2F : st;
      float h = simplex.noise2f((float)x / hPitch, (float)y / hPitch) * st * 2.0F;
      h = h > 0.0F ? -h : h;
      h = h + st;
      h = h * (h / hDivisor);
      h = h + st;
      float floNoise = riverized(baseHeight + h, river);
      return floNoise;
   }

   public static float terrainPlateau(float x, float y, RTGWorld rtgWorld, float river, float[] height, float border, float strength, int heightLength, float selectorWaveLength, boolean isM) {
      SimplexNoise simplex = rtgWorld.simplexInstance(0);
      river = river > 1.0F ? 1.0F : river;
      float border2 = border * 4.0F - 2.5F;
      border2 = border2 > 1.0F ? 1.0F : (border2 < 0.0F ? 0.0F : border2);
      float b = simplex.noise2f(x / 40.0F, y / 40.0F) * 1.5F;
      float sn = simplex.noise2f(x / selectorWaveLength, y / selectorWaveLength) * 0.5F + 0.5F;
      sn = sn * border2;
      sn = sn * river;
      sn = sn + simplex.noise2f(x / 4.0F, y / 4.0F) * 0.01F + 0.01F;
      sn = sn + simplex.noise2f(x / 2.0F, y / 2.0F) * 0.01F + 0.01F;

      for(int i = 0; i < heightLength; i += 2) {
         float n = (sn - height[i + 1]) / (1.0F - height[i + 1]);
         n = n * strength;
         n = n < 0.0F ? 0.0F : (n > 1.0F ? 1.0F : n);
         float hn = height[i] * 0.5F * (sn * 2.0F - 0.4F);
         hn = hn < 0.0F ? 0.0F : hn;
         float stepUp = 0.0F;
         if (sn > height[i + 1]) {
            stepUp += height[i] * n;
            if (isM) {
               stepUp = stepUp + simplex.noise2f(x / 20.0F, y / 20.0F) * 3.0F * n;
               stepUp = stepUp + simplex.noise2f(x / 12.0F, y / 12.0F) * 2.0F * n;
               stepUp = stepUp + simplex.noise2f(x / 5.0F, y / 5.0F) * 1.0F * n;
            }
         }

         if (i == 0 && stepUp < hn) {
            b += hn;
         }

         stepUp = stepUp < 0.0F ? 0.0F : stepUp;
         b += stepUp;
      }

      if (isM) {
         b += simplex.noise2f(x / 12.0F, y / 12.0F) * sn;
      }

      b = b / border;
      return riverized(getTerrainBase(), river) + b;
   }

   public static float terrainPolar(float x, float y, RTGWorld rtgWorld, float river, float stPitch, float stFactor, float hPitch, float hDivisor, float baseHeight) {
      SimplexNoise simplex = rtgWorld.simplexInstance(0);
      float st = (simplex.noise2f(x / stPitch, y / stPitch) + 0.38F) * stFactor * river;
      st = st < 0.1F ? 0.1F : st;
      float h = simplex.noise2f(x / hPitch, y / hPitch) * st * 2.0F;
      h = h > 0.0F ? -h : h;
      h = h + st;
      h = h * (h / hDivisor);
      h = h + st;
      float floNoise = riverized(baseHeight + h, river);
      return floNoise;
   }

   public static float terrainRollingHills(int x, int y, RTGWorld rtgWorld, float river, float hillStrength, float addedHeight, float groundNoiseAmplitudeHills, float lift) {
      float groundNoise = groundNoise(x, y, groundNoiseAmplitudeHills, rtgWorld);
      float m = hills((float)x, (float)y, hillStrength, rtgWorld);
      float floNoise = addedHeight + groundNoise + m;
      return riverized(floNoise + lift, river);
   }

   public static float terrainRollingHills(int x, int y, RTGWorld rtgWorld, float river, float hillStrength, float groundNoiseAmplitudeHills, float baseHeight) {
      float groundNoise = groundNoise(x, y, groundNoiseAmplitudeHills, rtgWorld);
      float m = hills((float)x, (float)y, hillStrength, rtgWorld);
      float floNoise = groundNoise + m;
      return riverized(floNoise + baseHeight, river);
   }

   public static float terrainSwampMountain(int x, int y, RTGWorld rtgWorld, float river, float width, float heigth, float hMax, float hDivisor, float baseHeight) {
      SimplexNoise simplex = rtgWorld.simplexInstance(0);
      float h = simplex.noise2f((float)x / width, (float)y / width) * heigth * river;
      h = h * (h / hDivisor);
      h = h > hMax ? hMax : h;
      if (h < 14.0F) {
         h += simplex.noise2f((float)x / 25.0F, (float)y / 25.0F) * (14.0F - h) * 0.8F;
      }

      if (h < 6.0F) {
         h = 6.0F - (6.0F - h) * 0.07F + simplex.noise2f((float)x / 20.0F, (float)y / 20.0F) + simplex.noise2f((float)x / 5.0F, (float)y / 5.0F);
      }

      if (h > 10.0F) {
         float d = (h - 10.0F) / 2.0F > 8.0F ? 8.0F : (h - 10.0F) / 2.0F;
         h = h + simplex.noise2f((float)x / 35.0F, (float)y / 35.0F) * d;
         h = h + simplex.noise2f((float)x / 60.0F, (float)y / 60.0F) * d * 0.5F;
         if (h > 35.0F) {
            float d2 = (h - 35.0F) / 1.5F > 30.0F ? 30.0F : (h - 35.0F) / 1.5F;
            h += rtgWorld.simplexInstance(4).noise3f((float)x / 25.0F, (float)y / 25.0F, 1.0F) * d2;
         }
      }

      if (h > 2.0F) {
         float d = (h - 2.0F) / 2.0F > 4.0F ? 4.0F : (h - 2.0F) / 2.0F;
         h = h + simplex.noise2f((float)x / 28.0F, (float)y / 28.0F) * d;
         h = h + simplex.noise2f((float)x / 18.0F, (float)y / 18.0F) * (d / 2.0F);
         h = h + simplex.noise2f((float)x / 8.0F, (float)y / 8.0F) * (d / 2.0F);
      }

      return riverized(h + baseHeight, river);
   }

   public static float terrainVolcano(int x, int y, RTGWorld rtgWorld, float border, float baseHeight) {
      SimplexNoise simplex = rtgWorld.simplexInstance(0);
      CellularNoise cellularNoise = rtgWorld.cellularInstance(0);
      float st = 15.0F - (float)(cellularNoise.eval2D((double)x / 500.0D, (double)y / 500.0D).getShortestDistance() * 42.0D) + simplex.noise2f((float)x / 30.0F, (float)y / 30.0F) * 2.0F;
      float h = st < 0.0F ? 0.0F : st;
      h = h < 0.0F ? 0.0F : h;
      h = h + h * 0.4F * h * 0.4F * 2.0F;
      if (h > 10.0F) {
         float d2 = (h - 10.0F) / 1.5F > 30.0F ? 30.0F : (h - 10.0F) / 1.5F;
         h = (float)((double)h + cellularNoise.eval2D((double)x / 25.0D, (double)y / 25.0D).getShortestDistance() * (double)d2);
      }

      h = h + simplex.noise2f((float)x / 18.0F, (float)y / 18.0F) * 3.0F;
      h = h + simplex.noise2f((float)x / 8.0F, (float)y / 8.0F) * 2.0F;
      return baseHeight + h * border;
   }

   public static float getRiverStrength(BlockPos blockPos, RTGWorld rtgWorld) {
      int worldX = blockPos.getX();
      int worldZ = blockPos.getZ();
      double pX = (double)worldX;
      double pZ = (double)worldZ;
      ISimplexData2D jitterData = SimplexData2D.newDisk();
      rtgWorld.simplexInstance(1).multiEval2D((double)((float)worldX) / 240.0D, (double)((float)worldZ) / 240.0D, jitterData);
      pX = pX + jitterData.getDeltaX() * rtgWorld.getRiverLargeBendSize();
      pZ = pZ + jitterData.getDeltaY() * rtgWorld.getRiverLargeBendSize();
      rtgWorld.simplexInstance(2).multiEval2D((double)((float)worldX) / 80.0D, (double)((float)worldZ) / 80.0D, jitterData);
      pX = pX + jitterData.getDeltaX() * rtgWorld.getRiverSmallBendSize();
      pZ = pZ + jitterData.getDeltaY() * rtgWorld.getRiverSmallBendSize();
      pX = pX / rtgWorld.getRiverSeparation();
      pZ = pZ / rtgWorld.getRiverSeparation();
      double riverFactor = rtgWorld.cellularInstance(0).eval2D(pX, pZ).interiorValue();
      riverFactor = (double)bayesianAdjustment((float)riverFactor, 0.5F);
      return riverFactor > rtgWorld.getRiverValleyLevel() ? 0.0F : (float)(riverFactor / rtgWorld.getRiverValleyLevel() - 1.0D);
   }

   public static float calcCliff(int x, int z, float[] noise) {
      float cliff = 0.0F;
      if (x > 0) {
         cliff = Math.max(cliff, Math.abs(noise[x * 16 + z] - noise[(x - 1) * 16 + z]));
      }

      if (z > 0) {
         cliff = Math.max(cliff, Math.abs(noise[x * 16 + z] - noise[x * 16 + z - 1]));
      }

      if (x < 15) {
         cliff = Math.max(cliff, Math.abs(noise[x * 16 + z] - noise[(x + 1) * 16 + z]));
      }

      if (z < 15) {
         cliff = Math.max(cliff, Math.abs(noise[x * 16 + z] - noise[x * 16 + z + 1]));
      }

      return cliff;
   }

   public static void calcSnowHeight(int x, int y, int z, ChunkPrimer primer, float[] noise) {
      if (y < 254) {
         byte h = (byte)((int)((noise[x * 16 + z] - (float)((int)noise[x * 16 + z])) * 8.0F));
         if (h > 7) {
            primer.setBlockState(x, y + 2, z, Blocks.SNOW_LAYER.getDefaultState());
            primer.setBlockState(x, y + 1, z, Blocks.SNOW_LAYER.getDefaultState().withProperty(BlockSnow.LAYERS, Integer.valueOf(7)));
         } else if (h > 0) {
            primer.setBlockState(x, y + 1, z, Blocks.SNOW_LAYER.getDefaultState().withProperty(BlockSnow.LAYERS, Integer.valueOf(h)));
         }
      }

   }

   public static float bayesianAdjustment(float probability, float multiplier) {
      if (probability >= 1.0F) {
         return probability;
      } else if (probability <= 0.0F) {
         return probability;
      } else {
         float newConfidence = probability * multiplier / (1.0F - probability);
         return newConfidence / (1.0F + newConfidence);
      }
   }

   public abstract float generateNoise(RTGWorld var1, int var2, int var3, float var4, float var5);
}

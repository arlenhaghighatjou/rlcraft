package rtg.api.world.biome;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import javax.annotation.Nonnull;
import net.minecraft.init.Biomes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import rtg.RTG;
import rtg.RTGConfig;
import rtg.api.RTGAPI;
import rtg.api.config.BiomeConfig;
import rtg.api.util.noise.ISimplexData2D;
import rtg.api.util.noise.SimplexData2D;
import rtg.api.util.noise.VoronoiResult;
import rtg.api.world.RTGWorld;
import rtg.api.world.deco.DecoBase;
import rtg.api.world.gen.feature.tree.rtg.TreeRTG;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.surface.SurfaceRiverOasis;
import rtg.api.world.terrain.TerrainBase;
import rtg.util.ModCompat;

public abstract class RealisticBiomeBase implements IRealisticBiome {
   private static final String BIOME_CONFIG_SUBDIR = "biomes";
   private final Biome baseBiome;
   private final ResourceLocation baseBiomeResLoc;
   private final int baseBiomeId;
   private final RealisticBiomeBase.RiverType riverType;
   private final RealisticBiomeBase.BeachType beachType;
   private final BiomeConfig config;
   private final TerrainBase terrain;
   private final SurfaceBase surface;
   private final SurfaceBase surfaceRiver;
   private Collection<DecoBase> decos;
   /** @deprecated */
   @Deprecated
   private Collection<TreeRTG> rtgTrees;

   public RealisticBiomeBase(@Nonnull Biome baseBiome) {
      this(baseBiome, RealisticBiomeBase.RiverType.NORMAL, RealisticBiomeBase.BeachType.NORMAL);
   }

   public RealisticBiomeBase(@Nonnull Biome baseBiome, @Nonnull RealisticBiomeBase.RiverType riverType) {
      this(baseBiome, riverType, RealisticBiomeBase.BeachType.NORMAL);
   }

   public RealisticBiomeBase(@Nonnull Biome baseBiome, @Nonnull RealisticBiomeBase.BeachType beachType) {
      this(baseBiome, RealisticBiomeBase.RiverType.NORMAL, beachType);
   }

   public RealisticBiomeBase(@Nonnull Biome baseBiome, @Nonnull RealisticBiomeBase.RiverType riverType, @Nonnull RealisticBiomeBase.BeachType beachType) {
      ResourceLocation resloc = baseBiome.getRegistryName();
      if (resloc == null) {
         throw new IllegalStateException(String.format("Biome with ID: %s, of class: %s, does not have a registry name set.", Biome.getIdForBiome(baseBiome), baseBiome.getClass().getName()));
      } else {
         this.baseBiome = baseBiome;
         this.baseBiomeResLoc = resloc;
         this.baseBiomeId = Biome.getIdForBiome(baseBiome);
         this.riverType = riverType;
         this.beachType = beachType;
         this.config = new BiomeConfig(this.getConfigFile());
         this.terrain = this.initTerrain();
         this.surface = this.initSurface();
         this.surfaceRiver = new SurfaceRiverOasis(this.config);
         this.decos = new ArrayList();
         this.rtgTrees = new ArrayList();
         this.initConfig();
         this.getConfig().loadConfig();
         this.initDecos();
         this.overrideDecorations();
      }
   }

   public BiomeConfig getConfig() {
      return this.config;
   }

   public final Biome baseBiome() {
      return this.baseBiome;
   }

   public RealisticBiomeBase.RiverType getRiverType() {
      return this.riverType;
   }

   public RealisticBiomeBase.BeachType getBeachType() {
      return this.beachType;
   }

   public Biome preferredBeach() {
      return this.beachType.getBiome();
   }

   public IRealisticBiome getRiverBiome() {
      return this.riverType.getRTGBiome();
   }

   public IRealisticBiome getBeachBiome() {
      IRealisticBiome rbb = RTGAPI.getRTGBiome(Biome.getIdForBiome(this.preferredBeach()));
      int configBiomeId = this.getConfig().BEACH_BIOME.get();
      if (configBiomeId > -1) {
         rbb = RTGAPI.getRTGBiome(configBiomeId);
      }

      return rbb;
   }

   public void overrideDecorations() {
   }

   public Collection<DecoBase> getDecos() {
      return this.decos;
   }

   public Collection<TreeRTG> getTrees() {
      return this.rtgTrees;
   }

   public ResourceLocation baseBiomeResLoc() {
      return this.baseBiomeResLoc;
   }

   public int baseBiomeId() {
      return this.baseBiomeId;
   }

   public float rNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
      if (!this.getConfig().ALLOW_RIVERS.get()) {
         float borderForRiver = border * 2.0F;
         if (borderForRiver > 1.0F) {
            borderForRiver = 1.0F;
         }

         river = 1.0F - (1.0F - borderForRiver) * (1.0F - river);
         return this.terrain.generateNoise(rtgWorld, x, y, border, river);
      } else {
         float lakeStrength = this.lakePressure(rtgWorld, x, y, border, rtgWorld.getLakeFrequency(), rtgWorld.getLakeBendSizeLarge(), rtgWorld.getLakeBendSizeMedium(), rtgWorld.getLakeBendSizeSmall());
         float lakeFlattening = this.lakeFlattening(lakeStrength, rtgWorld.getLakeShoreLevel(), rtgWorld.getLakeDepressionLevel());
         if (river < 1.0F && lakeFlattening < 1.0F) {
            river = (1.0F - river) / river + (1.0F - lakeFlattening) / lakeFlattening;
            river = 1.0F / (river + 1.0F);
         } else if (lakeFlattening < river) {
            river = lakeFlattening;
         }

         river = 1.0F - river;
         river = river * river / (river + 0.05F) * 1.05F;
         river = 1.0F - river;
         float riverFlattening = river * 1.1034483F - 0.10344828F;
         if (riverFlattening < 0.0F) {
            riverFlattening = 0.0F;
         }

         float terrainNoise = this.terrain.generateNoise(rtgWorld, x, y, border, riverFlattening);
         return this.erodedNoise(rtgWorld, x, y, river, border, terrainNoise);
      }
   }

   public float erodedNoise(RTGWorld rtgWorld, int x, int y, float river, float border, float biomeHeight) {
      float riverFlattening = 1.0F - river;
      riverFlattening = riverFlattening - 0.90625F;
      if (riverFlattening < 0.0F) {
         return biomeHeight;
      } else {
         riverFlattening = riverFlattening / 0.09375F;
         float r = 1.0F - riverFlattening;
         if (r < 1.0F && biomeHeight > 55.0F) {
            float irregularity = rtgWorld.simplexInstance(0).noise2f((float)x / 12.0F, (float)y / 12.0F) * 2.0F + rtgWorld.simplexInstance(0).noise2f((float)x / 8.0F, (float)y / 8.0F);
            irregularity = irregularity * (1.0F + r);
            return biomeHeight * r + (55.0F + irregularity) * 1.0F * (1.0F - r);
         } else {
            return biomeHeight;
         }
      }
   }

   public float lakePressure(RTGWorld rtgWorld, int x, int y, float border, float lakeInterval, float largeBendSize, float mediumBendSize, float smallBendSize) {
      if (!this.getConfig().ALLOW_SCENIC_LAKES.get()) {
         return 1.0F;
      } else {
         double pX = (double)x;
         double pY = (double)y;
         ISimplexData2D jitterData = SimplexData2D.newDisk();
         rtgWorld.simplexInstance(1).multiEval2D((double)x / 240.0D, (double)y / 240.0D, jitterData);
         pX = pX + jitterData.getDeltaX() * (double)largeBendSize;
         pY = pY + jitterData.getDeltaY() * (double)largeBendSize;
         rtgWorld.simplexInstance(0).multiEval2D((double)x / 80.0D, (double)y / 80.0D, jitterData);
         pX = pX + jitterData.getDeltaX() * (double)mediumBendSize;
         pY = pY + jitterData.getDeltaY() * (double)mediumBendSize;
         rtgWorld.simplexInstance(4).multiEval2D((double)x / 30.0D, (double)y / 30.0D, jitterData);
         pX = pX + jitterData.getDeltaX() * (double)smallBendSize;
         pY = pY + jitterData.getDeltaY() * (double)smallBendSize;
         VoronoiResult lakeResults = rtgWorld.cellularInstance(0).eval2D(pX / (double)lakeInterval, pY / (double)lakeInterval);
         return (float)(1.0D - lakeResults.interiorValue());
      }
   }

   public float lakeFlattening(float pressure, float shoreLevel, float topLevel) {
      if (pressure > topLevel) {
         return 1.0F;
      } else if (pressure < shoreLevel) {
         return pressure / shoreLevel * 0.09375F;
      } else {
         float proportion = (pressure - shoreLevel) / (topLevel - shoreLevel);
         return 0.09375F + proportion * 0.90625F;
      }
   }

   public void rReplace(ChunkPrimer primer, BlockPos blockPos, int x, int y, int depth, RTGWorld rtgWorld, float[] noise, float river, Biome[] base) {
      this.rReplace(primer, blockPos.getX(), blockPos.getZ(), x, y, depth, rtgWorld, noise, river, base);
   }

   public void rReplace(ChunkPrimer primer, int i, int j, int x, int y, int depth, RTGWorld rtgWorld, float[] noise, float river, Biome[] base) {
      if (!RTG.surfacesDisabled() && !this.getConfig().DISABLE_RTG_SURFACES.get()) {
         float riverRegion = !this.getConfig().ALLOW_RIVERS.get() ? 0.0F : river;
         this.surface.paintTerrain(primer, i, j, x, y, depth, rtgWorld, noise, riverRegion, base);
      }
   }

   protected void rReplaceWithRiver(ChunkPrimer primer, int i, int j, int x, int y, int depth, RTGWorld rtgWorld, float[] noise, float river, Biome[] base) {
      if (!RTG.surfacesDisabled() && !this.getConfig().DISABLE_RTG_SURFACES.get()) {
         float riverRegion = !this.getConfig().ALLOW_RIVERS.get() ? 0.0F : river;
         this.surface.paintTerrain(primer, i, j, x, y, depth, rtgWorld, noise, riverRegion, base);
         if (RTGConfig.lushRiverbanksInDesert()) {
            this.surfaceRiver.paintTerrain(primer, i, j, x, y, depth, rtgWorld, noise, riverRegion, base);
         }

      }
   }

   public TerrainBase terrain() {
      return this.terrain;
   }

   public SurfaceBase surface() {
      return this.surface;
   }

   public double waterLakeMult() {
      return (double)this.getConfig().SURFACE_WATER_LAKE_MULT.get();
   }

   public double lavaLakeMult() {
      return (double)this.getConfig().SURFACE_LAVA_LAKE_MULT.get();
   }

   private File getConfigFile() {
      return RTGAPI.getConfigPath().resolve("biomes").resolve(ModCompat.Mods.get(this.baseBiomeResLoc().getResourceDomain()).getPrettyName()).resolve(this.baseBiomeResLoc().getResourcePath() + ".cfg").toFile();
   }

   protected RealisticBiomeBase.BeachType determineBeachType() {
      if (this.baseBiome().getDefaultTemperature() > 0.05F && !BiomeDictionary.hasType(this.baseBiome(), Type.SNOWY)) {
         float height = this.baseBiome().getBaseHeight() + this.baseBiome().getHeightVariation() * 2.0F;
         return height <= 1.5F && !this.isTaigaBiome(this.baseBiome()) ? RealisticBiomeBase.BeachType.NORMAL : RealisticBiomeBase.BeachType.STONE;
      } else {
         return RealisticBiomeBase.BeachType.COLD;
      }
   }

   private boolean isTaigaBiome(Biome biome) {
      return BiomeDictionary.hasType(biome, Type.COLD) && BiomeDictionary.hasType(biome, Type.CONIFEROUS) && BiomeDictionary.hasType(biome, Type.FOREST);
   }

   public static enum BeachType {
      NORMAL,
      STONE,
      COLD;

      private IRealisticBiome rtgBiome;
      private boolean locked = false;

      private BeachType() {
      }

      public Biome getBiome() {
         return this == STONE ? Biomes.STONE_BEACH : (this == COLD ? Biomes.COLD_BEACH : Biomes.BEACH);
      }

      public IRealisticBiome getRTGBiome() {
         return this.rtgBiome;
      }

      public IRealisticBiome setRTGBiome(IRealisticBiome rtgBiome) {
         if (!this.locked) {
            this.rtgBiome = rtgBiome;
            this.locked = true;
         }

         return rtgBiome;
      }

      public RealisticBiomeBase.BeachType getTypeFromBiome(Biome beachBiome) {
         return beachBiome == Biomes.STONE_BEACH ? STONE : (beachBiome == Biomes.COLD_BEACH ? COLD : NORMAL);
      }
   }

   public static enum RiverType {
      NORMAL,
      FROZEN;

      private IRealisticBiome rtgBiome;
      private boolean locked = false;

      private RiverType() {
      }

      public static RealisticBiomeBase.RiverType getTypeFromBiome(Biome riverBiome) {
         return riverBiome == Biomes.FROZEN_RIVER ? FROZEN : NORMAL;
      }

      public Biome getBiome() {
         return this == NORMAL ? Biomes.RIVER : Biomes.FROZEN_RIVER;
      }

      public IRealisticBiome getRTGBiome() {
         return this.rtgBiome;
      }

      public IRealisticBiome setRTGBiome(IRealisticBiome rtgBiome) {
         if (!this.locked) {
            this.rtgBiome = rtgBiome;
            this.locked = true;
         }

         return rtgBiome;
      }
   }
}

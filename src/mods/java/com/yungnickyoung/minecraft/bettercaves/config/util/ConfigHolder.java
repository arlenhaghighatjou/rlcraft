package com.yungnickyoung.minecraft.bettercaves.config.util;

import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import com.yungnickyoung.minecraft.bettercaves.enums.RegionSize;
import com.yungnickyoung.minecraft.bettercaves.noise.FastNoise;
import java.util.HashMap;
import java.util.Map;

public class ConfigHolder {
   public Map<String, ConfigHolder.ConfigOption<?>> properties = new HashMap();
   public ConfigHolder.ConfigOption<RegionSize> caveRegionSize;
   public ConfigHolder.ConfigOption<Float> caveRegionCustomSize;
   public ConfigHolder.ConfigOption<Float> caveSpawnChance;
   public ConfigHolder.ConfigOption<Integer> cubicCaveBottom;
   public ConfigHolder.ConfigOption<Integer> cubicCaveTop;
   public ConfigHolder.ConfigOption<Integer> cubicCaveSurfaceCutoffDepth;
   public ConfigHolder.ConfigOption<Float> cubicCaveYCompression;
   public ConfigHolder.ConfigOption<Float> cubicCaveXZCompression;
   public ConfigHolder.ConfigOption<Integer> cubicCavePriority;
   public ConfigHolder.ConfigOption<Integer> simplexCaveBottom;
   public ConfigHolder.ConfigOption<Integer> simplexCaveTop;
   public ConfigHolder.ConfigOption<Integer> simplexCaveSurfaceCutoffDepth;
   public ConfigHolder.ConfigOption<Float> simplexCaveYCompression;
   public ConfigHolder.ConfigOption<Float> simplexCaveXZCompression;
   public ConfigHolder.ConfigOption<Integer> simplexCavePriority;
   public ConfigHolder.ConfigOption<Boolean> isSurfaceCavesEnabled;
   public ConfigHolder.ConfigOption<Integer> surfaceCaveBottom;
   public ConfigHolder.ConfigOption<Integer> surfaceCaveTop;
   public ConfigHolder.ConfigOption<Integer> surfaceCaveDensity;
   public ConfigHolder.ConfigOption<Integer> vanillaCaveBottom;
   public ConfigHolder.ConfigOption<Integer> vanillaCaveTop;
   public ConfigHolder.ConfigOption<Integer> vanillaCaveDensity;
   public ConfigHolder.ConfigOption<Integer> vanillaCavePriority;
   public ConfigHolder.ConfigOption<RegionSize> cavernRegionSize;
   public ConfigHolder.ConfigOption<Float> cavernRegionCustomSize;
   public ConfigHolder.ConfigOption<Float> cavernSpawnChance;
   public ConfigHolder.ConfigOption<Integer> liquidCavernBottom;
   public ConfigHolder.ConfigOption<Integer> liquidCavernTop;
   public ConfigHolder.ConfigOption<Float> liquidCavernYCompression;
   public ConfigHolder.ConfigOption<Float> liquidCavernXZCompression;
   public ConfigHolder.ConfigOption<Integer> liquidCavernPriority;
   public ConfigHolder.ConfigOption<Integer> flooredCavernBottom;
   public ConfigHolder.ConfigOption<Integer> flooredCavernTop;
   public ConfigHolder.ConfigOption<Float> flooredCavernYCompression;
   public ConfigHolder.ConfigOption<Float> flooredCavernXZCompression;
   public ConfigHolder.ConfigOption<Integer> flooredCavernPriority;
   public ConfigHolder.ConfigOption<Float> waterRegionSpawnChance;
   public ConfigHolder.ConfigOption<RegionSize> waterRegionSize;
   public ConfigHolder.ConfigOption<Float> waterRegionCustomSize;
   public ConfigHolder.ConfigOption<Boolean> enableVanillaRavines;
   public ConfigHolder.ConfigOption<Boolean> enableFloodedRavines;
   public ConfigHolder.ConfigOption<String> lavaBlock;
   public ConfigHolder.ConfigOption<String> waterBlock;
   public ConfigHolder.ConfigOption<Integer> liquidAltitude;
   public ConfigHolder.ConfigOption<Boolean> replaceFloatingGravel;
   public ConfigHolder.ConfigOption<Boolean> overrideSurfaceDetection;
   public ConfigHolder.ConfigOption<Boolean> enableFloodedUnderground;
   public ConfigHolder.ConfigOption<Boolean> flattenBedrock;
   public ConfigHolder.ConfigOption<Integer> bedrockWidth;
   public ConfigHolder.ConfigOption<Boolean> debugVisualizer;
   public ConfigHolder.ConfigOption<Float> cubicCaveNoiseThreshold;
   public ConfigHolder.ConfigOption<Integer> cubicCaveFractalOctaves;
   public ConfigHolder.ConfigOption<Float> cubicCaveFractalGain;
   public ConfigHolder.ConfigOption<Float> cubicCaveFractalFrequency;
   public ConfigHolder.ConfigOption<Integer> cubicCaveNumGenerators;
   public ConfigHolder.ConfigOption<Boolean> cubicCaveEnableVerticalAdjustment;
   public ConfigHolder.ConfigOption<Float> cubicCaveYAdjustF1;
   public ConfigHolder.ConfigOption<Float> cubicCaveYAdjustF2;
   public ConfigHolder.ConfigOption<FastNoise.NoiseType> cubicCaveNoiseType;
   public ConfigHolder.ConfigOption<Float> simplexCaveNoiseThreshold;
   public ConfigHolder.ConfigOption<Integer> simplexCaveFractalOctaves;
   public ConfigHolder.ConfigOption<Float> simplexCaveFractalGain;
   public ConfigHolder.ConfigOption<Float> simplexCaveFractalFrequency;
   public ConfigHolder.ConfigOption<Integer> simplexCaveNumGenerators;
   public ConfigHolder.ConfigOption<Boolean> simplexCaveEnableVerticalAdjustment;
   public ConfigHolder.ConfigOption<Float> simplexCaveYAdjustF1;
   public ConfigHolder.ConfigOption<Float> simplexCaveYAdjustF2;
   public ConfigHolder.ConfigOption<FastNoise.NoiseType> simplexCaveNoiseType;
   public ConfigHolder.ConfigOption<Float> liquidCavernNoiseThreshold;
   public ConfigHolder.ConfigOption<Integer> liquidCavernFractalOctaves;
   public ConfigHolder.ConfigOption<Float> liquidCavernFractalGain;
   public ConfigHolder.ConfigOption<Float> liquidCavernFractalFrequency;
   public ConfigHolder.ConfigOption<Integer> liquidCavernNumGenerators;
   public ConfigHolder.ConfigOption<FastNoise.NoiseType> liquidCavernNoiseType;
   public ConfigHolder.ConfigOption<Float> flooredCavernNoiseThreshold;
   public ConfigHolder.ConfigOption<Integer> flooredCavernFractalOctaves;
   public ConfigHolder.ConfigOption<Float> flooredCavernFractalGain;
   public ConfigHolder.ConfigOption<Float> flooredCavernFractalFrequency;
   public ConfigHolder.ConfigOption<Integer> flooredCavernNumGenerators;
   public ConfigHolder.ConfigOption<FastNoise.NoiseType> flooredCavernNoiseType;

   public ConfigHolder() {
      this.caveRegionSize = (new ConfigHolder.ConfigOption<RegionSize>("Cave Region Size", Configuration.caveSettings.caves.caveRegionSize)).setCategory("general.underground generation.caves").addToMap(this.properties);
      this.caveRegionCustomSize = (new ConfigHolder.ConfigOption<Float>("Cave Region Size Custom Value", Configuration.caveSettings.caves.customRegionSize)).setCategory("general.underground generation.caves").addToMap(this.properties);
      this.caveSpawnChance = (new ConfigHolder.ConfigOption<Float>("Cave Spawn Chance", Configuration.caveSettings.caves.caveSpawnChance)).setCategory("general.underground generation.caves").addToMap(this.properties);
      this.cubicCaveBottom = (new ConfigHolder.ConfigOption<Integer>("Type 1 Cave Minimum Altitude", Configuration.caveSettings.caves.cubicCave.caveBottom)).setCategory("general.underground generation.caves.type 1 caves").addToMap(this.properties);
      this.cubicCaveTop = (new ConfigHolder.ConfigOption<Integer>("Type 1 Cave Maximum Altitude", Configuration.caveSettings.caves.cubicCave.caveTop)).setCategory("general.underground generation.caves.type 1 caves").addToMap(this.properties);
      this.cubicCaveSurfaceCutoffDepth = (new ConfigHolder.ConfigOption<Integer>("Type 1 Cave Surface Cutoff Depth", Configuration.caveSettings.caves.cubicCave.caveSurfaceCutoff)).setCategory("general.underground generation.caves.type 1 caves").addToMap(this.properties);
      this.cubicCaveYCompression = (new ConfigHolder.ConfigOption<Float>("Compression - Vertical", Configuration.caveSettings.caves.cubicCave.yCompression)).setCategory("general.underground generation.caves.type 1 caves").addToMap(this.properties);
      this.cubicCaveXZCompression = (new ConfigHolder.ConfigOption<Float>("Compression - Horizontal", Configuration.caveSettings.caves.cubicCave.xzCompression)).setCategory("general.underground generation.caves.type 1 caves").addToMap(this.properties);
      this.cubicCavePriority = (new ConfigHolder.ConfigOption<Integer>("Type 1 Cave Priority", Configuration.caveSettings.caves.cubicCave.cavePriority)).setCategory("general.underground generation.caves.type 1 caves").addToMap(this.properties);
      this.simplexCaveBottom = (new ConfigHolder.ConfigOption<Integer>("Type 2 Cave Minimum Altitude", Configuration.caveSettings.caves.simplexCave.caveBottom)).setCategory("general.underground generation.caves.type 2 caves").addToMap(this.properties);
      this.simplexCaveTop = (new ConfigHolder.ConfigOption<Integer>("Type 2 Cave Maximum Altitude", Configuration.caveSettings.caves.simplexCave.caveTop)).setCategory("general.underground generation.caves.type 2 caves").addToMap(this.properties);
      this.simplexCaveSurfaceCutoffDepth = (new ConfigHolder.ConfigOption<Integer>("Type 2 Cave Surface Cutoff Depth", Configuration.caveSettings.caves.simplexCave.caveSurfaceCutoff)).setCategory("general.underground generation.caves.type 2 caves").addToMap(this.properties);
      this.simplexCaveYCompression = (new ConfigHolder.ConfigOption<Float>("Compression - Vertical", Configuration.caveSettings.caves.simplexCave.yCompression)).setCategory("general.underground generation.caves.type 2 caves").addToMap(this.properties);
      this.simplexCaveXZCompression = (new ConfigHolder.ConfigOption<Float>("Compression - Horizontal", Configuration.caveSettings.caves.simplexCave.xzCompression)).setCategory("general.underground generation.caves.type 2 caves").addToMap(this.properties);
      this.simplexCavePriority = (new ConfigHolder.ConfigOption<Integer>("Type 2 Cave Priority", Configuration.caveSettings.caves.simplexCave.cavePriority)).setCategory("general.underground generation.caves.type 2 caves").addToMap(this.properties);
      this.isSurfaceCavesEnabled = (new ConfigHolder.ConfigOption<Boolean>("Enable Surface Caves", Configuration.caveSettings.caves.surfaceCave.enableSurfaceCaves)).setCategory("general.underground generation.caves.surface caves").addToMap(this.properties);
      this.surfaceCaveBottom = (new ConfigHolder.ConfigOption<Integer>("Surface Cave Minimum Altitude", Configuration.caveSettings.caves.surfaceCave.caveBottom)).setCategory("general.underground generation.caves.surface caves").addToMap(this.properties);
      this.surfaceCaveTop = (new ConfigHolder.ConfigOption<Integer>("Surface Cave Maximum Altitude", Configuration.caveSettings.caves.surfaceCave.caveTop)).setCategory("general.underground generation.caves.surface caves").addToMap(this.properties);
      this.surfaceCaveDensity = (new ConfigHolder.ConfigOption<Integer>("Surface Cave Density", Configuration.caveSettings.caves.surfaceCave.caveDensity)).setCategory("general.underground generation.caves.surface caves").addToMap(this.properties);
      this.vanillaCaveBottom = (new ConfigHolder.ConfigOption<Integer>("Vanilla Cave Minimum Altitude", Configuration.caveSettings.caves.vanillaCave.caveBottom)).setCategory("general.underground generation.caves.vanilla caves").addToMap(this.properties);
      this.vanillaCaveTop = (new ConfigHolder.ConfigOption<Integer>("Vanilla Cave Maximum Altitude", Configuration.caveSettings.caves.vanillaCave.caveTop)).setCategory("general.underground generation.caves.vanilla caves").addToMap(this.properties);
      this.vanillaCaveDensity = (new ConfigHolder.ConfigOption<Integer>("Vanilla Cave Density", Configuration.caveSettings.caves.vanillaCave.caveDensity)).setCategory("general.underground generation.caves.vanilla caves").addToMap(this.properties);
      this.vanillaCavePriority = (new ConfigHolder.ConfigOption<Integer>("Vanilla Cave Priority", Configuration.caveSettings.caves.vanillaCave.cavePriority)).setCategory("general.underground generation.caves.vanilla caves").addToMap(this.properties);
      this.cavernRegionSize = (new ConfigHolder.ConfigOption<RegionSize>("Cavern Region Size", Configuration.caveSettings.caverns.cavernRegionSize)).setCategory("general.underground generation.caverns").addToMap(this.properties);
      this.cavernRegionCustomSize = (new ConfigHolder.ConfigOption<Float>("Cavern Region Size Custom Value", Configuration.caveSettings.caverns.customRegionSize)).setCategory("general.underground generation.caverns").addToMap(this.properties);
      this.cavernSpawnChance = (new ConfigHolder.ConfigOption<Float>("Cavern Spawn Chance", Configuration.caveSettings.caverns.cavernSpawnChance)).setCategory("general.underground generation.caverns").addToMap(this.properties);
      this.liquidCavernBottom = (new ConfigHolder.ConfigOption<Integer>("Liquid Cavern Minimum Altitude", Configuration.caveSettings.caverns.liquidCavern.cavernBottom)).setCategory("general.underground generation.caverns.liquid caverns").addToMap(this.properties);
      this.liquidCavernTop = (new ConfigHolder.ConfigOption<Integer>("Liquid Cavern Maximum Altitude", Configuration.caveSettings.caverns.liquidCavern.cavernTop)).setCategory("general.underground generation.caverns.liquid caverns").addToMap(this.properties);
      this.liquidCavernYCompression = (new ConfigHolder.ConfigOption<Float>("Compression - Vertical", Configuration.caveSettings.caverns.liquidCavern.yCompression)).setCategory("general.underground generation.caverns.liquid caverns").addToMap(this.properties);
      this.liquidCavernXZCompression = (new ConfigHolder.ConfigOption<Float>("Compression - Horizontal", Configuration.caveSettings.caverns.liquidCavern.xzCompression)).setCategory("general.underground generation.caverns.liquid caverns").addToMap(this.properties);
      this.liquidCavernPriority = (new ConfigHolder.ConfigOption<Integer>("Liquid Cavern Priority", Configuration.caveSettings.caverns.liquidCavern.cavernPriority)).setCategory("general.underground generation.caverns.liquid caverns").addToMap(this.properties);
      this.flooredCavernBottom = (new ConfigHolder.ConfigOption<Integer>("Floored Cavern Minimum Altitude", Configuration.caveSettings.caverns.flooredCavern.cavernBottom)).setCategory("general.underground generation.caverns.floored caverns").addToMap(this.properties);
      this.flooredCavernTop = (new ConfigHolder.ConfigOption<Integer>("Floored Cavern Maximum Altitude", Configuration.caveSettings.caverns.flooredCavern.cavernTop)).setCategory("general.underground generation.caverns.floored caverns").addToMap(this.properties);
      this.flooredCavernYCompression = (new ConfigHolder.ConfigOption<Float>("Compression - Vertical", Configuration.caveSettings.caverns.flooredCavern.yCompression)).setCategory("general.underground generation.caverns.floored caverns").addToMap(this.properties);
      this.flooredCavernXZCompression = (new ConfigHolder.ConfigOption<Float>("Compression - Horizontal", Configuration.caveSettings.caverns.flooredCavern.xzCompression)).setCategory("general.underground generation.caverns.floored caverns").addToMap(this.properties);
      this.flooredCavernPriority = (new ConfigHolder.ConfigOption<Integer>("Floored Cavern Priority", Configuration.caveSettings.caverns.flooredCavern.cavernPriority)).setCategory("general.underground generation.caverns.floored caverns").addToMap(this.properties);
      this.waterRegionSpawnChance = (new ConfigHolder.ConfigOption<Float>("Water Region Spawn Chance", Configuration.caveSettings.waterRegions.waterRegionSpawnChance)).setCategory("general.underground generation.water regions").addToMap(this.properties);
      this.waterRegionSize = (new ConfigHolder.ConfigOption<RegionSize>("Water Region Size", Configuration.caveSettings.waterRegions.waterRegionSize)).setCategory("general.underground generation.water regions").addToMap(this.properties);
      this.waterRegionCustomSize = (new ConfigHolder.ConfigOption<Float>("Water Region Size Custom Value", Configuration.caveSettings.waterRegions.waterRegionCustomSize)).setCategory("general.underground generation.water regions").addToMap(this.properties);
      this.enableVanillaRavines = (new ConfigHolder.ConfigOption<Boolean>("Enable Ravines", Configuration.caveSettings.ravines.enableVanillaRavines)).setCategory("general.underground generation.ravines").addToMap(this.properties);
      this.enableFloodedRavines = (new ConfigHolder.ConfigOption<Boolean>("Enable Flooded Ravines", Configuration.caveSettings.ravines.enableFloodedRavines)).setCategory("general.underground generation.ravines").addToMap(this.properties);
      this.lavaBlock = (new ConfigHolder.ConfigOption<String>("Lava Block", Configuration.caveSettings.miscellaneous.lavaBlock)).setCategory("general.underground generation.miscellaneous").addToMap(this.properties);
      this.waterBlock = (new ConfigHolder.ConfigOption<String>("Water Block", Configuration.caveSettings.miscellaneous.waterBlock)).setCategory("general.underground generation.miscellaneous").addToMap(this.properties);
      this.liquidAltitude = (new ConfigHolder.ConfigOption<Integer>("Liquid Altitude", Configuration.caveSettings.miscellaneous.liquidAltitude)).setCategory("general.underground generation.miscellaneous").addToMap(this.properties);
      this.replaceFloatingGravel = (new ConfigHolder.ConfigOption<Boolean>("Prevent Cascading Gravel", Configuration.caveSettings.miscellaneous.replaceFloatingGravel)).setCategory("general.underground generation.miscellaneous").addToMap(this.properties);
      this.overrideSurfaceDetection = (new ConfigHolder.ConfigOption<Boolean>("Override Surface Detection", Configuration.caveSettings.miscellaneous.overrideSurfaceDetection)).setCategory("general.underground generation.miscellaneous").addToMap(this.properties);
      this.enableFloodedUnderground = (new ConfigHolder.ConfigOption<Boolean>("Enable Flooded Underground", Configuration.caveSettings.miscellaneous.enableFloodedUnderground)).setCategory("general.underground generation.miscellaneous").addToMap(this.properties);
      this.flattenBedrock = (new ConfigHolder.ConfigOption<Boolean>("Flatten Bedrock", Configuration.bedrockSettings.flattenBedrock)).setCategory("general.bedrock generation").addToMap(this.properties);
      this.bedrockWidth = (new ConfigHolder.ConfigOption<Integer>("Bedrock Layer Width", Configuration.bedrockSettings.bedrockWidth)).setCategory("general.bedrock generation").addToMap(this.properties);
      this.debugVisualizer = (new ConfigHolder.ConfigOption<Boolean>("Enable DEBUG Visualizer", Configuration.debugsettings.debugVisualizer)).setCategory("general.debug settings").addToMap(this.properties);
      this.cubicCaveNoiseThreshold = (new ConfigHolder.ConfigOption<Float>("Noise Threshold", Configuration.caveSettings.caves.cubicCave.advancedSettings.noiseThreshold)).setCategory("general.underground generation.caves.type 1 caves.advanced settings").addToMap(this.properties).hidden();
      this.cubicCaveFractalOctaves = (new ConfigHolder.ConfigOption<Integer>("Fractal Octaves", Configuration.caveSettings.caves.cubicCave.advancedSettings.fractalOctaves)).setCategory("general.underground generation.caves.type 1 caves.advanced settings").addToMap(this.properties).hidden();
      this.cubicCaveFractalGain = (new ConfigHolder.ConfigOption<Float>("Fractal Gain", Configuration.caveSettings.caves.cubicCave.advancedSettings.fractalGain)).setCategory("general.underground generation.caves.type 1 caves.advanced settings").addToMap(this.properties).hidden();
      this.cubicCaveFractalFrequency = (new ConfigHolder.ConfigOption<Float>("Fractal Frequency", Configuration.caveSettings.caves.cubicCave.advancedSettings.fractalFrequency)).setCategory("general.underground generation.caves.type 1 caves.advanced settings").addToMap(this.properties).hidden();
      this.cubicCaveNumGenerators = (new ConfigHolder.ConfigOption<Integer>("Number of Generators", Configuration.caveSettings.caves.cubicCave.advancedSettings.numGenerators)).setCategory("general.underground generation.caves.type 1 caves.advanced settings").addToMap(this.properties).hidden();
      this.cubicCaveEnableVerticalAdjustment = (new ConfigHolder.ConfigOption<Boolean>("Enable y-adjustment", Configuration.caveSettings.caves.cubicCave.advancedSettings.yAdjust)).setCategory("general.underground generation.caves.type 1 caves.advanced settings").addToMap(this.properties).hidden();
      this.cubicCaveYAdjustF1 = (new ConfigHolder.ConfigOption<Float>("y-adjustment Variable 1", Configuration.caveSettings.caves.cubicCave.advancedSettings.yAdjustF1)).setCategory("general.underground generation.caves.type 1 caves.advanced settings").addToMap(this.properties).hidden();
      this.cubicCaveYAdjustF2 = (new ConfigHolder.ConfigOption<Float>("y-adjustment Variable 2", Configuration.caveSettings.caves.cubicCave.advancedSettings.yAdjustF2)).setCategory("general.underground generation.caves.type 1 caves.advanced settings").addToMap(this.properties).hidden();
      this.cubicCaveNoiseType = (new ConfigHolder.ConfigOption<FastNoise.NoiseType>("Noise Type", Configuration.caveSettings.caves.cubicCave.advancedSettings.noiseType)).setCategory("general.underground generation.caves.type 1 caves.advanced settings").addToMap(this.properties).hidden();
      this.simplexCaveNoiseThreshold = (new ConfigHolder.ConfigOption<Float>("Noise Threshold", Configuration.caveSettings.caves.simplexCave.advancedSettings.noiseThreshold)).setCategory("general.underground generation.caves.type 2 caves.advanced settings").addToMap(this.properties).hidden();
      this.simplexCaveFractalOctaves = (new ConfigHolder.ConfigOption<Integer>("Fractal Octaves", Configuration.caveSettings.caves.simplexCave.advancedSettings.fractalOctaves)).setCategory("general.underground generation.caves.type 2 caves.advanced settings").addToMap(this.properties).hidden();
      this.simplexCaveFractalGain = (new ConfigHolder.ConfigOption<Float>("Fractal Gain", Configuration.caveSettings.caves.simplexCave.advancedSettings.fractalGain)).setCategory("general.underground generation.caves.type 2 caves.advanced settings").addToMap(this.properties).hidden();
      this.simplexCaveFractalFrequency = (new ConfigHolder.ConfigOption<Float>("Fractal Frequency", Configuration.caveSettings.caves.simplexCave.advancedSettings.fractalFrequency)).setCategory("general.underground generation.caves.type 2 caves.advanced settings").addToMap(this.properties).hidden();
      this.simplexCaveNumGenerators = (new ConfigHolder.ConfigOption<Integer>("Number of Generators", Configuration.caveSettings.caves.simplexCave.advancedSettings.numGenerators)).setCategory("general.underground generation.caves.type 2 caves.advanced settings").addToMap(this.properties).hidden();
      this.simplexCaveEnableVerticalAdjustment = (new ConfigHolder.ConfigOption<Boolean>("Enable y-adjustment", Configuration.caveSettings.caves.simplexCave.advancedSettings.yAdjust)).setCategory("general.underground generation.caves.type 2 caves.advanced settings").addToMap(this.properties).hidden();
      this.simplexCaveYAdjustF1 = (new ConfigHolder.ConfigOption<Float>("y-adjustment Variable 1", Configuration.caveSettings.caves.simplexCave.advancedSettings.yAdjustF1)).setCategory("general.underground generation.caves.type 2 caves.advanced settings").addToMap(this.properties).hidden();
      this.simplexCaveYAdjustF2 = (new ConfigHolder.ConfigOption<Float>("y-adjustment Variable 2", Configuration.caveSettings.caves.simplexCave.advancedSettings.yAdjustF2)).setCategory("general.underground generation.caves.type 2 caves.advanced settings").addToMap(this.properties).hidden();
      this.simplexCaveNoiseType = (new ConfigHolder.ConfigOption<FastNoise.NoiseType>("Noise Type", Configuration.caveSettings.caves.simplexCave.advancedSettings.noiseType)).setCategory("general.underground generation.caves.type 2 caves.advanced settings").addToMap(this.properties).hidden();
      this.liquidCavernNoiseThreshold = (new ConfigHolder.ConfigOption<Float>("Noise Threshold", Configuration.caveSettings.caverns.liquidCavern.advancedSettings.noiseThreshold)).setCategory("general.underground generation.caverns.liquid caverns.advanced settings").addToMap(this.properties).hidden();
      this.liquidCavernFractalOctaves = (new ConfigHolder.ConfigOption<Integer>("Fractal Octaves", Configuration.caveSettings.caverns.liquidCavern.advancedSettings.fractalOctaves)).setCategory("general.underground generation.caverns.liquid caverns.advanced settings").addToMap(this.properties).hidden();
      this.liquidCavernFractalGain = (new ConfigHolder.ConfigOption<Float>("Fractal Gain", Configuration.caveSettings.caverns.liquidCavern.advancedSettings.fractalGain)).setCategory("general.underground generation.caverns.liquid caverns.advanced settings").addToMap(this.properties).hidden();
      this.liquidCavernFractalFrequency = (new ConfigHolder.ConfigOption<Float>("Fractal Frequency", Configuration.caveSettings.caverns.liquidCavern.advancedSettings.fractalFrequency)).setCategory("general.underground generation.caverns.liquid caverns.advanced settings").addToMap(this.properties).hidden();
      this.liquidCavernNumGenerators = (new ConfigHolder.ConfigOption<Integer>("Number of Generators", Configuration.caveSettings.caverns.liquidCavern.advancedSettings.numGenerators)).setCategory("general.underground generation.caverns.liquid caverns.advanced settings").addToMap(this.properties).hidden();
      this.liquidCavernNoiseType = (new ConfigHolder.ConfigOption<FastNoise.NoiseType>("Noise Type", Configuration.caveSettings.caverns.liquidCavern.advancedSettings.noiseType)).setCategory("general.underground generation.caverns.liquid caverns.advanced settings").addToMap(this.properties).hidden();
      this.flooredCavernNoiseThreshold = (new ConfigHolder.ConfigOption<Float>("Noise Threshold", Configuration.caveSettings.caverns.flooredCavern.advancedSettings.noiseThreshold)).setCategory("general.underground generation.caverns.floored caverns.advanced settings").addToMap(this.properties).hidden();
      this.flooredCavernFractalOctaves = (new ConfigHolder.ConfigOption<Integer>("Fractal Octaves", Configuration.caveSettings.caverns.flooredCavern.advancedSettings.fractalOctaves)).setCategory("general.underground generation.caverns.floored caverns.advanced settings").addToMap(this.properties).hidden();
      this.flooredCavernFractalGain = (new ConfigHolder.ConfigOption<Float>("Fractal Gain", Configuration.caveSettings.caverns.flooredCavern.advancedSettings.fractalGain)).setCategory("general.underground generation.caverns.floored caverns.advanced settings").addToMap(this.properties).hidden();
      this.flooredCavernFractalFrequency = (new ConfigHolder.ConfigOption<Float>("Fractal Frequency", Configuration.caveSettings.caverns.flooredCavern.advancedSettings.fractalFrequency)).setCategory("general.underground generation.caverns.floored caverns.advanced settings").addToMap(this.properties).hidden();
      this.flooredCavernNumGenerators = (new ConfigHolder.ConfigOption<Integer>("Number of Generators", Configuration.caveSettings.caverns.flooredCavern.advancedSettings.numGenerators)).setCategory("general.underground generation.caverns.floored caverns.advanced settings").addToMap(this.properties).hidden();
      this.flooredCavernNoiseType = (new ConfigHolder.ConfigOption<FastNoise.NoiseType>("Noise Type", Configuration.caveSettings.caverns.flooredCavern.advancedSettings.noiseType)).setCategory("general.underground generation.caverns.floored caverns.advanced settings").addToMap(this.properties).hidden();
   }

   public static class ConfigOption<T> {
      public String name;
      public String fullName;
      public Class<?> type;
      private T value;
      private boolean hidden = false;
      private String category = "";

      public ConfigOption(String name, T value) {
         this.name = name;
         this.value = value;
         this.type = value.getClass();
      }

      public T get() {
         return this.value;
      }

      public void set(Object value) {
         this.value = (T)value;
      }

      public ConfigHolder.ConfigOption<T> hidden() {
         this.hidden = true;
         return this;
      }

      public ConfigHolder.ConfigOption<T> setCategory(String category) {
         this.category = category;
         this.fullName = category + "." + this.name;
         return this;
      }

      public ConfigHolder.ConfigOption<T> addToMap(Map<String, ConfigHolder.ConfigOption<?>> map) {
         map.put(this.fullName, this);
         return this;
      }
   }
}

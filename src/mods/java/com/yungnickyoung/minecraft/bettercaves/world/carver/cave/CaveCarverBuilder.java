package com.yungnickyoung.minecraft.bettercaves.world.carver.cave;

import com.yungnickyoung.minecraft.bettercaves.config.util.ConfigHolder;
import com.yungnickyoung.minecraft.bettercaves.enums.CaveType;
import com.yungnickyoung.minecraft.bettercaves.noise.FastNoise;
import com.yungnickyoung.minecraft.bettercaves.world.carver.CarverSettings;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;

public class CaveCarverBuilder {
   private CarverSettings settings;
   private int surfaceCutoff;
   private int bottomY;
   private int topY;
   private boolean enableYAdjust;
   private float yAdjustF1;
   private float yAdjustF2;

   public CaveCarverBuilder(World world) {
      this.settings = new CarverSettings(world);
   }

   public CaveCarver build() {
      return new CaveCarver(this);
   }

   public CaveCarverBuilder ofTypeFromConfig(CaveType caveType, ConfigHolder config) {
      this.settings.setLiquidAltitude(((Integer)config.liquidAltitude.get()).intValue());
      this.settings.setReplaceFloatingGravel(((Boolean)config.replaceFloatingGravel.get()).booleanValue());
      this.settings.setEnableDebugVisualizer(((Boolean)config.debugVisualizer.get()).booleanValue());
      this.settings.getNoiseSettings().setFractalType(FastNoise.FractalType.RigidMulti);
      switch(caveType) {
      case CUBIC:
         this.settings.setFastNoise(true);
         this.settings.setNoiseThreshold(((Float)config.cubicCaveNoiseThreshold.get()).floatValue());
         this.settings.getNoiseSettings().setNoiseType(config.cubicCaveNoiseType.get());
         this.settings.getNoiseSettings().setOctaves(((Integer)config.cubicCaveFractalOctaves.get()).intValue());
         this.settings.getNoiseSettings().setGain(((Float)config.cubicCaveFractalGain.get()).floatValue());
         this.settings.getNoiseSettings().setFrequency(((Float)config.cubicCaveFractalFrequency.get()).floatValue());
         this.settings.setNumGens(((Integer)config.cubicCaveNumGenerators.get()).intValue());
         this.settings.setXzCompression(((Float)config.cubicCaveXZCompression.get()).floatValue());
         this.settings.setyCompression(((Float)config.cubicCaveYCompression.get()).floatValue());
         this.settings.setPriority(((Integer)config.cubicCavePriority.get()).intValue());
         this.surfaceCutoff = ((Integer)config.cubicCaveSurfaceCutoffDepth.get()).intValue();
         this.bottomY = ((Integer)config.cubicCaveBottom.get()).intValue();
         this.topY = ((Integer)config.cubicCaveTop.get()).intValue();
         this.enableYAdjust = ((Boolean)config.cubicCaveEnableVerticalAdjustment.get()).booleanValue();
         this.yAdjustF1 = ((Float)config.cubicCaveYAdjustF1.get()).floatValue();
         this.yAdjustF2 = ((Float)config.cubicCaveYAdjustF2.get()).floatValue();
         break;
      case SIMPLEX:
         this.settings.setFastNoise(false);
         this.settings.setNoiseThreshold(((Float)config.simplexCaveNoiseThreshold.get()).floatValue());
         this.settings.getNoiseSettings().setNoiseType(config.simplexCaveNoiseType.get());
         this.settings.getNoiseSettings().setOctaves(((Integer)config.simplexCaveFractalOctaves.get()).intValue());
         this.settings.getNoiseSettings().setGain(((Float)config.simplexCaveFractalGain.get()).floatValue());
         this.settings.getNoiseSettings().setFrequency(((Float)config.simplexCaveFractalFrequency.get()).floatValue());
         this.settings.setNumGens(((Integer)config.simplexCaveNumGenerators.get()).intValue());
         this.settings.setXzCompression(((Float)config.simplexCaveXZCompression.get()).floatValue());
         this.settings.setyCompression(((Float)config.simplexCaveYCompression.get()).floatValue());
         this.settings.setPriority(((Integer)config.simplexCavePriority.get()).intValue());
         this.surfaceCutoff = ((Integer)config.simplexCaveSurfaceCutoffDepth.get()).intValue();
         this.bottomY = ((Integer)config.simplexCaveBottom.get()).intValue();
         this.topY = ((Integer)config.simplexCaveTop.get()).intValue();
         this.enableYAdjust = ((Boolean)config.simplexCaveEnableVerticalAdjustment.get()).booleanValue();
         this.yAdjustF1 = ((Float)config.simplexCaveYAdjustF1.get()).floatValue();
         this.yAdjustF2 = ((Float)config.simplexCaveYAdjustF2.get()).floatValue();
      }

      return this;
   }

   public CaveCarverBuilder noiseType(FastNoise.NoiseType noiseType) {
      this.settings.getNoiseSettings().setNoiseType(noiseType);
      return this;
   }

   public CaveCarverBuilder fractalOctaves(int fractalOctaves) {
      this.settings.getNoiseSettings().setOctaves(fractalOctaves);
      return this;
   }

   public CaveCarverBuilder fractalGain(float fractalGain) {
      this.settings.getNoiseSettings().setGain(fractalGain);
      return this;
   }

   public CaveCarverBuilder fractalFrequency(float fractalFreq) {
      this.settings.getNoiseSettings().setFrequency(fractalFreq);
      return this;
   }

   public CaveCarverBuilder numberOfGenerators(int numGens) {
      this.settings.setNumGens(numGens);
      return this;
   }

   public CaveCarverBuilder verticalCompression(float yCompression) {
      this.settings.setyCompression(yCompression);
      return this;
   }

   public CaveCarverBuilder horizontalCompression(float xzCompression) {
      this.settings.setXzCompression(xzCompression);
      return this;
   }

   public CaveCarverBuilder surfaceCutoff(int surfaceCutoff) {
      this.surfaceCutoff = surfaceCutoff;
      return this;
   }

   public CaveCarverBuilder bottomY(int bottomY) {
      this.bottomY = bottomY;
      return this;
   }

   public CaveCarverBuilder topY(int topY) {
      this.topY = topY;
      return this;
   }

   public CaveCarverBuilder verticalAdjuster1(float yAdjustF1) {
      this.yAdjustF1 = yAdjustF1;
      return this;
   }

   public CaveCarverBuilder verticalAdjuster2(float yAdjustF2) {
      this.yAdjustF2 = yAdjustF2;
      return this;
   }

   public CaveCarverBuilder enableVerticalAdjustment(boolean enableYAdjust) {
      this.enableYAdjust = enableYAdjust;
      return this;
   }

   public CaveCarverBuilder noiseThreshold(float noiseThreshold) {
      this.settings.setNoiseThreshold(noiseThreshold);
      return this;
   }

   public CaveCarverBuilder debugVisualizerBlock(IBlockState vBlock) {
      this.settings.setDebugBlock(vBlock);
      return this;
   }

   public CaveCarverBuilder liquidAltitude(int liquidAltitude) {
      this.settings.setLiquidAltitude(liquidAltitude);
      return this;
   }

   public CaveCarverBuilder enableDebugVisualizer(boolean enableDebugVisualizer) {
      this.settings.setEnableDebugVisualizer(enableDebugVisualizer);
      return this;
   }

   public CarverSettings getSettings() {
      return this.settings;
   }

   public int getSurfaceCutoff() {
      return this.surfaceCutoff;
   }

   public int getBottomY() {
      return this.bottomY;
   }

   public int getTopY() {
      return this.topY;
   }

   public boolean isEnableYAdjust() {
      return this.enableYAdjust;
   }

   public float getyAdjustF1() {
      return this.yAdjustF1;
   }

   public float getyAdjustF2() {
      return this.yAdjustF2;
   }
}

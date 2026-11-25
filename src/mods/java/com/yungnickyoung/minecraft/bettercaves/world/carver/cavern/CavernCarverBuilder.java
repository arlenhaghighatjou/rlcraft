package com.yungnickyoung.minecraft.bettercaves.world.carver.cavern;

import com.yungnickyoung.minecraft.bettercaves.config.util.ConfigHolder;
import com.yungnickyoung.minecraft.bettercaves.enums.CavernType;
import com.yungnickyoung.minecraft.bettercaves.noise.FastNoise;
import com.yungnickyoung.minecraft.bettercaves.world.carver.CarverSettings;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;

public class CavernCarverBuilder {
   private CarverSettings settings;
   private CavernType cavernType;
   private int bottomY;
   private int topY;

   public CavernCarverBuilder(World world) {
      this.settings = new CarverSettings(world);
   }

   public CavernCarver build() {
      return new CavernCarver(this);
   }

   public CavernCarverBuilder ofTypeFromConfig(CavernType cavernType, ConfigHolder config) {
      this.settings.setLiquidAltitude(((Integer)config.liquidAltitude.get()).intValue());
      this.settings.setReplaceFloatingGravel(((Boolean)config.replaceFloatingGravel.get()).booleanValue());
      this.settings.getNoiseSettings().setFractalType(FastNoise.FractalType.RigidMulti);
      this.settings.setEnableDebugVisualizer(((Boolean)config.debugVisualizer.get()).booleanValue());
      this.settings.setFastNoise(true);
      this.cavernType = cavernType;
      switch(cavernType) {
      case LIQUID:
         this.settings.setNoiseThreshold(((Float)config.liquidCavernNoiseThreshold.get()).floatValue());
         this.settings.getNoiseSettings().setNoiseType(config.liquidCavernNoiseType.get());
         this.settings.getNoiseSettings().setOctaves(((Integer)config.liquidCavernFractalOctaves.get()).intValue());
         this.settings.getNoiseSettings().setGain(((Float)config.liquidCavernFractalGain.get()).floatValue());
         this.settings.getNoiseSettings().setFrequency(((Float)config.liquidCavernFractalFrequency.get()).floatValue());
         this.settings.setNumGens(((Integer)config.liquidCavernNumGenerators.get()).intValue());
         this.settings.setyCompression(((Float)config.liquidCavernYCompression.get()).floatValue());
         this.settings.setXzCompression(((Float)config.liquidCavernXZCompression.get()).floatValue());
         this.settings.setPriority(((Integer)config.liquidCavernPriority.get()).intValue());
         this.bottomY = ((Integer)config.liquidCavernBottom.get()).intValue();
         this.topY = ((Integer)config.liquidCavernTop.get()).intValue();
         break;
      case FLOORED:
         this.settings.setNoiseThreshold(((Float)config.flooredCavernNoiseThreshold.get()).floatValue());
         this.settings.getNoiseSettings().setNoiseType(config.flooredCavernNoiseType.get());
         this.settings.getNoiseSettings().setOctaves(((Integer)config.flooredCavernFractalOctaves.get()).intValue());
         this.settings.getNoiseSettings().setGain(((Float)config.flooredCavernFractalGain.get()).floatValue());
         this.settings.getNoiseSettings().setFrequency(((Float)config.flooredCavernFractalFrequency.get()).floatValue());
         this.settings.setNumGens(((Integer)config.flooredCavernNumGenerators.get()).intValue());
         this.settings.setyCompression(((Float)config.flooredCavernYCompression.get()).floatValue());
         this.settings.setXzCompression(((Float)config.flooredCavernXZCompression.get()).floatValue());
         this.settings.setPriority(((Integer)config.flooredCavernPriority.get()).intValue());
         this.bottomY = ((Integer)config.flooredCavernBottom.get()).intValue();
         this.topY = ((Integer)config.flooredCavernTop.get()).intValue();
      }

      return this;
   }

   public CavernCarverBuilder noiseType(FastNoise.NoiseType noiseType) {
      this.settings.getNoiseSettings().setNoiseType(noiseType);
      return this;
   }

   public CavernCarverBuilder fractalOctaves(int fractalOctaves) {
      this.settings.getNoiseSettings().setOctaves(fractalOctaves);
      return this;
   }

   public CavernCarverBuilder fractalGain(float fractalGain) {
      this.settings.getNoiseSettings().setGain(fractalGain);
      return this;
   }

   public CavernCarverBuilder fractalFrequency(float fractalFreq) {
      this.settings.getNoiseSettings().setFrequency(fractalFreq);
      return this;
   }

   public CavernCarverBuilder numberOfGenerators(int numGens) {
      this.settings.setNumGens(numGens);
      return this;
   }

   public CavernCarverBuilder verticalCompression(float yCompression) {
      this.settings.setyCompression(yCompression);
      return this;
   }

   public CavernCarverBuilder horizontalCompression(float xzCompression) {
      this.settings.setXzCompression(xzCompression);
      return this;
   }

   public CavernCarverBuilder noiseThreshold(float noiseThreshold) {
      this.settings.setNoiseThreshold(noiseThreshold);
      return this;
   }

   public CavernCarverBuilder debugVisualizerBlock(IBlockState vBlock) {
      this.settings.setDebugBlock(vBlock);
      return this;
   }

   public CavernCarverBuilder liquidAltitude(int liquidAltitude) {
      this.settings.setLiquidAltitude(liquidAltitude);
      return this;
   }

   public CavernCarverBuilder enableDebugVisualizer(boolean enableDebugVisualizer) {
      this.settings.setEnableDebugVisualizer(enableDebugVisualizer);
      return this;
   }

   public CavernCarverBuilder cavernType(CavernType cavernType) {
      this.cavernType = cavernType;
      return this;
   }

   public CavernCarverBuilder bottomY(int bottomY) {
      this.bottomY = bottomY;
      return this;
   }

   public CavernCarverBuilder topY(int topY) {
      this.topY = topY;
      return this;
   }

   public CarverSettings getSettings() {
      return this.settings;
   }

   public CavernType getCavernType() {
      return this.cavernType;
   }

   public int getBottomY() {
      return this.bottomY;
   }

   public int getTopY() {
      return this.topY;
   }
}

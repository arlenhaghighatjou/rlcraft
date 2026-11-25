package org.orecruncher.dsurround.registry.effect.theme;

import javax.annotation.Nonnull;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModOptions;

@SideOnly(Side.CLIENT)
public class ThemeInfo {
   protected final String name = "DEFAULT";
   protected boolean doBiomeFog = true;
   protected boolean doMorningFog = true;
   protected boolean doWeatherFog = true;
   protected boolean doElevationHaze = true;
   protected boolean doBedrockFog = true;
   protected int maxLightLevel = 15;
   protected float minFogDistance = 0.0F;
   protected float maxFogDistance = 0.0F;

   public ThemeInfo() {
   }

   @Nonnull
   public String name() {
      return this.name;
   }

   public boolean doBiomeFog() {
      return this.doBiomeFog && ModOptions.fog.enableBiomeFog;
   }

   public boolean doMorningFog() {
      return this.doMorningFog && ModOptions.fog.enableMorningFog;
   }

   public boolean doWeatherFog() {
      return this.doWeatherFog && ModOptions.fog.enableWeatherFog;
   }

   public boolean doElevationHaze() {
      return this.doElevationHaze && ModOptions.fog.enableElevationHaze;
   }

   public boolean doBedrockFog() {
      return this.doBedrockFog && ModOptions.fog.enableBedrockFog;
   }

   public boolean doMaxLightLevel() {
      return this.maxLightLevel >= 0 && this.maxLightLevel < 16;
   }

   public int getMaxLightLevel() {
      return this.maxLightLevel;
   }

   public boolean doFixedFog() {
      return this.minFogDistance >= 0.0F && this.minFogDistance < this.maxFogDistance;
   }

   public float getMinFogDistance() {
      return this.minFogDistance;
   }

   public float getMaxFogDistance() {
      return this.maxFogDistance;
   }
}

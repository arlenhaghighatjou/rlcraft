package org.orecruncher.dsurround.capabilities.dimension;

import java.text.DecimalFormat;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nonnull;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.registry.RegistryManager;
import org.orecruncher.dsurround.registry.config.DimensionConfig;
import org.orecruncher.lib.math.MathStuff;
import org.orecruncher.lib.random.XorShiftRandom;

public final class DimensionInfo implements IDimensionInfoEx {
   public static final IDimensionInfo NONE = new DimensionInfo();
   public static final float MIN_INTENSITY = 0.0F;
   public static final float MAX_INTENSITY = 1.0F;
   private static final int SPACE_HEIGHT_OFFSET = 32;
   private static final DecimalFormat FORMATTER = new DecimalFormat("0");
   protected final EaglercraftRandom RANDOM = XorShiftRandom.current();
   protected final World world;
   private float intensity = 0.0F;
   private float currentIntensity = 0.0F;
   private float minIntensity = ModOptions.rain.defaultMinRainStrength;
   private float maxIntensity = ModOptions.rain.defaultMaxRainStrength;
   private int thunderTimer = 0;
   protected final int dimensionId;
   protected final String name;
   protected int seaLevel;
   protected int skyHeight;
   protected int cloudHeight;
   protected int spaceHeight;
   protected boolean hasHaze = false;
   protected boolean hasAuroras = false;
   protected boolean hasWeather = false;
   protected boolean hasFog = false;
   protected boolean alwaysOutside = false;
   protected boolean playBiomeSounds = true;

   public DimensionInfo() {
      this.world = null;
      this.dimensionId = Integer.MIN_VALUE;
      this.name = "<DEFAULT NONE>";
   }

   public DimensionInfo(@Nonnull World world) {
      this.world = world;
      this.dimensionId = world.provider.getDimension();
      this.name = world.provider.getDimensionType().getName();
      this.seaLevel = world.getSeaLevel();
      this.skyHeight = world.getActualHeight();
      this.cloudHeight = this.skyHeight;
      this.spaceHeight = this.skyHeight + 32;
      if (world.provider.isSurfaceWorld() && world.provider.hasSkyLight()) {
         this.hasWeather = true;
         this.hasAuroras = true;
         this.hasFog = true;
      }

      WorldType wt = world.getWorldType();
      if (wt == WorldType.FLAT) {
         this.seaLevel = 0;
      } else if (this.dimensionId == 0 && ModOptions.biomes.worldSealevelOverride > 0) {
         this.seaLevel = ModOptions.biomes.worldSealevelOverride;
      }

      String dim = Integer.toString(this.dimensionId);

      for(int i = 0; i < ModOptions.biomes.dimensionBlacklist.length; ++i) {
         if (dim.equals(ModOptions.biomes.dimensionBlacklist[i])) {
            this.playBiomeSounds = false;
            break;
         }
      }

      DimensionConfig entry = RegistryManager.DIMENSION.getData(this.world);
      if (entry != null) {
         if (entry.seaLevel != null) {
            this.seaLevel = entry.seaLevel.intValue();
         }

         if (entry.skyHeight != null) {
            this.skyHeight = entry.skyHeight.intValue();
         }

         if (entry.hasHaze != null) {
            this.hasHaze = entry.hasHaze.booleanValue();
         }

         if (entry.hasAurora != null) {
            this.hasAuroras = entry.hasAurora.booleanValue();
         }

         if (entry.hasWeather != null) {
            this.hasWeather = entry.hasWeather.booleanValue();
         }

         if (entry.cloudHeight != null) {
            this.cloudHeight = entry.cloudHeight.intValue();
         } else {
            this.cloudHeight = this.hasHaze ? this.skyHeight / 2 : this.skyHeight;
         }

         if (entry.hasFog != null) {
            this.hasFog = entry.hasFog.booleanValue();
         }

         if (entry.alwaysOutside != null) {
            this.alwaysOutside = entry.alwaysOutside.booleanValue();
         }

         this.spaceHeight = this.skyHeight + 32;
      }

   }

   public int getId() {
      return this.dimensionId;
   }

   @Nonnull
   public String getName() {
      return this.name;
   }

   public int getSeaLevel() {
      return this.seaLevel;
   }

   public int getSkyHeight() {
      return this.skyHeight;
   }

   public int getCloudHeight() {
      return this.cloudHeight;
   }

   public int getSpaceHeight() {
      return this.spaceHeight;
   }

   public boolean hasHaze() {
      return this.hasHaze;
   }

   public boolean hasAuroras() {
      return this.hasAuroras;
   }

   public boolean hasWeather() {
      return this.hasWeather;
   }

   public boolean hasFog() {
      return this.hasFog;
   }

   public boolean playBiomeSounds() {
      return this.playBiomeSounds;
   }

   public boolean alwaysOutside() {
      return this.alwaysOutside;
   }

   public float getRainIntensity() {
      return this.intensity;
   }

   public float getCurrentRainIntensity() {
      return this.currentIntensity;
   }

   public void setRainIntensity(float intensity) {
      this.intensity = MathStuff.clamp(intensity, 0.0F, 1.0F);
   }

   public void setCurrentRainIntensity(float intensity) {
      this.currentIntensity = MathStuff.clamp(intensity, 0.0F, this.intensity);
   }

   public float getMinRainIntensity() {
      return this.minIntensity;
   }

   public void setMinRainIntensity(float intensity) {
      this.minIntensity = MathStuff.clamp(intensity, 0.0F, this.maxIntensity);
   }

   public float getMaxRainIntensity() {
      return this.maxIntensity;
   }

   public void setMaxRainIntensity(float intensity) {
      this.maxIntensity = MathStuff.clamp(intensity, this.minIntensity, 1.0F);
   }

   public int getThunderTimer() {
      return this.thunderTimer;
   }

   public void setThunderTimer(int time) {
      this.thunderTimer = MathStuff.clamp(time, 0, Integer.MAX_VALUE);
   }

   public void randomizeRain() {
      float delta = this.maxIntensity - this.minIntensity;
      float result;
      if (delta <= 0.0F) {
         result = this.minIntensity;
      } else {
         float mid = delta / 2.0F;
         result = this.minIntensity + this.RANDOM.nextFloat() * mid + this.RANDOM.nextFloat() * mid;
      }

      this.setRainIntensity(MathStuff.clamp(result, 0.01F, 1.0F));
      this.setCurrentRainIntensity(0.0F);
   }

   public NBTTagCompound serializeNBT() {
      NBTTagCompound nbt = new NBTTagCompound();
      nbt.setFloat("i", this.getRainIntensity());
      nbt.setFloat("ci", this.getCurrentRainIntensity());
      nbt.setFloat("min", this.getMinRainIntensity());
      nbt.setFloat("max", this.getMaxRainIntensity());
      nbt.setInteger("th", this.getThunderTimer());
      return nbt;
   }

   public void deserializeNBT(@Nonnull NBTTagCompound nbt) {
      this.setRainIntensity(nbt.getFloat("i"));
      this.setCurrentRainIntensity(nbt.getFloat("ci"));
      this.setMinRainIntensity(nbt.getFloat("min"));
      this.setMaxRainIntensity(nbt.getFloat("max"));
      this.setThunderTimer(nbt.getInteger("th"));
   }

   @Nonnull
   public String configString() {
      StringBuilder builder = new StringBuilder();
      builder.append("dim ").append(this.getId()).append(": ");
      builder.append("rainIntensity [").append(FORMATTER.format((double)(this.getMinRainIntensity() * 100.0F)));
      builder.append(",").append(FORMATTER.format((double)(this.getMaxRainIntensity() * 100.0F)));
      builder.append("]");
      return builder.toString();
   }

   @Nonnull
   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("dim ").append(this.getId()).append(": ");
      builder.append("rainIntensity: ").append(FORMATTER.format((double)(this.getRainIntensity() * 100.0F)));
      builder.append('/').append(FORMATTER.format((double)(this.getCurrentRainIntensity() * 100.0F)));
      builder.append(" [").append(FORMATTER.format((double)(this.getMinRainIntensity() * 100.0F)));
      builder.append(",").append(FORMATTER.format((double)(this.getMaxRainIntensity() * 100.0F)));
      builder.append("], thunderTimer: ").append(this.getThunderTimer());
      return builder.toString();
   }

   private static final class NBT {
      public static final String INTENSITY = "i";
      public static final String CURRENT_INTENSITY = "ci";
      public static final String MIN_INTENSITY = "min";
      public static final String MAX_INTENSITY = "max";
      public static final String THUNDER_TIMER = "th";

      private NBT() {
      }
   }
}

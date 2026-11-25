package org.orecruncher.dsurround.client.weather;

import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.client.sound.Sounds;
import org.orecruncher.dsurround.client.weather.tracker.ServerDrivenTracker;
import org.orecruncher.dsurround.client.weather.tracker.SimulationTracker;
import org.orecruncher.dsurround.client.weather.tracker.Tracker;
import org.orecruncher.dsurround.event.WeatherUpdateEvent;
import org.orecruncher.dsurround.registry.RegistryManager;
import org.orecruncher.lib.math.MathStuff;

@SideOnly(Side.CLIENT)
public class Weather {
   private static final SoundEvent VANILLA_RAIN = RegistryManager.SOUND.getSound(new ResourceLocation("weather.rain"));
   private static Tracker tracker = new SimulationTracker();

   public Weather() {
   }

   private static World getWorld() {
      return Minecraft.getMinecraft().world;
   }

   public static boolean isRaining() {
      return tracker.isRaining();
   }

   public static boolean isThundering() {
      return tracker.isThundering();
   }

   @Nonnull
   public static Weather.Properties getWeatherProperties() {
      return tracker.getWeatherProperties();
   }

   public static float getIntensityLevel() {
      return tracker.getIntensityLevel();
   }

   public static float getMaxIntensityLevel() {
      return tracker.getMaxIntensityLevel();
   }

   public static int getNextRainChange() {
      return tracker.getNextRainChange();
   }

   public static float getThunderStrength() {
      return tracker.getThunderStrength();
   }

   public static int getNextThunderChange() {
      return tracker.getNextThunderChange();
   }

   public static int getNextThunderEvent() {
      return tracker.getNextThunderEvent();
   }

   public static float getCurrentVolume() {
      return tracker.getCurrentVolume();
   }

   @Nonnull
   public static SoundEvent getCurrentStormSound() {
      return tracker.getCurrentStormSound();
   }

   public static boolean notDoVanilla() {
      return !tracker.doVanilla();
   }

   public static void update() {
      tracker.update();
   }

   @SubscribeEvent
   public static void onWeatherUpdateEvent(@Nonnull WeatherUpdateEvent event) {
      World world = getWorld();
      if (world != null && world.provider != null) {
         if (world.provider.getDimension() == event.dimId) {
            if (tracker instanceof ServerDrivenTracker) {
               ((ServerDrivenTracker)tracker).update(event);
            }

         }
      }
   }

   public static void register(boolean serverAvailable) {
      if (serverAvailable) {
         tracker = new ServerDrivenTracker();
      }

   }

   public static void unregister() {
      tracker = new SimulationTracker();
   }

   @Nonnull
   public static String diagnostic() {
      return tracker.toString();
   }

   public static enum Properties {
      VANILLA,
      NONE(0.0F, "calm"),
      CALM(0.125F, "calm"),
      LIGHT(0.25F, "light"),
      GENTLE(0.365F, "gentle"),
      MODERATE(0.5F, "moderate"),
      HEAVY(0.625F, "heavy"),
      STRONG(0.75F, "strong"),
      INTENSE(0.875F, "intense"),
      TORRENTIAL(1.0F, "torrential");

      private final float level;
      private final ResourceLocation rainTexture;
      private final ResourceLocation snowTexture;
      private final ResourceLocation dustTexture;

      private Properties() {
         this.level = -10.0F;
         this.rainTexture = new ResourceLocation("textures/environment/rain.png");
         this.snowTexture = new ResourceLocation("textures/environment/snow.png");
         this.dustTexture = new ResourceLocation("dsurround", "textures/environment/dust_calm.png");
      }

      private Properties(float level, @Nonnull String intensity) {
         this.level = level;
         this.rainTexture = new ResourceLocation("dsurround", String.format("textures/environment/rain_%s.png", intensity));
         this.snowTexture = new ResourceLocation("dsurround", String.format("textures/environment/snow_%s.png", intensity));
         this.dustTexture = new ResourceLocation("dsurround", String.format("textures/environment/dust_%s.png", intensity));
      }

      public float getLevel() {
         return this.level;
      }

      @Nonnull
      public ResourceLocation getRainTexture() {
         return this.rainTexture;
      }

      @Nonnull
      public ResourceLocation getDustTexture() {
         return this.dustTexture;
      }

      @Nonnull
      public ResourceLocation getSnowTexture() {
         return this.snowTexture;
      }

      @Nonnull
      public SoundEvent getStormSound() {
         return ModOptions.rain.useVanillaRainSound ? Weather.VANILLA_RAIN : Sounds.RAIN;
      }

      @Nonnull
      public SoundEvent getDustSound() {
         return Sounds.DUST;
      }

      public static Weather.Properties mapRainStrength(float str) {
         Weather.Properties result;
         if (str == VANILLA.getLevel()) {
            result = VANILLA;
         } else {
            str = MathStuff.clamp(str, 0.0F, 1.0F);
            result = NONE;

            for(int i = 0; i < values().length; ++i) {
               Weather.Properties p = values()[i];
               if (str <= p.getLevel()) {
                  result = p;
                  break;
               }
            }
         }

         return result;
      }
   }
}

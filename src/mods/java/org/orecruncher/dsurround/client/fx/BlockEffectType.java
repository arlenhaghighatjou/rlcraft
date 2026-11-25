package org.orecruncher.dsurround.client.fx;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.lang.reflect.Constructor;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.orecruncher.dsurround.ModOptions;

public enum BlockEffectType {
   UNKNOWN("UNKNOWN", (Class)null) {
      public boolean isEnabled() {
         return false;
      }
   },
   SOUND("sound", (Class)null) {
      public boolean isEnabled() {
         return false;
      }
   },
   FIREFLY("firefly", FireFlyEffect.class) {
      public boolean isEnabled() {
         return ModOptions.effects.enableFireflies;
      }
   },
   STEAM_JET("steam", SteamJetEffect.class) {
      public boolean isEnabled() {
         return ModOptions.effects.enableSteamJets;
      }
   },
   FIRE_JET("fire", FireJetEffect.class) {
      public boolean isEnabled() {
         return ModOptions.effects.enableFireJets;
      }
   },
   BUBBLE_JET("bubble", BubbleJetEffect.class) {
      public boolean isEnabled() {
         return ModOptions.effects.enableBubbleJets;
      }
   },
   DUST_JET("dust", DustJetEffect.class) {
      public boolean isEnabled() {
         return ModOptions.effects.enableDustJets;
      }
   },
   FOUNTAIN_JET("fountain", FountainJetEffect.class) {
      public boolean isEnabled() {
         return ModOptions.effects.enableFountainJets;
      }
   },
   SPLASH_JET("splash", WaterSplashJetEffect.class) {
      public boolean isEnabled() {
         return ModOptions.effects.enableWaterSplash;
      }
   };

   private static final Map<String, BlockEffectType> typeMap = new Object2ObjectOpenHashMap();
   protected final String name;
   protected Constructor<?> factory;

   @Nonnull
   public static BlockEffectType get(@Nonnull String name) {
      BlockEffectType result = (BlockEffectType)typeMap.get(name);
      return result == null ? UNKNOWN : result;
   }

   private BlockEffectType(@Nonnull String name, @Nullable Class<?> clazz) {
      this.name = name;

      try {
         if (clazz != null) {
            this.factory = clazz.getConstructor(Integer.TYPE);
         }
      } catch (Throwable var6) {
         ;
      }

   }

   @Nonnull
   public String getName() {
      return this.name;
   }

   public abstract boolean isEnabled();

   @Nullable
   public BlockEffect getInstance(int chance) {
      if (this.factory != null) {
         try {
            return (BlockEffect)this.factory.newInstance(chance);
         } catch (Throwable var3) {
            ;
         }
      }

      return null;
   }

   static {
      for(BlockEffectType effect : values()) {
         typeMap.put(effect.getName(), effect);
      }

   }
}

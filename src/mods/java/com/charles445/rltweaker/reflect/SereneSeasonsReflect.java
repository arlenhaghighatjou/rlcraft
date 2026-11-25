package com.charles445.rltweaker.reflect;

import com.charles445.rltweaker.util.ReflectUtil;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockIce;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SereneSeasonsReflect {
   public final Class c_SeasonHelper = Class.forName("sereneseasons.api.season.SeasonHelper");
   public final Method m_SeasonHelper_getSeasonState;
   public final Class c_ISeasonState;
   public final Method m_ISeasonState_getSubSeason;
   public final Class c_Season$SubSeason;
   public final Class c_ModConfig;
   public final Field f_ModConfig_seasons;
   public final Class c_SeasonsConfig;
   public final Method m_SeasonsConfig_isDimensionWhitelisted;
   public final Field f_SeasonsConfig_generateSnowAndIce;
   public final Field f_SeasonsConfig_changeWeatherFrequency;
   public final Class c_BiomeConfig;
   public final Field f_BiomeConfig_biomeDataMap;
   public final Class c_BiomeData;
   public final Field f_BiomeData_enableSeasonalEffects;
   public final Field f_BiomeData_useTropicalSeasons;
   public final Method m_BlockIce_turnIntoWater;

   public SereneSeasonsReflect() throws Exception {
      this.m_SeasonHelper_getSeasonState = this.c_SeasonHelper.getDeclaredMethod("getSeasonState", World.class);
      this.c_ISeasonState = Class.forName("sereneseasons.api.season.ISeasonState");
      this.m_ISeasonState_getSubSeason = this.c_ISeasonState.getDeclaredMethod("getSubSeason");
      this.c_Season$SubSeason = Class.forName("sereneseasons.api.season.Season$SubSeason");
      this.c_ModConfig = Class.forName("sereneseasons.init.ModConfig");
      this.f_ModConfig_seasons = ReflectUtil.findField(this.c_ModConfig, "seasons");
      this.c_SeasonsConfig = Class.forName("sereneseasons.config.SeasonsConfig");
      this.m_SeasonsConfig_isDimensionWhitelisted = ReflectUtil.findMethod(this.c_SeasonsConfig, "isDimensionWhitelisted");
      this.f_SeasonsConfig_generateSnowAndIce = ReflectUtil.findField(this.c_SeasonsConfig, "generateSnowAndIce");
      this.f_SeasonsConfig_changeWeatherFrequency = ReflectUtil.findField(this.c_SeasonsConfig, "changeWeatherFrequency");
      this.c_BiomeConfig = Class.forName("sereneseasons.config.BiomeConfig");
      this.f_BiomeConfig_biomeDataMap = ReflectUtil.findField(this.c_BiomeConfig, "biomeDataMap");
      this.c_BiomeData = Class.forName("sereneseasons.config.json.BiomeData");
      this.f_BiomeData_enableSeasonalEffects = ReflectUtil.findField(this.c_BiomeData, "enableSeasonalEffects");
      this.f_BiomeData_useTropicalSeasons = ReflectUtil.findField(this.c_BiomeData, "useTropicalSeasons");
      this.m_BlockIce_turnIntoWater = ReflectUtil.findMethodAny(BlockIce.class, "turnIntoWater", "turnIntoWater", World.class, BlockPos.class);
   }

   public Object getISeasonState(World world) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return this.m_SeasonHelper_getSeasonState.invoke((Object)null, world);
   }

   public Object getSeasonConfig() throws IllegalArgumentException, IllegalAccessException {
      return this.f_ModConfig_seasons.get((Object)null);
   }

   public boolean getConfigGenerateSnowAndIce(Object config) throws IllegalArgumentException, IllegalAccessException {
      return this.f_SeasonsConfig_generateSnowAndIce.getBoolean(config);
   }

   public boolean getConfigChangeWeatherFrequency(Object config) throws IllegalArgumentException, IllegalAccessException {
      return this.f_SeasonsConfig_changeWeatherFrequency.getBoolean(config);
   }

   public boolean isDimensionWhitelisted(int dimension) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return ((Boolean)this.m_SeasonsConfig_isDimensionWhitelisted.invoke((Object)null, dimension)).booleanValue();
   }

   public Map<ResourceLocation, Object> getBiomeDataMap() throws IllegalArgumentException, IllegalAccessException {
      return (Map)this.f_BiomeConfig_biomeDataMap.get((Object)null);
   }

   public Map<ResourceLocation, Boolean> createBiomeSeasonalEffects() throws IllegalArgumentException, IllegalAccessException {
      Map<ResourceLocation, Boolean> result = new ConcurrentHashMap();

      for(Entry<ResourceLocation, Object> entry : this.getBiomeDataMap().entrySet()) {
         result.put(entry.getKey(), Boolean.valueOf(((Boolean)this.f_BiomeData_enableSeasonalEffects.get(entry.getValue())).booleanValue()));
      }

      return result;
   }

   public Map<ResourceLocation, Boolean> createBiomeTropicalSeasons() throws IllegalArgumentException, IllegalAccessException {
      Map<ResourceLocation, Boolean> result = new ConcurrentHashMap();

      for(Entry<ResourceLocation, Object> entry : this.getBiomeDataMap().entrySet()) {
         result.put(entry.getKey(), Boolean.valueOf(((Boolean)this.f_BiomeData_useTropicalSeasons.get(entry.getValue())).booleanValue()));
      }

      return result;
   }

   public void turnIntoWater(Block blockIce, World world, BlockPos pos) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      this.m_BlockIce_turnIntoWater.invoke(blockIce, world, pos);
   }

   public SereneSeasonsReflect.SubSeason getSubSeason(Object iSeasonState) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      String var2 = ((Enum)this.m_ISeasonState_getSubSeason.invoke(iSeasonState)).name();
      byte var3 = -1;
      switch(var2.hashCode()) {
      case -1928688636:
         if (var2.equals("MID_SPRING")) {
            var3 = 9;
         }
         break;
      case -1924216410:
         if (var2.equals("MID_SUMMER")) {
            var3 = 10;
         }
         break;
      case -1820745540:
         if (var2.equals("MID_WINTER")) {
            var3 = 11;
         }
         break;
      case -169831985:
         if (var2.equals("LATE_AUTUMN")) {
            var3 = 4;
         }
         break;
      case 340804038:
         if (var2.equals("LATE_SPRING")) {
            var3 = 5;
         }
         break;
      case 345276264:
         if (var2.equals("LATE_SUMMER")) {
            var3 = 6;
         }
         break;
      case 448747134:
         if (var2.equals("LATE_WINTER")) {
            var3 = 7;
         }
         break;
      case 1464009362:
         if (var2.equals("EARLY_AUTUMN")) {
            var3 = 0;
         }
         break;
      case 1855642637:
         if (var2.equals("MID_AUTUMN")) {
            var3 = 8;
         }
         break;
      case 1974645385:
         if (var2.equals("EARLY_SPRING")) {
            var3 = 1;
         }
         break;
      case 1979117611:
         if (var2.equals("EARLY_SUMMER")) {
            var3 = 2;
         }
         break;
      case 2082588481:
         if (var2.equals("EARLY_WINTER")) {
            var3 = 3;
         }
      }

      switch(var3) {
      case 0:
         return SereneSeasonsReflect.SubSeason.EARLY_AUTUMN;
      case 1:
         return SereneSeasonsReflect.SubSeason.EARLY_SPRING;
      case 2:
         return SereneSeasonsReflect.SubSeason.EARLY_SUMMER;
      case 3:
         return SereneSeasonsReflect.SubSeason.EARLY_WINTER;
      case 4:
         return SereneSeasonsReflect.SubSeason.LATE_AUTUMN;
      case 5:
         return SereneSeasonsReflect.SubSeason.LATE_SPRING;
      case 6:
         return SereneSeasonsReflect.SubSeason.LATE_SUMMER;
      case 7:
         return SereneSeasonsReflect.SubSeason.LATE_WINTER;
      case 8:
         return SereneSeasonsReflect.SubSeason.MID_AUTUMN;
      case 9:
         return SereneSeasonsReflect.SubSeason.MID_SPRING;
      case 10:
         return SereneSeasonsReflect.SubSeason.MID_SUMMER;
      case 11:
         return SereneSeasonsReflect.SubSeason.MID_WINTER;
      default:
         return null;
      }
   }

   public static enum Season {
      WINTER,
      SPRING,
      SUMMER,
      AUTUMN;

      private Season() {
      }
   }

   public static enum SubSeason {
      EARLY_AUTUMN(SereneSeasonsReflect.Season.AUTUMN),
      EARLY_SPRING(SereneSeasonsReflect.Season.SPRING),
      EARLY_SUMMER(SereneSeasonsReflect.Season.SUMMER),
      EARLY_WINTER(SereneSeasonsReflect.Season.WINTER),
      LATE_AUTUMN(SereneSeasonsReflect.Season.AUTUMN),
      LATE_SPRING(SereneSeasonsReflect.Season.SPRING),
      LATE_SUMMER(SereneSeasonsReflect.Season.SUMMER),
      LATE_WINTER(SereneSeasonsReflect.Season.WINTER),
      MID_AUTUMN(SereneSeasonsReflect.Season.AUTUMN),
      MID_SPRING(SereneSeasonsReflect.Season.SPRING),
      MID_SUMMER(SereneSeasonsReflect.Season.SUMMER),
      MID_WINTER(SereneSeasonsReflect.Season.WINTER);

      private SereneSeasonsReflect.Season season;

      private SubSeason(SereneSeasonsReflect.Season season) {
         this.season = season;
      }

      public SereneSeasonsReflect.Season getSeason() {
         return this.season;
      }
   }
}

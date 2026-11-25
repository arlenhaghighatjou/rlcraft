package com.charles445.simpledifficulty.compat.mod;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.temperature.ModifierBase;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HarvestFestivalModifier extends ModifierBase {
   private boolean enabled;
   private Class HFApi;
   private Class CalendarManager;
   private Class Season;
   private Class CalendarDate;
   private Field f_HFApi_calendar;
   private Object o_CalendarManager;
   private Method m_CalendarManager_getSeasonAtCoordinates;
   private Method m_CalendarManager_getDate;
   private Method m_CalendarDate_getSeason;

   public HarvestFestivalModifier() {
      super("HarvestFestival");

      try {
         this.HFApi = Class.forName("joshie.harvest.api.HFApi");
         this.CalendarManager = Class.forName("joshie.harvest.api.calendar.CalendarManager");
         this.Season = Class.forName("joshie.harvest.api.calendar.Season");
         this.CalendarDate = Class.forName("joshie.harvest.api.calendar.CalendarDate");
         this.f_HFApi_calendar = this.HFApi.getDeclaredField("calendar");
         this.o_CalendarManager = this.CalendarManager.cast(this.f_HFApi_calendar.get(this.CalendarManager.getClass()));
         this.m_CalendarManager_getSeasonAtCoordinates = this.CalendarManager.getMethod("getSeasonAtCoordinates", World.class, BlockPos.class);
         this.m_CalendarManager_getDate = this.CalendarManager.getMethod("getDate", World.class);
         this.m_CalendarDate_getSeason = this.CalendarDate.getMethod("getSeason");
         this.enabled = true;
         if (!this.Season.isEnum()) {
            SimpleDifficulty.logger.error("HarvestFestivalModifier reflection failed! Season was not an enum! Harvest Festival compatibility is now disabled!");
            this.enabled = false;
         }
      } catch (Exception var2) {
         SimpleDifficulty.logger.error("HarvestFestivalModifier reflection failed! Harvest Festival compatibility is now disabled!", var2);
         this.enabled = false;
      }

   }

   public float getWorldInfluence(World world, BlockPos pos) {
      if (this.enabled && world.provider.isSurfaceWorld() && ModConfig.server.compatibility.toggles.harvestFestival) {
         try {
            Object o_SeasonResult = this.m_CalendarManager_getSeasonAtCoordinates.invoke(this.o_CalendarManager, world, pos);
            if (o_SeasonResult == null) {
               Object o_DateResult = this.m_CalendarManager_getDate.invoke(this.o_CalendarManager, world);
               if (o_DateResult != null) {
                  o_SeasonResult = this.m_CalendarDate_getSeason.invoke(this.CalendarDate.cast(o_DateResult));
               }
            }

            if (o_SeasonResult == null) {
               return 0.0F;
            }

            String var7 = ((Enum)o_SeasonResult).name();
            byte var5 = -1;
            switch(var7.hashCode()) {
            case -1842350579:
               if (var7.equals("SPRING")) {
                  var5 = 0;
               }
               break;
            case -1837878353:
               if (var7.equals("SUMMER")) {
                  var5 = 1;
               }
               break;
            case -1734407483:
               if (var7.equals("WINTER")) {
                  var5 = 3;
               }
               break;
            case 1941980694:
               if (var7.equals("AUTUMN")) {
                  var5 = 2;
               }
            }

            switch(var5) {
            case 0:
               return this.applyUndergroundEffect((float)ModConfig.server.compatibility.harvestfestival.seasonSpring, world, pos);
            case 1:
               return this.applyUndergroundEffect((float)ModConfig.server.compatibility.harvestfestival.seasonSummer, world, pos);
            case 2:
               return this.applyUndergroundEffect((float)ModConfig.server.compatibility.harvestfestival.seasonAutumn, world, pos);
            case 3:
               return this.applyUndergroundEffect((float)ModConfig.server.compatibility.harvestfestival.seasonWinter, world, pos);
            default:
               return 0.0F;
            }
         } catch (Exception var6) {
            SimpleDifficulty.logger.error("HarvestFestivalModifier reflection failed during getWorldInfluence! Harvest Festival compatibility is now disabled!", var6);
            this.enabled = false;
         }
      }

      return 0.0F;
   }
}

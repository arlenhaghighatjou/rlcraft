package com.charles445.simpledifficulty.compat.mod;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.temperature.ModifierBase;
import java.lang.reflect.Method;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SereneSeasonsModifier extends ModifierBase {
   private Class SeasonHelper;
   private Class ISeasonState;
   private Class SubSeason;
   private Method getSeasonState;
   private Method getSubSeason;
   private boolean enabled;

   public SereneSeasonsModifier() {
      super("SereneSeasons");

      try {
         this.SeasonHelper = Class.forName("sereneseasons.api.season.SeasonHelper");
         this.ISeasonState = Class.forName("sereneseasons.api.season.ISeasonState");
         this.SubSeason = Class.forName("sereneseasons.api.season.Season$SubSeason");
         this.getSeasonState = this.SeasonHelper.getDeclaredMethod("getSeasonState", World.class);
         this.getSubSeason = this.ISeasonState.getDeclaredMethod("getSubSeason");
         this.enabled = true;
         if (!this.SubSeason.isEnum()) {
            SimpleDifficulty.logger.error("ModifierSeason reflection failed! SubSeason was not an enum! Serene Seasons compatibility is now disabled!");
            this.enabled = false;
         }
      } catch (Exception var2) {
         SimpleDifficulty.logger.error("ModifierSeason reflection failed! Serene Seasons compatibility is now disabled!", var2);
         this.enabled = false;
      }

   }

   public float getWorldInfluence(World world, BlockPos pos) {
      if (this.enabled && world.provider.isSurfaceWorld() && ModConfig.server.compatibility.toggles.sereneSeasons) {
         try {
            String e = ((Enum)this.getSubSeason.invoke(this.ISeasonState.cast(this.getSeasonState.invoke((Object)null, world)))).name();
            byte var4 = -1;
            switch(e.hashCode()) {
            case -1928688636:
               if (e.equals("MID_SPRING")) {
                  var4 = 9;
               }
               break;
            case -1924216410:
               if (e.equals("MID_SUMMER")) {
                  var4 = 10;
               }
               break;
            case -1820745540:
               if (e.equals("MID_WINTER")) {
                  var4 = 11;
               }
               break;
            case -169831985:
               if (e.equals("LATE_AUTUMN")) {
                  var4 = 4;
               }
               break;
            case 340804038:
               if (e.equals("LATE_SPRING")) {
                  var4 = 5;
               }
               break;
            case 345276264:
               if (e.equals("LATE_SUMMER")) {
                  var4 = 6;
               }
               break;
            case 448747134:
               if (e.equals("LATE_WINTER")) {
                  var4 = 7;
               }
               break;
            case 1464009362:
               if (e.equals("EARLY_AUTUMN")) {
                  var4 = 0;
               }
               break;
            case 1855642637:
               if (e.equals("MID_AUTUMN")) {
                  var4 = 8;
               }
               break;
            case 1974645385:
               if (e.equals("EARLY_SPRING")) {
                  var4 = 1;
               }
               break;
            case 1979117611:
               if (e.equals("EARLY_SUMMER")) {
                  var4 = 2;
               }
               break;
            case 2082588481:
               if (e.equals("EARLY_WINTER")) {
                  var4 = 3;
               }
            }

            switch(var4) {
            case 0:
               return this.applyUndergroundEffect((float)ModConfig.server.compatibility.sereneseasons.seasonEarlyAutumn, world, pos);
            case 1:
               return this.applyUndergroundEffect((float)ModConfig.server.compatibility.sereneseasons.seasonEarlySpring, world, pos);
            case 2:
               return this.applyUndergroundEffect((float)ModConfig.server.compatibility.sereneseasons.seasonEarlySummer, world, pos);
            case 3:
               return this.applyUndergroundEffect((float)ModConfig.server.compatibility.sereneseasons.seasonEarlyWinter, world, pos);
            case 4:
               return this.applyUndergroundEffect((float)ModConfig.server.compatibility.sereneseasons.seasonLateAutumn, world, pos);
            case 5:
               return this.applyUndergroundEffect((float)ModConfig.server.compatibility.sereneseasons.seasonLateSpring, world, pos);
            case 6:
               return this.applyUndergroundEffect((float)ModConfig.server.compatibility.sereneseasons.seasonLateSummer, world, pos);
            case 7:
               return this.applyUndergroundEffect((float)ModConfig.server.compatibility.sereneseasons.seasonLateWinter, world, pos);
            case 8:
               return this.applyUndergroundEffect((float)ModConfig.server.compatibility.sereneseasons.seasonMidAutumn, world, pos);
            case 9:
               return this.applyUndergroundEffect((float)ModConfig.server.compatibility.sereneseasons.seasonMidSpring, world, pos);
            case 10:
               return this.applyUndergroundEffect((float)ModConfig.server.compatibility.sereneseasons.seasonMidSummer, world, pos);
            case 11:
               return this.applyUndergroundEffect((float)ModConfig.server.compatibility.sereneseasons.seasonMidWinter, world, pos);
            default:
               return 0.0F;
            }
         } catch (Exception var5) {
            SimpleDifficulty.logger.error("ModifierSeason reflection failed during getWorldInfluence! Serene Seasons compatibility is now disabled!", var5);
            this.enabled = false;
         }
      }

      return 0.0F;
   }
}

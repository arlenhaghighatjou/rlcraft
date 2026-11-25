package org.orecruncher.dshuds.compat;

import javax.annotation.Nonnull;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sereneseasons.api.season.ISeasonState;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.Season.SubSeason;

@SideOnly(Side.CLIENT)
public enum SeasonType {
   NONE("noseason"),
   SPRING("spring"),
   SUMMER("summer"),
   AUTUMN("autumn"),
   WINTER("winter");

   private final String val;

   private SeasonType(@Nonnull String val) {
      this.val = val;
   }

   @Nonnull
   public String getValue() {
      return this.val;
   }

   @Nonnull
   public static SeasonType getSeasonType(@Nonnull ISeasonState state) {
      if (state != null && state.getSeason() != null) {
         Season season = state.getSeason();
         switch(season) {
         case SUMMER:
            return SUMMER;
         case AUTUMN:
            return AUTUMN;
         case WINTER:
            return WINTER;
         case SPRING:
            return SPRING;
         default:
            return NONE;
         }
      } else {
         return NONE;
      }
   }

   @Nonnull
   public static SeasonType.SubType getSeasonSubType(@Nonnull ISeasonState state) {
      SubSeason sub = state.getSubSeason();
      switch(sub) {
      case EARLY_SUMMER:
      case EARLY_AUTUMN:
      case EARLY_WINTER:
      case EARLY_SPRING:
         return SeasonType.SubType.EARLY;
      case MID_SUMMER:
      case MID_AUTUMN:
      case MID_WINTER:
      case MID_SPRING:
         return SeasonType.SubType.MID;
      case LATE_SUMMER:
      case LATE_AUTUMN:
      case LATE_WINTER:
      case LATE_SPRING:
         return SeasonType.SubType.LATE;
      default:
         return SeasonType.SubType.NONE;
      }
   }

   public static enum SubType {
      NONE("nosubtype"),
      EARLY("early"),
      MID("mid"),
      LATE("late");

      private final String val;

      private SubType(@Nonnull String val) {
         this.val = val;
      }

      @Nonnull
      public String getValue() {
         return this.val;
      }
   }
}

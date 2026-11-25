package sereneseasons.season;

import com.google.common.base.Preconditions;
import sereneseasons.api.config.SeasonsOption;
import sereneseasons.api.config.SyncedConfig;
import sereneseasons.api.season.ISeasonState;
import sereneseasons.api.season.Season;

public final class SeasonTime implements ISeasonState {
   public static final SeasonTime ZERO = new SeasonTime(0);
   public final int time;

   public SeasonTime(int time) {
      Preconditions.checkArgument(time >= 0, "Time cannot be negative!");
      this.time = time;
   }

   public int getDayDuration() {
      return SyncedConfig.getIntValue(SeasonsOption.DAY_DURATION);
   }

   public int getSubSeasonDuration() {
      return this.getDayDuration() * SyncedConfig.getIntValue(SeasonsOption.SUB_SEASON_DURATION);
   }

   public int getSeasonDuration() {
      return this.getSubSeasonDuration() * 3;
   }

   public int getCycleDuration() {
      return this.getSubSeasonDuration() * Season.SubSeason.VALUES.length;
   }

   public int getSeasonCycleTicks() {
      return this.time;
   }

   public int getDay() {
      return this.time / this.getDayDuration();
   }

   public Season.SubSeason getSubSeason() {
      int index = this.time / this.getSubSeasonDuration() % Season.SubSeason.VALUES.length;
      return Season.SubSeason.VALUES[index];
   }

   public Season getSeason() {
      return this.getSubSeason().getSeason();
   }

   public Season.TropicalSeason getTropicalSeason() {
      int index = ((this.time / this.getSubSeasonDuration() + 11) / 2 + 5) % Season.TropicalSeason.VALUES.length;
      return Season.TropicalSeason.VALUES[index];
   }
}

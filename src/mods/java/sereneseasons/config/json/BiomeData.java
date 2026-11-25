package sereneseasons.config.json;

import com.google.gson.annotations.SerializedName;

public class BiomeData {
   @SerializedName("enable_seasonal_effects")
   public boolean enableSeasonalEffects;
   @SerializedName("use_tropical_seasons")
   public boolean useTropicalSeasons;
   @SerializedName("disable_crops")
   public boolean disableCrops;

   public BiomeData(boolean enableSeasonalEffects, boolean useTropicalSeasons, boolean disableCrops) {
      this.enableSeasonalEffects = enableSeasonalEffects;
      this.useTropicalSeasons = useTropicalSeasons;
      this.disableCrops = disableCrops;
   }
}

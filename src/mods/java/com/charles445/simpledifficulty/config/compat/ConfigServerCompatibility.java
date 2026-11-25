package com.charles445.simpledifficulty.config.compat;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RangeInt;

public class ConfigServerCompatibility {
   @Comment({"Built-In Compatibility Toggles - Turn compatibility for mods on or off"})
   @Name("Built-In Compatibility Toggles")
   public final ConfigServerCompatibility.ConfigToggles toggles = new ConfigServerCompatibility.ConfigToggles();
   @Comment({"Armor Underwear Configurations"})
   @Name("Armor Underwear")
   public final ConfigServerCompatibility.ConfigArmorUnderwear auw = new ConfigServerCompatibility.ConfigArmorUnderwear();
   @Comment({"Harvest Festival Configurations"})
   @Name("Harvest Festival")
   public final ConfigServerCompatibility.ConfigHarvestFestival harvestfestival = new ConfigServerCompatibility.ConfigHarvestFestival();
   @Comment({"Serene Seasons Configurations"})
   @Name("Serene Seasons")
   public final ConfigServerCompatibility.ConfigSereneSeasons sereneseasons = new ConfigServerCompatibility.ConfigSereneSeasons();

   public ConfigServerCompatibility() {
   }

   public class ConfigArmorUnderwear {
      @Comment({"Goopak Temperature Modifier - Effect of a Goopak on temperature (won't change tooltip)"})
      @Name("GoopakTemperatureModifier")
      @RangeDouble(
         min = 0.0D
      )
      public double goopakTemperatureModifier = 2.0D;
      @Comment({"Goopak Maximum Active - How many Goopaks can stack their effects at once"})
      @Name("GoopakMaximumActive")
      @RangeInt(
         min = 1
      )
      public int goopakMaximumActive = 5;
      @Comment({"Ozzy Base Range - The base temperature range of an Ozzy Liner"})
      @Name("OzzyBaseRange")
      @RangeDouble(
         min = 0.0D
      )
      public double ozzyBaseRange = 3.0D;
      @Comment({"Ozzy Extra Range - The added temperature range when upgrading an Ozzy Liner"})
      @Name("OzzyExtraRange")
      @RangeDouble(
         min = 0.0D
      )
      public double ozzyExtraRange = 3.0D;
      @Comment({"Liner Multiplier - Multiplier for the effect of normal liners"})
      @Name("LinerMultiplier")
      @RangeDouble(
         min = 0.0D
      )
      public double linerMultiplier = 1.0D;

      public ConfigArmorUnderwear() {
      }
   }

   public class ConfigHarvestFestival {
      @Comment({"Season Winter - Temperature change during the Harvest Festival season"})
      @Name("SeasonWinter")
      public int seasonWinter = -10;
      @Comment({"Season Spring - Temperature change during the Harvest Festival season"})
      @Name("SeasonSpring")
      public int seasonSpring = 0;
      @Comment({"Season Summer - Temperature change during the Harvest Festival season"})
      @Name("SeasonSummer")
      public int seasonSummer = 4;
      @Comment({"Season Autumn - Temperature change during the Harvest Festival season"})
      @Name("SeasonAutumn")
      public int seasonAutumn = 0;

      public ConfigHarvestFestival() {
      }
   }

   public class ConfigSereneSeasons {
      @Comment({"Season Early Winter - Temperature change during the Serene Seasons season"})
      @Name("SeasonEarlyWinter")
      @RangeInt
      public int seasonEarlyWinter = -7;
      @Comment({"Season Mid Winter - Temperature change during the Serene Seasons season"})
      @Name("SeasonMidWinter")
      @RangeInt
      public int seasonMidWinter = -14;
      @Comment({"Season Late Winter - Temperature change during the Serene Seasons season"})
      @Name("SeasonLateWinter")
      @RangeInt
      public int seasonLateWinter = -7;
      @Comment({"Season Early Spring - Temperature change during the Serene Seasons season"})
      @Name("SeasonEarlySpring")
      @RangeInt
      public int seasonEarlySpring = -3;
      @Comment({"Season Mid Spring - Temperature change during the Serene Seasons season"})
      @Name("SeasonMidSpring")
      @RangeInt
      public int seasonMidSpring = 0;
      @Comment({"Season Late Spring - Temperature change during the Serene Seasons season"})
      @Name("SeasonLateSpring")
      @RangeInt
      public int seasonLateSpring = 2;
      @Comment({"Season Early Summer - Temperature change during the Serene Seasons season"})
      @Name("SeasonEarlySummer")
      @RangeInt
      public int seasonEarlySummer = 3;
      @Comment({"Season Mid Summer - Temperature change during the Serene Seasons season"})
      @Name("SeasonMidSummer")
      @RangeInt
      public int seasonMidSummer = 5;
      @Comment({"Season Late Summer - Temperature change during the Serene Seasons season"})
      @Name("SeasonLateSummer")
      @RangeInt
      public int seasonLateSummer = 3;
      @Comment({"Season Early Autumn - Temperature change during the Serene Seasons season"})
      @Name("SeasonEarlyAutumn")
      @RangeInt
      public int seasonEarlyAutumn = 2;
      @Comment({"Season Mid Autumn - Temperature change during the Serene Seasons season"})
      @Name("SeasonMidAutumn")
      @RangeInt
      public int seasonMidAutumn = 0;
      @Comment({"Season Late Autumn - Temperature change during the Serene Seasons season"})
      @Name("SeasonLateAutumn")
      @RangeInt
      public int seasonLateAutumn = -3;

      public ConfigSereneSeasons() {
      }
   }

   public class ConfigToggles {
      @Comment({"Enable Armor Underwear - Built-In Compatibility"})
      @Name("EnableArmorUnderwear")
      public boolean armorUnderwear = true;
      @Comment({"Enable Baubles - Built-In Compatibility"})
      @Name("EnableBaubles")
      public boolean baubles = true;
      @Comment({"Enable Harvest Festival - Built-In Compatibility"})
      @Name("EnableHarvestFestival")
      public boolean harvestFestival = true;
      @Comment({"Enable Inspirations - Built-In Compatibility"})
      @Name("EnableInspirations")
      public boolean inspirations = true;
      @Comment({"Enable OreExcavation - Built-In Compatibility"})
      @Name("EnableOreExcavation")
      public boolean oreExcavation = true;
      @Comment({"Enable Serene Seasons - Built-In Compatibility"})
      @Name("EnableSereneSeasons")
      public boolean sereneSeasons = true;

      public ConfigToggles() {
      }
   }
}

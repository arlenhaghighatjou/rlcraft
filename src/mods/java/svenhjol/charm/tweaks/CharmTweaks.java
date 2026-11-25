package svenhjol.charm.tweaks;

import svenhjol.charm.tweaks.feature.CactusWater;
import svenhjol.charm.tweaks.feature.CauldronWaterSource;
import svenhjol.charm.tweaks.feature.CompassBinding;
import svenhjol.charm.tweaks.feature.DispensersUseAnyBow;
import svenhjol.charm.tweaks.feature.ExtraRecords;
import svenhjol.charm.tweaks.feature.FurnacesRecycleMore;
import svenhjol.charm.tweaks.feature.GoldToolImprovements;
import svenhjol.charm.tweaks.feature.LeatherArmorInvisibility;
import svenhjol.charm.tweaks.feature.PickaxesBreakPistons;
import svenhjol.charm.tweaks.feature.RandomAnimalTextures;
import svenhjol.charm.tweaks.feature.RecordsStopBackgroundMusic;
import svenhjol.charm.tweaks.feature.RemoveNitwits;
import svenhjol.charm.tweaks.feature.RestrictFurnaceInput;
import svenhjol.charm.tweaks.feature.ShearedMelons;
import svenhjol.charm.tweaks.feature.SpongesReduceFallDamage;
import svenhjol.charm.tweaks.feature.StackableEnchantedBooks;
import svenhjol.charm.tweaks.feature.StackableMilkBuckets;
import svenhjol.charm.tweaks.feature.StackablePotions;
import svenhjol.charm.tweaks.feature.TamedAnimalsHealing;
import svenhjol.charm.tweaks.feature.TamedAnimalsNoDamage;
import svenhjol.charm.tweaks.feature.UseTotemFromInventory;
import svenhjol.charm.tweaks.feature.WaterInHotBiomes;
import svenhjol.charm.tweaks.feature.WitchHatProtection;
import svenhjol.charm.tweaks.feature.WitchesDropDecay;
import svenhjol.charm.tweaks.feature.WitchesDropLuck;
import svenhjol.meson.Module;

public class CharmTweaks extends Module {
   public CharmTweaks() {
      this.features.add(new CactusWater());
      this.features.add(new CauldronWaterSource());
      this.features.add(new CompassBinding());
      this.features.add(new DispensersUseAnyBow());
      this.features.add(new ExtraRecords());
      this.features.add(new FurnacesRecycleMore());
      this.features.add(new GoldToolImprovements());
      this.features.add(new LeatherArmorInvisibility());
      this.features.add(new PickaxesBreakPistons());
      this.features.add(new RandomAnimalTextures());
      this.features.add(new RecordsStopBackgroundMusic());
      this.features.add(new RemoveNitwits());
      this.features.add(new RestrictFurnaceInput());
      this.features.add(new ShearedMelons());
      this.features.add(new SpongesReduceFallDamage());
      this.features.add(new StackableEnchantedBooks());
      this.features.add(new StackableMilkBuckets());
      this.features.add(new StackablePotions());
      this.features.add(new TamedAnimalsHealing());
      this.features.add(new TamedAnimalsNoDamage());
      this.features.add(new UseTotemFromInventory());
      this.features.add(new WaterInHotBiomes());
      this.features.add(new WitchesDropDecay());
      this.features.add(new WitchesDropLuck());
      this.features.add(new WitchHatProtection());
   }
}

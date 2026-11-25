package dev.satyrn.wolfarmor.config.settings;

import dev.satyrn.wolfarmor.api.config.settings.ValueSetting;
import dev.satyrn.wolfarmor.util.WolfFoodStatsLevel;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.config.Configuration;

public class WolfFoodStatsSetting extends ValueSetting<WolfFoodStatsLevel> {
   public WolfFoodStatsSetting() {
      super(WolfFoodStatsLevel.DISABLED);
   }

   public void saveToConfiguration(@Nonnull Configuration config) {
      this.getConfigurationProperty(config).set(Objects.toString(this.getValue()).toLowerCase());
   }

   public WolfFoodStatsLevel readTag(NBTBase tag) {
      return this.parse(((NBTTagString)tag).getString());
   }

   public NBTBase writeTag(WolfFoodStatsLevel value) {
      return new NBTTagString(value.name());
   }

   @Nonnull
   public WolfFoodStatsLevel parse(String value) {
      Optional<WolfFoodStatsLevel> stats = Arrays.stream(WolfFoodStatsLevel.values()).filter((option) -> {
         return option.name().equalsIgnoreCase(value.trim());
      }).findAny();
      return (WolfFoodStatsLevel)stats.orElse(WolfFoodStatsLevel.DISABLED);
   }
}

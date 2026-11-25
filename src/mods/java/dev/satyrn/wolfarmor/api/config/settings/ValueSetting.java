package dev.satyrn.wolfarmor.api.config.settings;

import java.util.Objects;
import javax.annotation.Nonnull;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public abstract class ValueSetting<T> extends Setting<T> {
   public ValueSetting(T defaultValue) {
      super(defaultValue);
   }

   public void loadFromConfiguration(@Nonnull Configuration config) {
      this.setValue(this.parse(this.getConfigurationProperty(config).getString()));
   }

   public void saveToConfiguration(@Nonnull Configuration config) {
      this.getConfigurationProperty(config).set(Objects.toString(this.getValue()));
   }

   @Nonnull
   public Property getConfigurationProperty(@Nonnull Configuration config) {
      return config.get(this.getCategory(), this.getName(), Objects.toString(this.getDefaultValue()), this.getComment(), this.getPropertyType());
   }
}

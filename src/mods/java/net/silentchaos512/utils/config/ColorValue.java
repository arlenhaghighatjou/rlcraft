package net.silentchaos512.utils.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.ConfigSpec;
import java.util.function.Consumer;
import net.silentchaos512.utils.Color;

public class ColorValue extends ConfigValue<Color> {
   private final int defaultValue;

   ColorValue(int defaultValue, ConfigSpecWrapper wrapper, String path, Consumer<ConfigSpec> handleSpec, Consumer<CommentedConfig> handleConfig) {
      super(wrapper, path, handleSpec, handleConfig);
      this.defaultValue = defaultValue;
   }

   public Color get() {
      return Color.tryParse((String)this.config.get(this.path), this.defaultValue);
   }

   public static ConfigValue.Builder builder(ConfigSpecWrapper wrapper, String path) {
      return new ConfigValue.Builder(wrapper, path);
   }
}

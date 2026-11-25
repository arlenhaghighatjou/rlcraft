package net.silentchaos512.utils.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.ConfigSpec;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Consumer;

public class EnumValue<E extends Enum<E>> extends ConfigValue<E> {
   private final E defaultValue;

   EnumValue(E defaultValue, ConfigSpecWrapper wrapper, String path, Consumer<ConfigSpec> handleSpec, Consumer<CommentedConfig> handleConfig) {
      super(wrapper, path, handleSpec, handleConfig);
      this.defaultValue = defaultValue;
   }

   public E get() {
      String name = (String)this.config.get(this.path);

      for(E e : (Enum[])this.defaultValue.getDeclaringClass().getEnumConstants()) {
         if (e.name().equalsIgnoreCase(name)) {
            return e;
         }
      }

      return this.defaultValue;
   }

   public static <E extends Enum<E>> String allValuesComment(Class<E> clazz) {
      return validValuesComment(EnumSet.allOf(clazz));
   }

   public static <E extends Enum<E>> String validValuesComment(Set<E> validValues) {
      StringBuilder builder = new StringBuilder();
      validValues.forEach((e) -> {
         builder.append(builder.length() == 0 ? "Valid values: " : ", ");
         builder.append(e.name());
      });
      return builder.toString();
   }
}

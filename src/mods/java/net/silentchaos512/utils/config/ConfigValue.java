package net.silentchaos512.utils.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.ConfigSpec;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.silentchaos512.utils.Color;

public class ConfigValue<V> {
   final CommentedConfig config;
   final ConfigSpec spec;
   final String path;
   public static final Predicate<Object> IS_STRING = (o) -> {
      return o instanceof String;
   };
   public static final Predicate<Object> IS_NONEMPTY_STRING = (o) -> {
      return o instanceof String && !((String)o).isEmpty();
   };

   ConfigValue(ConfigSpecWrapper wrapper, String path, Consumer<ConfigSpec> handleSpec, Consumer<CommentedConfig> handleConfig) {
      this.config = wrapper.config;
      this.spec = wrapper.spec;
      this.path = path;
      handleSpec.accept(this.spec);
      handleConfig.accept(this.config);
   }

   public V get() {
      return (V)this.config.get(this.path);
   }

   public static ConfigValue.Builder builder(ConfigSpecWrapper wrapper, String path) {
      return new ConfigValue.Builder(wrapper, path);
   }

   public static class Builder {
      private final ConfigSpecWrapper wrapper;
      private final String path;
      private final Collection<Consumer<CommentedConfig>> handleConfig = new ArrayList();
      private final Collection<Consumer<ConfigSpec>> handleSpec = new ArrayList();

      public Builder(ConfigSpecWrapper wrapper, String path) {
         this.wrapper = wrapper;
         this.path = path;
      }

      public String getPath() {
         return this.path;
      }

      public ConfigValue.Builder comment(String comment) {
         return this.handleConfig((c) -> {
            c.setComment(this.path, comment);
         });
      }

      public ConfigValue.Builder comment(String firstLine, String... rest) {
         StringBuilder builder = new StringBuilder(firstLine);

         for(String str : rest) {
            builder.append("\n").append(str);
         }

         return this.comment(builder.toString());
      }

      public ConfigValue.Builder handleSpec(Consumer<ConfigSpec> handler) {
         this.handleSpec.add(handler);
         return this;
      }

      public ConfigValue.Builder handleConfig(Consumer<CommentedConfig> handler) {
         this.handleConfig.add(handler);
         return this;
      }

      public BooleanValue define(boolean defaultValue) {
         this.handleSpec((spec) -> {
            spec.define(this.path, Boolean.valueOf(defaultValue), (o) -> {
               return o instanceof Boolean;
            });
         });
         return new BooleanValue(this.wrapper, this.path, this::doSpec, this::doConfig);
      }

      public DoubleValue defineInRange(double defaultValue, double min, double max) {
         return this.defineInRange(() -> {
            return defaultValue;
         }, min, max);
      }

      public DoubleValue defineInRange(Supplier<Double> defaultValue, double min, double max) {
         this.handleSpec((spec) -> {
            spec.defineInRange(this.path, defaultValue, Double.valueOf(min), Double.valueOf(max));
         });
         return new DoubleValue(this.wrapper, this.path, this::doSpec, this::doConfig);
      }

      public IntValue defineInRange(int defaultValue, int min, int max) {
         return this.defineInRange(() -> {
            return defaultValue;
         }, min, max);
      }

      public IntValue defineInRange(Supplier<Integer> defaultValue, int min, int max) {
         this.handleSpec((spec) -> {
            spec.defineInRange(this.path, defaultValue, Integer.valueOf(min), Integer.valueOf(max));
         });
         return new IntValue(this.wrapper, this.path, this::doSpec, this::doConfig);
      }

      public LongValue defineInRange(long defaultValue, long min, long max) {
         return this.defineInRange(() -> {
            return defaultValue;
         }, min, max);
      }

      public LongValue defineInRange(Supplier<Long> defaultValue, long min, long max) {
         this.handleSpec((spec) -> {
            spec.defineInRange(this.path, defaultValue, Long.valueOf(min), Long.valueOf(max));
         });
         return new LongValue(this.wrapper, this.path, this::doSpec, this::doConfig);
      }

      public ColorValue defineColor(int defaultValue) {
         this.handleSpec((spec) -> {
            spec.define(this.path, Color.format(defaultValue), (o) -> {
               return o instanceof String && Color.validate((String)o);
            });
         });
         return new ColorValue(defaultValue, this.wrapper, this.path, this::doSpec, this::doConfig);
      }

      public IntValue defineColorInt(int defaultValue) {
         this.handleSpec((spec) -> {
            spec.define(this.path, Color.format(defaultValue), (o) -> {
               return o instanceof String && Color.validate((String)o);
            });
         });
         return new IntValue(this.wrapper, this.path, this::doSpec, this::doConfig) {
            public Integer get() {
               return Color.parseInt((String)this.config.get(this.path));
            }
         };
      }

      public <E extends Enum<E>> EnumValue<E> defineEnum(E defaultValue) {
         EnumSet<E> validValues = EnumSet.allOf(defaultValue.getDeclaringClass());
         return this.<E>defineEnum(defaultValue, validValues);
      }

      public <E extends Enum<E>> EnumValue<E> defineEnum(E defaultValue, Set<E> validValues) {
         this.handleSpec((spec) -> {
            spec.define(this.path, defaultValue.name(), (o) -> {
               return validateEnum(defaultValue.getDeclaringClass(), validValues, o);
            });
         });
         return new EnumValue<E>(defaultValue, this.wrapper, this.path, this::doSpec, this::doConfig);
      }

      private static <E extends Enum<E>> boolean validateEnum(Class<E> clazz, Set<E> validValues, Object o) {
         if (!(o instanceof String)) {
            return false;
         } else {
            String name = (String)o;

            for(E e : validValues) {
               if (e.name().equalsIgnoreCase(name)) {
                  return true;
               }
            }

            return false;
         }
      }

      public StringValue defineString(String defaultValue) {
         return this.defineString(() -> {
            return defaultValue;
         }, ConfigValue.IS_STRING);
      }

      public StringValue defineString(String defaultValue, Predicate<Object> validator) {
         return this.defineString(() -> {
            return defaultValue;
         }, validator);
      }

      public StringValue defineString(Supplier<String> defaultValue) {
         return this.defineString(defaultValue, ConfigValue.IS_STRING);
      }

      public StringValue defineString(Supplier<String> defaultValue, Predicate<Object> validator) {
         this.handleSpec((spec) -> {
            spec.define(this.path, defaultValue, validator);
         });
         return new StringValue(this.wrapper, this.path, this::doSpec, this::doConfig);
      }

      public <E> ConfigValue<List<? extends E>> defineList(List<? extends E> defaultValues, Predicate<Object> elementValidator) {
         return this.defineList(() -> {
            return defaultValues;
         }, elementValidator);
      }

      public <E> ConfigValue<List<? extends E>> defineList(Supplier<List<? extends E>> defaultValues, Predicate<Object> elementValidator) {
         this.handleSpec((spec) -> {
            spec.defineList(this.path, defaultValues::get, elementValidator);
         });
         return new ConfigValue<List<? extends E>>(this.wrapper, this.path, this::doSpec, this::doConfig);
      }

      public <S> ConfigValue<S> define(S defaultValue) {
         return this.define(() -> {
            return (ConfigValue<S>)defaultValue;
         }, (o) -> {
            return o != null && defaultValue.getClass().isAssignableFrom(o.getClass());
         });
      }

      public <S> ConfigValue<S> define(S defaultValue, Predicate<Object> validator) {
         return this.define(() -> {
            return (ConfigValue<S>)defaultValue;
         }, validator);
      }

      public <S> ConfigValue<S> define(Supplier<S> defaultValue, Predicate<Object> validator) {
         this.handleSpec((spec) -> {
            spec.define(this.path, defaultValue, validator);
         });
         return new ConfigValue<S>(this.wrapper, this.path, this::doSpec, this::doConfig);
      }

      private void doSpec(ConfigSpec spec) {
         this.handleSpec.forEach((h) -> {
            h.accept(spec);
         });
      }

      private void doConfig(CommentedConfig config) {
         this.handleConfig.forEach((h) -> {
            h.accept(config);
         });
      }
   }
}

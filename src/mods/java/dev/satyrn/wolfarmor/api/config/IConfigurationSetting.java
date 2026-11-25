package dev.satyrn.wolfarmor.api.config;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public interface IConfigurationSetting<T> {
   @Nonnull
   Property getConfigurationProperty(@Nonnull Configuration var1);

   @Nonnull
   String getFullName();

   @Nullable
   T getDefaultValue();

   /** @deprecated */
   @Deprecated
   @Nullable
   default T getSynchronizedValue() {
      return (T)this.getSyncedValue();
   }

   @Nullable
   T getSyncedValue();

   @Nonnull
   T getCurrentValue();

   @Nonnull
   IConfigurationSetting<T> setValue(@Nullable T var1);

   @Nonnull
   String getName();

   @Nonnull
   IConfigurationSetting<T> setName(@Nonnull String var1);

   @Nonnull
   String getCategory();

   @Nonnull
   IConfigurationSetting<T> setCategory(@Nonnull String var1);

   @Nonnull
   String getComment();

   @Nonnull
   IConfigurationSetting<T> setComment(@Nonnull String var1);

   /** @deprecated */
   @Deprecated
   default boolean getIsSynchronized() {
      return this.getSynchronizes();
   }

   /** @deprecated */
   @Deprecated
   default IConfigurationSetting<T> setIsSynchronized(boolean value) {
      return this.setSynchronizes(value);
   }

   boolean getSynchronizes();

   @Nonnull
   IConfigurationSetting<T> setSynchronizes(boolean var1);

   void loadFromConfiguration(@Nonnull Configuration var1);

   void saveToConfiguration(@Nonnull Configuration var1);

   @Nonnull
   T parse(String var1);
}

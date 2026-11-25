package dev.satyrn.wolfarmor.api.config.settings;

import dev.satyrn.wolfarmor.api.config.IConfigurationSetting;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTBase;
import net.minecraftforge.common.config.Property.Type;

public abstract class Setting<T> implements IConfigurationSetting<T> {
   private final T defaultValue;
   private T value;
   private T syncedValue;
   private boolean synchronizes;
   private boolean isSynchronized;
   private String category = "";
   private String name = "";
   private String comment = "";
   private Type propertyType = Type.STRING;
   private Setting.ReloadAction reloadAction = Setting.ReloadAction.NONE;
   private String configWidgetClassName = "";

   public Setting(T defaultValue) {
      this.defaultValue = defaultValue;
   }

   @Nonnull
   public String getName() {
      return this.name;
   }

   @Nonnull
   public Setting<T> setName(@Nonnull String value) {
      this.name = value;
      return this;
   }

   @Nonnull
   public String getCategory() {
      return this.category;
   }

   @Nonnull
   public Setting<T> setCategory(@Nonnull String value) {
      this.category = value;
      return this;
   }

   @Nonnull
   public String getComment() {
      return this.comment;
   }

   @Nonnull
   public Setting<T> setComment(@Nonnull String value) {
      this.comment = value;
      return this;
   }

   @Nonnull
   protected Type getPropertyType() {
      return this.propertyType;
   }

   protected void setPropertyType(@Nonnull Type value) {
      this.propertyType = value;
   }

   @Nullable
   protected T getValue() {
      return this.value;
   }

   @Nonnull
   public Setting<T> setValue(T value) {
      this.value = value;
      return this;
   }

   public boolean getSynchronizes() {
      return this.synchronizes;
   }

   @Nonnull
   public Setting<T> setSynchronizes(boolean value) {
      this.synchronizes = value;
      return this;
   }

   public T getDefaultValue() {
      return this.defaultValue;
   }

   public T getSyncedValue() {
      return (T)(this.synchronizes ? this.syncedValue : null);
   }

   public boolean getIsCurrentlySynchronized() {
      return this.synchronizes && this.isSynchronized;
   }

   @Nonnull
   public String getFullName() {
      return String.format("%s.%s", this.category, this.name);
   }

   @Nonnull
   public T getCurrentValue() {
      if (this.synchronizes && this.isSynchronized) {
         return (T)(this.syncedValue == null ? this.defaultValue : this.syncedValue);
      } else {
         return (T)(this.value == null ? this.defaultValue : this.value);
      }
   }

   public void readSynchronized(NBTBase tag) {
      this.isSynchronized = true;
      this.syncedValue = (T)this.readTag(tag);
   }

   public NBTBase writeSynchronized() {
      return this.writeTag(this.value);
   }

   public Setting<T> setReloadAction(Setting.ReloadAction reloadAction) {
      this.reloadAction = reloadAction;
      return this;
   }

   public Setting.ReloadAction getReloadAction() {
      return this.reloadAction;
   }

   public Setting<T> setRequiresWorldReload() {
      return this.setReloadAction(Setting.ReloadAction.WORLD);
   }

   public Setting<T> setRequiresMinecraftRestart() {
      return this.setReloadAction(Setting.ReloadAction.MINECRAFT);
   }

   public boolean getRequiresWorldReload() {
      return this.reloadAction == Setting.ReloadAction.WORLD;
   }

   public boolean getRequiresMinecraftRestart() {
      return this.reloadAction == Setting.ReloadAction.MINECRAFT;
   }

   public Setting<T> setConfigWidgetClassName(String configWidgetClassName) {
      this.configWidgetClassName = configWidgetClassName;
      return this;
   }

   public String getConfigWidgetClassName() {
      return this.configWidgetClassName;
   }

   public abstract T readTag(NBTBase var1);

   public abstract NBTBase writeTag(T var1);

   public void onDisconnect() {
      this.isSynchronized = false;
      this.syncedValue = null;
   }

   public static enum ReloadAction {
      NONE,
      WORLD,
      MINECRAFT;

      private ReloadAction() {
      }
   }
}

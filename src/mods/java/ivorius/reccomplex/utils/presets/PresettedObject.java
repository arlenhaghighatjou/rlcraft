package ivorius.reccomplex.utils.presets;

import com.google.common.collect.Lists;
import ivorius.reccomplex.RecurrentComplex;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PresettedObject<T> {
   @Nonnull
   protected PresetRegistry<T> presetRegistry;
   @Nullable
   protected String preset;
   protected T t;

   public PresettedObject(@Nonnull PresetRegistry<T> presetRegistry, @Nullable String preset) {
      this.presetRegistry = presetRegistry;
      this.setPreset(preset);
   }

   @Nonnull
   public PresetRegistry<T> getPresetRegistry() {
      return this.presetRegistry;
   }

   public void setPresetRegistry(@Nonnull PresetRegistry<T> presetRegistry) {
      this.presetRegistry = presetRegistry;
   }

   @Nullable
   public String getPreset() {
      return this.preset;
   }

   @Nonnull
   public Optional<String> presetTitle() {
      return Optional.ofNullable(this.getPreset()).map((id) -> {
         return (String)this.presetRegistry.title(id).orElse(String.format("Missing Preset (%s)", id));
      });
   }

   @Nonnull
   public Optional<List<String>> presetDescription() {
      return Optional.ofNullable(this.getPreset()).map((id) -> {
         return (List)this.presetRegistry.description(id).orElse(Lists.newArrayList(new String[]{String.format("Missing Preset (%s)", id)}));
      });
   }

   public boolean setPreset(@Nullable String preset) {
      this.preset = preset;
      this.t = null;
      return true;
   }

   public void setToCustom() {
      this.loadFromPreset(true);
      this.preset = null;
   }

   public boolean isCustom() {
      return this.preset == null;
   }

   public void setToDefault() {
      this.setPreset(this.defaultPreset());
   }

   public T getContents() {
      if (!this.isCustom()) {
         this.loadFromPreset(false);
      }

      return this.t;
   }

   public void setContents(T ts) {
      this.preset = null;
      this.t = ts;
   }

   protected boolean loadFromPreset(boolean copy) {
      if (this.preset == null) {
         return true;
      } else if ((this.t = (T)this.presetContents(copy)) != null) {
         return true;
      } else {
         RecurrentComplex.logger.warn(String.format("Failed to find preset (%s): %s", this.presetRegistry.getRegistry().description, this.preset));
         this.t = (T)(copy ? this.presetRegistry.preset(this.defaultPreset()) : this.presetRegistry.originalPreset(this.defaultPreset())).get();
         return false;
      }
   }

   protected String defaultPreset() {
      String defaultPreset = this.presetRegistry.defaultID();
      if (!this.presetRegistry.has(defaultPreset)) {
         throw new IllegalStateException(String.format("Default preset named '%s' not found!", defaultPreset));
      } else {
         return defaultPreset;
      }
   }

   private T presetContents(boolean copy) {
      return (T)(copy ? this.presetRegistry.preset(this.preset) : this.presetRegistry.originalPreset(this.preset)).orElse((Object)null);
   }
}

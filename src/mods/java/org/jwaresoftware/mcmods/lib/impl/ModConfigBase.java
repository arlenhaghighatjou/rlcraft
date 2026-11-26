package org.jwaresoftware.mcmods.lib.impl;

import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jwaresoftware.mcmods.lib.ModIntegration;
import org.jwaresoftware.mcmods.lib.Strings;
import org.jwaresoftware.mcmods.lib.api.mod.IModConfig;
import org.jwaresoftware.mcmods.lib.api.mod.IModInfo;

public abstract class ModConfigBase implements IModConfig {
   protected Configuration _forgeConfig;
   protected final Map<String, Object> _cache = new HashMap(71);
   private final Map<String, Object> _builderContext = new HashMap();
   private boolean _dontSave;

   protected ModConfigBase() {
      this._forgeConfig = new Configuration();
   }

   protected ModConfigBase(@Nonnull IModInfo mod, FMLPreInitializationEvent event) {
      VFile2 genericLoc = event.getSuggestedConfigurationFile();
      VFile2 configFile = new VFile2(genericLoc.getParent(), "wabbity_" + genericLoc.getName());
      this._forgeConfig = new Configuration(configFile, mod.config_version());
      this._forgeConfig.load();
      this.validateVersion(mod);
   }

   protected final Configuration forgeConfig() {
      return this._forgeConfig;
   }

   public final void persist() {
      this.fullyDefine();
      if (!this._dontSave) {
         this.forgeConfig().save();
      }

   }

   public void changed() {
      this._cache.clear();
      this.fullyDefine();
      if (this.forgeConfig().hasChanged() && !this._dontSave) {
         this.forgeConfig().save();
      }

   }

   public final boolean isSmartHoppersPresent() {
      return ModIntegration.SMARTHOPPERS.isLoaded();
   }

   public final boolean isSuperBlocksPresent() {
      return ModIntegration.SUPERBLOCKS.isLoaded();
   }

   public final boolean isVanillaFoodPantryPresent() {
      return ModIntegration.VANILLA_FOODPANTRY.isLoaded();
   }

   public final Map<String, Object> builderContext() {
      return this._builderContext;
   }

   protected static final boolean _getBooleanOrDefault(String config_value, boolean default_value) {
      Boolean b = null;
      if (Strings.isDefaultValue(config_value)) {
         b = default_value;
      } else {
         b = BooleanUtils.toBooleanObject(config_value);
         if (b == null) {
            b = default_value;
         }
      }

      return b.booleanValue();
   }

   public boolean isAllowed(@Nonnull String option, boolean defaultValue) {
      Boolean placeholder = BooleanUtils.toBooleanObject(option);
      return placeholder != null ? placeholder.booleanValue() : defaultValue;
   }

   public boolean isPresent(@Nonnull String feature) {
      return Boolean.TRUE.equals(this._builderContext.get(feature + ".present"));
   }

   public void setPresent(@Nonnull String feature) {
      this._builderContext.put(feature + ".present", Boolean.valueOf(true));
   }

   public void setNotPresent(@Nonnull String feature) {
      this._builderContext.remove(feature + ".present");
      this._builderContext.remove(feature + ".forgeid");
   }

   @Nullable
   public String getPresentAs(@Nonnull String feature) {
      Object value = this._builderContext.get(feature + ".forgeid");
      return value == null ? null : String.valueOf(value);
   }

   public void setPresentAs(String feature, String as) {
      this._builderContext.put(feature + ".present", Boolean.valueOf(true));
      this._builderContext.put(feature + ".forgeid", as);
   }

   protected final void setRecipeFlag(boolean b, String recipe_id) {
      if (b) {
         this.setPresent(recipe_id);
      } else {
         this.setNotPresent(recipe_id);
      }

   }

   public final void captureContextOres() {
      List<String> ores = Arrays.asList(OreDictionary.getOreNames());
      this._builderContext.put("__ores", ores);
   }

   public final List<String> capturedContextOres() {
      List<String> ores = (List)List.class.cast(this._builderContext.get("__ores"));
      return ores != null ? ores : Collections.EMPTY_LIST;
   }

   protected void earlyDefine() {
   }

   protected void fullyDefine() {
   }

   protected void validateVersion(IModInfo mod) {
      if (!StringUtils.equals(mod.config_version(), this._forgeConfig.getLoadedConfigVersion())) {
         int v_load = NumberUtils.toInt(this._forgeConfig.getLoadedConfigVersion(), -1);
         int v_curr = NumberUtils.toInt(mod.config_version());
         if (v_load < 0 || v_load > v_curr) {
            this._dontSave = true;
            FMLLog.warning("Unrecognized configuration version detected; config changes will NOT be saved this session", new Object[0]);
         }
      }

   }
}

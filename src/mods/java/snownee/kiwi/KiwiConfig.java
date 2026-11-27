package snownee.kiwi;

import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.ConfigEntry;
import java.util.HashMap;
import java.util.Map;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.RequiresMcRestart;

@Config(
   modid = "kiwi",
   name = "kiwi",
   category = ""
)
@LangKey("kiwi.config")
public class KiwiConfig {
   @Comment({"General settings of Kiwi."})
   @LangKey("kiwi.config.general")
   @Name("General")
   public static final KiwiConfig.General GENERAL = new KiwiConfig.General();
   @Comment({"Toggle optional modules of Kiwi."})
   @LangKey("kiwi.config.modules")
   @Name("Modules")
   public static final KiwiConfig.Modules MODULES = new KiwiConfig.Modules();

   private KiwiConfig() {
   }

   public static final class General {
      @Comment({"A list of preferred Mod IDs that results of Cuisine processes should stem from"})
      @LangKey("kiwi.config.general.oredict_preference")
      @Name("OreDict Preference")
      public String[] orePreference = new String[]{"cuisine", "minecraft"};
      @Comment({"Tooltips require pressing shift to be shown"})
      @LangKey("kiwi.config.general.press_shift")
      @Name("Tooltip Requires Shift")
      public boolean tooltipRequiresShift = false;
      @Comment({"Max line width shown in description of tooltips"})
      @LangKey("kiwi.config.general.tip_width")
      @Name("Tooltip Wrap Width")
      @RangeInt(
         min = 50
      )
      public int tooltipWrapWidth = 100;
      @Comment({"Use \u00a7x (almost) everywhere. Fix MC-109260. Do NOT enable this unless you know what you are doing"})
      @Name("Replace Default Font Renderer")
      @RequiresMcRestart
      public boolean replaceDefaultFontRenderer = false;

      General() {
      }
   }

   public static final class Modules {
      @Comment({"You can set the value to false to force disable the optional module"})
      @LangKey("kiwi.config.modules.optional_modules")
      @Name("Optional Modules")
      @RequiresMcRestart
      public Map<String, Boolean> modules = new HashMap();

      Modules() {
      }
   }

   public static void register() {
      ConfigManager.register("kiwi", "kiwi", "", "kiwi.config",
         ConfigEntry.category("General",
            ConfigEntry.value("OreDict Preference", String[].class, () -> KiwiConfig.GENERAL.orePreference, v -> KiwiConfig.GENERAL.orePreference = (String[])v).comment("A list of preferred Mod IDs that results of Cuisine processes should stem from").langKey("kiwi.config.general.oredict_preference"),
            ConfigEntry.value("Tooltip Requires Shift", boolean.class, () -> KiwiConfig.GENERAL.tooltipRequiresShift, v -> KiwiConfig.GENERAL.tooltipRequiresShift = (Boolean)v).comment("Tooltips require pressing shift to be shown").langKey("kiwi.config.general.press_shift"),
            ConfigEntry.value("Tooltip Wrap Width", int.class, () -> KiwiConfig.GENERAL.tooltipWrapWidth, v -> KiwiConfig.GENERAL.tooltipWrapWidth = (Integer)v).comment("Max line width shown in description of tooltips").langKey("kiwi.config.general.tip_width").range(50, Integer.MAX_VALUE),
            ConfigEntry.value("Replace Default Font Renderer", boolean.class, () -> KiwiConfig.GENERAL.replaceDefaultFontRenderer, v -> KiwiConfig.GENERAL.replaceDefaultFontRenderer = (Boolean)v).comment("Use \u00a7x (almost) everywhere. Fix MC-109260. Do NOT enable this unless you know what you are doing").langKey("kiwi.general.replacedefaultfontrenderer").requiresMcRestart()).comment("General settings of Kiwi.").langKey("kiwi.config.general"),
         ConfigEntry.category("Modules",
            ConfigEntry.map("Optional Modules", KiwiConfig.MODULES.modules, Boolean.class).comment("You can set the value to false to force disable the optional module").langKey("kiwi.config.modules.optional_modules").requiresMcRestart()).comment("Toggle optional modules of Kiwi.").langKey("kiwi.config.modules"));
   }
}

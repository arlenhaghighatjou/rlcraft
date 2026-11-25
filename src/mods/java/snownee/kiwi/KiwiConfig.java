package snownee.kiwi;

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
}

package com.alcatrazescapee.alcatrazcore;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.ConfigEntry;
import net.minecraftforge.common.config.ConfigManager;

@Config(
   modid = "alcatrazcore"
)
public final class CoreConfig {
   @Comment({"When Advanced tooltips are enabled, show extra information about the item", "(Ore dictionary values, NBT data, registry + translation keys, etc.)"})
   public static boolean showDebugTooltips = false;

   public CoreConfig() {
   }

   public static void register() {
      ConfigManager.register(AlcatrazCore.MOD_ID, "", "general", null,
         ConfigEntry.value("showDebugTooltips", boolean.class, () -> showDebugTooltips, value -> showDebugTooltips = (Boolean)value)
            .comment("When Advanced tooltips are enabled, show extra information about the item", "(Ore dictionary values, NBT data, registry + translation keys, etc.)"));
   }
}

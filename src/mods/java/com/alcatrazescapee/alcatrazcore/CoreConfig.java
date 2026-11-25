package com.alcatrazescapee.alcatrazcore;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;

@Config(
   modid = "alcatrazcore"
)
public final class CoreConfig {
   @Comment({"When Advanced tooltips are enabled, show extra information about the item", "(Ore dictionary values, NBT data, registry + translation keys, etc.)"})
   public static boolean showDebugTooltips = false;

   public CoreConfig() {
   }
}

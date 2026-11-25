package org.jwaresoftware.mcmods.lib;

import javax.annotation.Nullable;
import net.minecraft.item.EnumDyeColor;

public final class Colors {
   public static final int[] RGB_VALUES = new int[]{16383998, 16351261, 13061821, 3847130, 16701501, 8439583, 15961002, 4673362, 10329495, 1481884, 8991416, 3949738, 8606770, 6192150, 11546150, 1908001};

   public Colors() {
   }

   public static final int getRGB(@Nullable EnumDyeColor dye) {
      return dye != null && dye.getMetadata() < RGB_VALUES.length ? RGB_VALUES[dye.getMetadata()] : 1908001;
   }
}

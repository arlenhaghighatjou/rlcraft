package com.endertech.minecraft.forge.configs;

import java.util.Locale;
import net.minecraft.util.IStringSerializable;

public interface IForgeEnum extends IStringSerializable {
   default String getLowerCase(String name) {
      return name.toLowerCase(Locale.ROOT);
   }
}

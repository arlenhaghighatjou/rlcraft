package com.endertech.minecraft.forge.coremod.descriptors;

import com.endertech.minecraft.forge.coremod.names.Name;

public class Descriptor extends Name {
   public Descriptor(String normal, String obfuscated) {
      super(normal, obfuscated);
   }

   public Descriptor(String prefix, Name name, String suffix) {
      super(prefix + name.normal + suffix, prefix + name.obfuscated + suffix);
   }
}

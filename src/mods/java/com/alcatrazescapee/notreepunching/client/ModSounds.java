package com.alcatrazescapee.notreepunching.client;

import com.alcatrazescapee.alcatrazcore.util.RegistryHelper;
import net.minecraft.util.SoundEvent;

public final class ModSounds {
   public static SoundEvent KNAPPING;

   public ModSounds() {
   }

   public static void preInit() {
      RegistryHelper r = RegistryHelper.get("notreepunching");
      KNAPPING = r.registerSound("knapping");
   }
}

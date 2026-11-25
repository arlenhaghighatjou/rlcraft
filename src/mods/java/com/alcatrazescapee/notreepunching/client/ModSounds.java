package com.alcatrazescapee.notreepunching.client;

import com.alcatrazescapee.alcatrazcore.util.CoreHelpers;
import com.alcatrazescapee.alcatrazcore.util.RegistryHelper;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder("notreepunching")
public final class ModSounds {
   public static final SoundEvent KNAPPING = (SoundEvent)CoreHelpers.getNull();

   public ModSounds() {
   }

   public static void preInit() {
      RegistryHelper r = RegistryHelper.get("notreepunching");
      r.registerSound("knapping");
   }
}

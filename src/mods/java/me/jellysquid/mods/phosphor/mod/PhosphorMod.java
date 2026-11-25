package me.jellysquid.mods.phosphor.mod;

import net.minecraftforge.fml.common.Mod;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   name = "Phosphor",
   modid = "phosphor-lighting",
   version = "1.12.2-0.2.6",
   certificateFingerprint = "f0387d288626cc2d937daa504e74af570c52a2f1",
   acceptedMinecraftVersions = "1.12.2",
   acceptableRemoteVersions = "*",
   dependencies = "after:neid@[1.5.4.4,);after:spongeforge@[1.12.2-2838-7.1.7-RC3844,)"
)
public class PhosphorMod {
   public static final Logger LOGGER = LogManager.getLogger("Phosphor");

   public PhosphorMod() {
   }
}

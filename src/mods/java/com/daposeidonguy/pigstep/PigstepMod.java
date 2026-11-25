package com.daposeidonguy.pigstep;

import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
   modid = "pigstep",
   name = "Pigstep",
   version = "1.12",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class PigstepMod {
   public static final String MODID = "pigstep";
   public static final String NAME = "Pigstep";
   public static final String VERSION = "1.12";
   public static Logger logger = LogManager.getLogger();

   public PigstepMod() {
   }
}

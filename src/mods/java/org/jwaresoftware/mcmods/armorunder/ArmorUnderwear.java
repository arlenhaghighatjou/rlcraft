package org.jwaresoftware.mcmods.armorunder;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import org.jwaresoftware.mcmods.lib.api.mod.IModRuntime;

@Mod(
   modid = "armorunder",
   name = "Armor Underwear",
   version = "1.0.0",
   acceptedMinecraftVersions = "[1.12,)",
   updateJSON = "http://jwaresoftware.org/files/mcmods/versions/armorunder.json",
   dependencies = "required-after:forge@[14.23.4.2705,);required-after:carrots@[1.0.0b1]"
)
public final class ArmorUnderwear {
   @Instance("armorunder")
   public static ArmorUnderwear instance;
   @SidedProxy(
      clientSide = "org.jwaresoftware.mcmods.armorunder.runtime.ModRuntimeSP",
      serverSide = "org.jwaresoftware.mcmods.armorunder.runtime.ModRuntimeDS"
   )
   public static IModRuntime runtime;

   public ArmorUnderwear() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      runtime.beginStartUp(event);
      runtime.getConfig().persist();
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      runtime.midStartUp(event);
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) {
      runtime.finishStartUp(event);
   }

   @EventHandler
   public void stop(FMLServerStoppingEvent event) {
      runtime.shutDown(event);
   }
}

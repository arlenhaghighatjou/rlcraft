package org.jwaresoftware.mcmods.lib;

import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jwaresoftware.mcmods.lib.loot.Loot;

@Mod(
   modid = "carrots",
   name = "Carrots Lib",
   version = "1.0.0b1",
   acceptedMinecraftVersions = "[1.12.2,)",
   updateJSON = "http://jwaresoftware.org/files/mcmods/versions/carrotslib.json",
   dependencies = "required-after:forge@[14.23.4.2705,)"
)
public class CarrotsLib {
   public static final String NAME = "Carrots Lib";
   public static final String MOD_ID = "carrots";
   public static final String RESOURCES_ID = "carrots:";
   public static final String VERSION = "1.0.0b1";
   public static final String VERSION_CHECK_URL = "http://jwaresoftware.org/files/mcmods/versions/carrotslib.json";
   @Instance("carrots")
   public static CarrotsLib instance;
   LibConfig _libConfig;

   public CarrotsLib() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      Tooltips.initLanguageSupport();
      this._libConfig = (new LibConfig()).setInstance();
      MinecraftForge.EVENT_BUS.register(new CarrotsLib.RegisterObjectsListener());
      Loot.initDefaults();
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) {
   }

   final class RegisterObjectsListener {
      RegisterObjectsListener() {
      }

      @SubscribeEvent
      public void doRecipesRegister(Register<IRecipe> event) {
         RID.initDefaults();
      }
   }
}

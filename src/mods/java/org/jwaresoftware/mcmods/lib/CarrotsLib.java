package org.jwaresoftware.mcmods.lib;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
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
   public static CarrotsLib instance;
   LibConfig _libConfig;

   public CarrotsLib() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         org.jwaresoftware.mcmods.lib.SharedGlue.registerHolders();
         org.jwaresoftware.mcmods.lib.Whatis.registerHolders();
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.init((FMLInitializationEvent)event);
      } else if (event instanceof FMLPostInitializationEvent) {
         this.postInit((FMLPostInitializationEvent)event);
      }

   }

   public void preInit(FMLPreInitializationEvent event) {
      Tooltips.initLanguageSupport();
      this._libConfig = (new LibConfig()).setInstance();
      (new CarrotsLib.RegisterObjectsListener()).register(MinecraftForge.EVENT_BUS);
      Loot.initDefaults();
   }

   public void init(FMLInitializationEvent event) {
   }

   public void postInit(FMLPostInitializationEvent event) {
   }

   final class RegisterObjectsListener {
      RegisterObjectsListener() {
      }

      public void register(EventBus bus) {
         bus.registerGeneric(Register.class, IRecipe.class, this::doRecipesRegister);
      }

      public void doRecipesRegister(Register<IRecipe> event) {
         RID.initDefaults();
      }
   }
}

package com.oblivioussp.spartanweaponry;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import com.oblivioussp.spartanweaponry.api.SpartanWeaponryAPI;
import com.oblivioussp.spartanweaponry.client.gui.CreativeTabsSW;
import com.oblivioussp.spartanweaponry.client.gui.GuiHandler;
import com.oblivioussp.spartanweaponry.compat.spartanhud.EventHandlerBaubleHUD;
import com.oblivioussp.spartanweaponry.init.ItemRegistrySW;
import com.oblivioussp.spartanweaponry.init.OreDictionarySW;
import com.oblivioussp.spartanweaponry.network.NetworkHandler;
import com.oblivioussp.spartanweaponry.proxy.CommonProxy;
import com.oblivioussp.spartanweaponry.util.APIInternalMethodHandler;
import com.oblivioussp.spartanweaponry.util.AdvancementTrigger;
import com.oblivioussp.spartanweaponry.util.ConfigHandler;
import com.oblivioussp.spartanweaponry.util.Log;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod(
   modid = "spartanweaponry",
   name = "Spartan Weaponry",
   version = "1.5.3",
   dependencies = "after:xat@[0.31,);after:bettercombatmod@[2.0.5,);",
   acceptedMinecraftVersions = "[1.12.2]",
   guiFactory = "com.oblivioussp.spartanweaponry.client.gui.GuiFactorySW"
)
public class ModSpartanWeaponry {
   public static final String ID = "spartanweaponry";
   public static final String Name = "Spartan Weaponry";
   public static final String Dependencies = "after:xat@[0.31,);after:bettercombatmod@[2.0.5,);";
   public static final String McVersion = "[1.12.2]";
   public static final String ConfigVersion = "2.0";
   public static final String ProxyClientClass = "com.oblivioussp.spartanweaponry.proxy.ClientProxy";
   public static final String ProxyServerClass = "com.oblivioussp.spartanweaponry.proxy.ServerProxy";
   public static final String GuiFactoryClass = "com.oblivioussp.spartanweaponry.client.gui.GuiFactorySW";
   public static final String ModID_Baubles = "baubles";
   public static final String ModID_SpartanHudBaubles = "spartanhudbaubles";
   public static final String ModID_BetterCombat = "bettercombatmod";
   public static final String ModID_Trinkets = "xat";
   public static final String ModID_SME = "somanyenchantments";
   public static boolean isBaublesLoaded = false;
   public static boolean isSpartanHudBaublesLoaded = false;
   public static boolean isRLCombatLoaded = false;
   public static boolean isTrinketsLoaded = false;
   public static boolean isSMELoaded = false;
   public static boolean debugMode = false;
   public static ModSpartanWeaponry instance;
   public static CommonProxy proxy;

   public ModSpartanWeaponry() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new com.oblivioussp.spartanweaponry.proxy.ClientProxy() : new com.oblivioussp.spartanweaponry.proxy.ServerProxy();
         net.minecraftforge.common.crafting.CraftingHelper.register(new net.minecraft.util.ResourceLocation("spartanweaponry", "ore_dict_exists"), new com.oblivioussp.spartanweaponry.util.ConditionFactoryOreDict());
         net.minecraftforge.common.crafting.CraftingHelper.register(new net.minecraft.util.ResourceLocation("spartanweaponry", "item_exists"), new com.oblivioussp.spartanweaponry.util.ConditionFactoryItemExists());
         if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            com.oblivioussp.spartanweaponry.event.EventHandlerClient.register(MinecraftForge.EVENT_BUS);
         }
         com.oblivioussp.spartanweaponry.event.EventHandlerCommon.register(MinecraftForge.EVENT_BUS);
         com.oblivioussp.spartanweaponry.init.EnchantmentRegistrySW.register(MinecraftForge.EVENT_BUS);
         com.oblivioussp.spartanweaponry.init.EntityRegistrySW.register(MinecraftForge.EVENT_BUS);
         com.oblivioussp.spartanweaponry.init.ItemRegistrySW.register(MinecraftForge.EVENT_BUS);
         com.oblivioussp.spartanweaponry.init.ModelRenderRegistry.register(MinecraftForge.EVENT_BUS);
         com.oblivioussp.spartanweaponry.init.RecipeRegistry.register(MinecraftForge.EVENT_BUS);
         com.oblivioussp.spartanweaponry.init.SoundRegistry.register(MinecraftForge.EVENT_BUS);
      } else if (event instanceof FMLPreInitializationEvent) {
         ModSpartanWeaponry.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         ModSpartanWeaponry.init((FMLInitializationEvent)event);
      } else if (event instanceof FMLPostInitializationEvent) {
         ModSpartanWeaponry.postInit((FMLPostInitializationEvent)event);
      }

   }

   public static void preInit(FMLPreInitializationEvent ev) {
      Log.info("Starting up Spartan Weaponry!");
      if (debugMode) {
         Log.info("Deobfuscated Dev Environment detected! Enabling debug features!");
      }

      isBaublesLoaded = Loader.isModLoaded("baubles");
      isSpartanHudBaublesLoaded = Loader.isModLoaded("spartanhudbaubles");
      isRLCombatLoaded = Loader.isModLoaded("bettercombatmod");
      isTrinketsLoaded = Loader.isModLoaded("xat");
      isSMELoaded = Loader.isModLoaded("somanyenchantments");
      SpartanWeaponryAPI.internalHandler = new APIInternalMethodHandler();
      Log.info("API: Loaded internal method handler. API functionality should work now!");
      if (isSpartanHudBaublesLoaded && ev.getSide() == Side.CLIENT) {
         Log.debug("Spartan HUD: Baubles detected! Integrating event handler for that mod");
         (new EventHandlerBaubleHUD()).register(MinecraftForge.EVENT_BUS);
      }

      ConfigHandler.init(ev.getModConfigurationDirectory());
      (new ConfigHandler()).register(MinecraftForge.EVENT_BUS);
      ItemRegistrySW.registerModdedMaterials();
      CreativeTabsSW.preInit();
      proxy.preInit(ev);
      Log.debug("Finished preInit phase!");
   }

   public static void init(FMLInitializationEvent ev) {
      ConfigHandler.validateCrossbowEnchantmentWhitelist();
      OreDictionarySW.init();
      NetworkHandler.init();
      NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
      AdvancementTrigger.registerTriggers();
      LootTableList.register(new ResourceLocation("spartanweaponry", "inject/village_blacksmith"));
      proxy.init(ev);
      Log.debug("Finished init phase!");
   }

   public static void postInit(FMLPostInitializationEvent ev) {
      proxy.postInit(ev);
      Log.debug("Finished postInit phase!");
   }
}

package com.oblivioussp.spartanshields;

import com.oblivioussp.spartanshields.client.gui.CreativeTabsSS;
import com.oblivioussp.spartanshields.enchantment.EnchantmentSS;
import com.oblivioussp.spartanshields.init.RecipeRegistry;
import com.oblivioussp.spartanshields.network.NetworkHandler;
import com.oblivioussp.spartanshields.proxy.CommonProxy;
import com.oblivioussp.spartanshields.util.ConfigHandler;
import com.oblivioussp.spartanshields.util.LogHelper;
import com.oblivioussp.spartanshields.util.ModHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(
   modid = "spartanshields",
   name = "Spartan Shields",
   version = "1.5.5",
   dependencies = "after:redstoneflux;after:enderio;after:rftools;after:botania;after:redstonearsenal;after:abyssalcraft;after:betterwithmods;after:thaumcraft",
   acceptedMinecraftVersions = "[1.12.2]",
   guiFactory = "com.oblivioussp.spartanshields.client.gui.GuiFactorySS"
)
public class ModSpartanShields {
   @Instance("spartanshields")
   public static ModSpartanShields instance;
   @SidedProxy(
      clientSide = "com.oblivioussp.spartanshields.proxy.ClientProxy",
      serverSide = "com.oblivioussp.spartanshields.proxy.ServerProxy"
   )
   public static CommonProxy proxy;

   public ModSpartanShields() {
   }

   @EventHandler
   public static void preInit(FMLPreInitializationEvent ev) {
      LogHelper.info("Starting up Spartan Shields!");
      ConfigHandler.init(ev.getSuggestedConfigurationFile());
      proxy.preInit(ev);
      LogHelper.debug("Finished preInit phase!");
   }

   @EventHandler
   public static void init(FMLInitializationEvent ev) {
      NetworkHandler.init();
      CreativeTabsSS.TAB_SS.setRelevantEnchantmentTypes(new EnumEnchantmentType[]{EnchantmentSS.TYPE_SHIELD});
      proxy.init(ev);
      LogHelper.debug("Finished init phase!");
   }

   @EventHandler
   public static void postInit(FMLPostInitializationEvent ev) {
      ModHelper.postInit();
      RecipeRegistry.addCompatRecipes();
      proxy.postInit(ev);
      LogHelper.debug("Finished postInit phase!");
   }
}

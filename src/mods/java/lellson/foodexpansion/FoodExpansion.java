package lellson.foodexpansion;

import lellson.foodexpansion.crafting.FoodRecipes;
import lellson.foodexpansion.items.ItemEventHandler;
import lellson.foodexpansion.proxy.CommonProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(
   modid = "foodexpansion",
   name = "Food Expansion",
   version = "1.3",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class FoodExpansion {
   @SidedProxy(
      clientSide = "lellson.foodexpansion.proxy.ClientProxy",
      serverSide = "lellson.foodexpansion.proxy.CommonProxy"
   )
   public static CommonProxy proxy;
   public static Configuration config;
   @Instance("foodexpansion")
   public static FoodExpansion instance;
   public static CreativeTabs tabFoodExpansion = new CreativeTabs("tabFoodExpansion") {
      public ItemStack getTabIconItem() {
         return new ItemStack(FoodItems.itemChocolateBar);
      }
   };

   public FoodExpansion() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      config = new Configuration(event.getSuggestedConfigurationFile());
      FoodExpansionConfig.syncConfig();
      FoodItems.init();
      MinecraftForge.EVENT_BUS.register(new FoodRegistry());
   }

   @SubscribeEvent
   public void onConfigChanged(OnConfigChangedEvent event) {
      if (event.getModID().equals("foodexpansion")) {
         FoodExpansionConfig.syncConfig();
      }

   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      FoodRecipes.initShapedRecipes();
      FoodRecipes.initShapelessRecipes();
      FoodRecipes.initSmeltingRecipes();
      FMLCommonHandler.instance().bus().register(instance);
      MinecraftForge.EVENT_BUS.register(new DropHandler());
      MinecraftForge.EVENT_BUS.register(new ItemEventHandler());
      proxy.registerRenders();
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) {
      FoodItems.increaseStackSizes();
   }
}

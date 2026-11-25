package lellson.foodexpansion;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
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
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(
   modid = "foodexpansion",
   name = "Food Expansion",
   version = "1.3",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class FoodExpansion {
   public static CommonProxy proxy;
   public static Configuration config;
   public static FoodExpansion instance;
   public static CreativeTabs tabFoodExpansion = new CreativeTabs("tabFoodExpansion") {
      public ItemStack getTabIconItem() {
         return new ItemStack(FoodItems.itemChocolateBar);
      }
   };

   public FoodExpansion() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new lellson.foodexpansion.proxy.ClientProxy() : new lellson.foodexpansion.proxy.CommonProxy();
         FoodRegistry.register(MinecraftForge.EVENT_BUS);
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.init((FMLInitializationEvent)event);
      } else if (event instanceof FMLPostInitializationEvent) {
         this.postInit((FMLPostInitializationEvent)event);
      }

   }

   public void preInit(FMLPreInitializationEvent event) {
      config = new Configuration(event.getSuggestedConfigurationFile());
      FoodExpansionConfig.syncConfig();
      FoodItems.init();
      FoodRecipes.registerConditions();
   }

   public void register(EventBus bus) {
      bus.register(OnConfigChangedEvent.class, this::onConfigChanged);
   }

   public void onConfigChanged(OnConfigChangedEvent event) {
      if (event.getModID().equals("foodexpansion")) {
         FoodExpansionConfig.syncConfig();
      }

   }

   public void init(FMLInitializationEvent event) {
      FoodRecipes.initSmeltingRecipes();
      instance.register(FMLCommonHandler.instance().bus());
      (new DropHandler()).register(MinecraftForge.EVENT_BUS);
      (new ItemEventHandler()).register(MinecraftForge.EVENT_BUS);
      proxy.registerRenders();
   }

   public void postInit(FMLPostInitializationEvent event) {
      FoodItems.increaseStackSizes();
   }
}

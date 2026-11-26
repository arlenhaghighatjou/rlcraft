package tschipp.forgottenitems;

import tschipp.forgottenitems.util.FIWorldSavedData;
import net.minecraft.world.storage.MapStorage;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;

import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import tschipp.forgottenitems.crafting.CraftingRegister;
import tschipp.forgottenitems.items.ItemList;
import tschipp.forgottenitems.network.ForbiddenPacket;
import tschipp.forgottenitems.network.ForbiddenPacketHandler;
import tschipp.forgottenitems.network.SeedPacket;
import tschipp.forgottenitems.network.SeedPacketHandler;
import tschipp.forgottenitems.util.CommonProxy;
import tschipp.forgottenitems.util.FIConfig;
import tschipp.forgottenitems.util.FIEvents;
import tschipp.forgottenitems.util.FIHelper;


@Mod(modid = "forgottenitems", name = "Forgotten Items", version = "1.2.1", dependencies = "required-after:tschipplib", acceptedMinecraftVersions = "[1.12,1.13)")

public class FIM 
{
   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new tschipp.forgottenitems.util.ClientProxy() : new tschipp.forgottenitems.util.CommonProxy();
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.init((FMLInitializationEvent)event);
      } else if (event instanceof FMLPostInitializationEvent) {
         this.postInit((FMLPostInitializationEvent)event);
      }

   }

	public final static String MODID = "forgottenitems";

	
	public static FIM instance;

	public static final String COMMON_PROXY = "tschipp.forgottenitems.util.CommonProxy";
	public static final String CLIENT_PROXY = "tschipp.forgottenitems.util.ClientProxy";

	public static CommonProxy proxy;
	
	public static SimpleNetworkWrapper network;

	public static Configuration config;
	public static Configuration bannedItemConfig;
	public static Configuration customRecipesConfig;

	public static final int RECIPE_AMOUNT = 25;
	
	public static final EaglercraftRandom RANDOM = new EaglercraftRandom();
	public static Logger LOGGER;
	public void preInit(FMLPreInitializationEvent event) {
		MapStorage.storageProviders.put(FIWorldSavedData.class, FIWorldSavedData::new);
		
		for(int k = 0; k < RECIPE_AMOUNT + 1; k++)
		{
			FIHelper.OUTPUTS_CORES.add(null);
		}
		LOGGER = event.getModLog();

		config = new Configuration(new VFile2(event.getModConfigurationDirectory(), "ForgottenItems.cfg"));
		bannedItemConfig = new Configuration(new VFile2(event.getModConfigurationDirectory(), "ForgottenItemsBannedList.cfg"));
		customRecipesConfig = new Configuration(new VFile2(event.getModConfigurationDirectory(), "ForgottenItemsCustomRecipes.cfg"));
		
		FIConfig.syncConfig();
		
		proxy.preInit(event);

		network = NetworkRegistry.INSTANCE.newSimpleChannel("ForgottenItems");
		network.registerMessage(new SeedPacketHandler(), SeedPacket.class, SeedPacket::new, 0, Side.CLIENT);
		network.registerMessage(new ForbiddenPacketHandler(), ForbiddenPacket.class, ForbiddenPacket::new, 1, Side.CLIENT);
		

	}

	public void init(FMLInitializationEvent event) {
		proxy.init(event);
		(new FIEvents()).register(MinecraftForge.EVENT_BUS);
		CraftingRegister.registerRecipes();
	}


	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);

	}


	public static CreativeTabs forgottenItems = new CreativeTabs("forgottenItems"){
		@Override
		public ItemStack getTabIconItem(){
			return new ItemStack(ItemList.explosionPickaxe);
		}
	};




}







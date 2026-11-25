package cursedflames.bountifulbaubles;

import cursedflames.bountifulbaubles.baubleeffect.BaubleAttributeModifierHandler;
import cursedflames.bountifulbaubles.block.ModBlocks;
import cursedflames.bountifulbaubles.block.TESRReforger;
import cursedflames.bountifulbaubles.block.TileReforger;
import cursedflames.bountifulbaubles.capability.CapabilityWormholePins;
import cursedflames.bountifulbaubles.entity.ModEntities;
import cursedflames.bountifulbaubles.item.ItemAmuletSinGluttony;
import cursedflames.bountifulbaubles.item.ItemAmuletSinWrath;
import cursedflames.bountifulbaubles.item.ItemShieldCobalt;
import cursedflames.bountifulbaubles.item.ItemTrinketBrokenHeart;
import cursedflames.bountifulbaubles.item.ModItems;
import cursedflames.bountifulbaubles.loot.ModLoot;
import cursedflames.bountifulbaubles.network.PacketHandler;
import cursedflames.bountifulbaubles.potion.ModPotions;
import cursedflames.bountifulbaubles.proxy.GuiProxy;
import cursedflames.bountifulbaubles.proxy.ISideProxy;
import cursedflames.bountifulbaubles.recipe.AnvilRecipes;
import cursedflames.bountifulbaubles.recipe.ModCrafting;
import cursedflames.bountifulbaubles.util.Config;
import cursedflames.bountifulbaubles.util.RegistryHelper;
import cursedflames.bountifulbaubles.wormhole.CommandWormhole;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Logger;

@Mod(
   modid = "bountifulbaubles",
   name = "Bountiful Baubles",
   version = "0.0.1",
   useMetadata = true,
   acceptedMinecraftVersions = "[1.12.2]"
)
@EventBusSubscriber
public class BountifulBaubles {
   @Instance
   public static BountifulBaubles instance;
   public static final String MODNAME = "Bountiful Baubles";
   public static final String MODID = "bountifulbaubles";
   public static final String VERSION = "0.0.1";
   public static final RegistryHelper registryHelper = new RegistryHelper("bountifulbaubles");
   public static Config config;
   public static Logger logger;
   @SidedProxy(
      clientSide = "cursedflames.bountifulbaubles.proxy.ClientProxy",
      serverSide = "cursedflames.bountifulbaubles.proxy.ServerProxy"
   )
   public static ISideProxy proxy;
   public static final String ARMOR_TEXTURE_PATH = "textures/models/armor/";
   public static final CreativeTabs TAB = new CreativeTabs("bountifulbaubles") {
      @SideOnly(Side.CLIENT)
      public ItemStack getTabIconItem() {
         return new ItemStack(ModItems.trinketObsidianSkull);
      }
   };
   public static boolean isQuarkLoaded = false;
   public static boolean isBotaniaLoaded = false;
   public static boolean isAlbedoLoaded = false;

   public BountifulBaubles() {
   }

   @EventHandler
   public static void preInit(FMLPreInitializationEvent event) {
      isBotaniaLoaded = Loader.isModLoaded("botania");
      isQuarkLoaded = Loader.isModLoaded("quark");
      isAlbedoLoaded = Loader.isModLoaded("albedo");
      logger = event.getModLog();
      config = new Config("1", logger);
      config.preInit(event);
      ModConfig.initConfig();
      MinecraftForge.EVENT_BUS.register(cursedflames.bountifulbaubles.event.EventHandler.class);
      MinecraftForge.EVENT_BUS.register(AnvilRecipes.class);
      MinecraftForge.EVENT_BUS.register(ModCrafting.class);
      MinecraftForge.EVENT_BUS.register(ModPotions.class);
      MinecraftForge.EVENT_BUS.register(BaubleAttributeModifierHandler.class);
      MinecraftForge.EVENT_BUS.register(ModLoot.class);
      MinecraftForge.EVENT_BUS.register(ItemShieldCobalt.class);
      MinecraftForge.EVENT_BUS.register(ItemAmuletSinGluttony.class);
      MinecraftForge.EVENT_BUS.register(ItemAmuletSinWrath.class);
      MinecraftForge.EVENT_BUS.register(ItemTrinketBrokenHeart.class);
      CapabilityWormholePins.registerCapability();
      MinecraftForge.EVENT_BUS.register(CapabilityWormholePins.class);
      ModEntities.registerEntities();
   }

   @SubscribeEvent
   public static void registerBlocks(Register<Block> event) {
      ModBlocks.registerToRegistry();
      registryHelper.registerBlocks(event);
   }

   @SubscribeEvent
   public static void registerItems(Register<Item> event) {
      ModItems.registerToRegistry();
      registryHelper.registerItems(event);
      ModItems.registerOreDictionaryEntries();
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public static void registerModels(ModelRegistryEvent event) {
      registryHelper.registerModels();
      ClientRegistry.bindTileEntitySpecialRenderer(TileReforger.class, new TESRReforger());
   }

   @EventHandler
   public static void init(FMLInitializationEvent event) {
      proxy.addRenderLayer();
      ModItems.registerOtherModOreDictionaryEntries();
      NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiProxy());
      PacketHandler.registerMessages();
   }

   @EventHandler
   public static void postInit(FMLPostInitializationEvent event) {
      config.postInit(event);
   }

   @EventHandler
   public static void serverStart(FMLServerStartingEvent event) {
      event.registerServerCommand(new CommandWormhole());
   }
}

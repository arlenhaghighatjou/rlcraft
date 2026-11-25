package net.blay09.mods.waystones;

import net.blay09.mods.waystones.block.BlockWaystone;
import net.blay09.mods.waystones.block.TileWaystone;
import net.blay09.mods.waystones.item.ItemBoundScroll;
import net.blay09.mods.waystones.item.ItemReturnScroll;
import net.blay09.mods.waystones.item.ItemWarpScroll;
import net.blay09.mods.waystones.item.ItemWarpStone;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.worldgen.ComponentVillageWaystone;
import net.blay09.mods.waystones.worldgen.LegacyWorldGen;
import net.blay09.mods.waystones.worldgen.VillageWaystoneCreationHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@Mod(
   modid = "waystones",
   name = "Waystones",
   acceptedMinecraftVersions = "[1.12]"
)
@EventBusSubscriber
public class Waystones {
   public static final String MOD_ID = "waystones";
   @Instance("waystones")
   public static Waystones instance;
   @SidedProxy(
      serverSide = "net.blay09.mods.waystones.CommonProxy",
      clientSide = "net.blay09.mods.waystones.client.ClientProxy"
   )
   public static CommonProxy proxy;
   @ObjectHolder("waystone")
   public static final Block blockWaystone = Blocks.AIR;
   @ObjectHolder("return_scroll")
   public static final Item itemReturnScroll = Items.AIR;
   @ObjectHolder("bound_scroll")
   public static final Item itemBoundScroll = Items.AIR;
   @ObjectHolder("warp_scroll")
   public static final Item itemWarpScroll = Items.AIR;
   @ObjectHolder("warp_stone")
   public static final Item itemWarpStone = Items.AIR;
   public static final CreativeTabs creativeTab = new CreativeTabs("waystones") {
      public ItemStack getTabIconItem() {
         return new ItemStack(Waystones.blockWaystone);
      }
   };

   public Waystones() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      GameRegistry.registerTileEntity(TileWaystone.class, new ResourceLocation("waystones", "waystone"));
      NetworkHandler.init();
      VillagerRegistry.instance().registerVillageCreationHandler(new VillageWaystoneCreationHandler());
      MapGenStructureIO.registerStructureComponent(ComponentVillageWaystone.class, "waystones:village_waystone");
      GameRegistry.registerWorldGenerator(new LegacyWorldGen(), 0);
      proxy.preInit(event);
      MinecraftForge.EVENT_BUS.register(new WarpDamageResetHandler());
   }

   @SubscribeEvent
   public static void onConfigChanged(OnConfigChangedEvent event) {
      if ("waystones".equals(event.getModID())) {
         ConfigManager.sync("waystones", Type.INSTANCE);
      }

   }

   @SubscribeEvent
   public static void registerBlocks(Register<Block> event) {
      event.getRegistry().registerAll(new Block[]{new BlockWaystone()});
   }

   @SubscribeEvent
   public static void registerItems(Register<Item> event) {
      event.getRegistry().registerAll(new Item[]{(Item)(new ItemBlock(blockWaystone)).setRegistryName("waystone")});
      event.getRegistry().registerAll(new Item[]{(Item)(new ItemReturnScroll()).setRegistryName(ItemReturnScroll.registryName), (Item)(new ItemBoundScroll()).setRegistryName(ItemBoundScroll.registryName), new ItemWarpScroll(), new ItemWarpStone()});
   }

   @SubscribeEvent
   public static void registerModels(ModelRegistryEvent event) {
      ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockWaystone), 0, new ModelResourceLocation(BlockWaystone.registryName, "inventory"));
      ModelLoader.setCustomModelResourceLocation(itemWarpStone, 0, new ModelResourceLocation(ItemWarpStone.registryName, "inventory"));
      ModelLoader.setCustomModelResourceLocation(itemReturnScroll, 0, new ModelResourceLocation(ItemReturnScroll.registryName, "inventory"));
      ModelLoader.setCustomModelResourceLocation(itemWarpScroll, 0, new ModelResourceLocation(ItemWarpScroll.registryName, "inventory"));
      ModelLoader.setCustomModelResourceLocation(itemBoundScroll, 0, new ModelResourceLocation(ItemBoundScroll.registryName, "inventory"));
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(blockWaystone), 0, TileWaystone.class);
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", "net.blay09.mods.waystones.compat.TheOneProbeAddon");
   }
}

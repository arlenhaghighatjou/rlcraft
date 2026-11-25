package net.blay09.mods.waystones;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.registries.ObjectHolderRegistry;
import net.minecraftforge.fml.common.eventhandler.EventBus;
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
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

@Mod(
   modid = "waystones",
   name = "Waystones",
   acceptedMinecraftVersions = "[1.12]"
)
public class Waystones {
   public static final String MOD_ID = "waystones";
   public static Waystones instance;
   public static CommonProxy proxy;
   public static Block blockWaystone = Blocks.AIR;
   public static Item itemReturnScroll = Items.AIR;
   public static Item itemBoundScroll = Items.AIR;
   public static Item itemWarpScroll = Items.AIR;
   public static Item itemWarpStone = Items.AIR;
   public static final CreativeTabs creativeTab = new CreativeTabs("waystones") {
      public ItemStack getTabIconItem() {
         return new ItemStack(Waystones.blockWaystone);
      }
   };

   public Waystones() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new net.blay09.mods.waystones.client.ClientProxy() : new net.blay09.mods.waystones.CommonProxy();
         net.blay09.mods.waystones.WaystoneConfig.register();
         net.blay09.mods.waystones.Waystones.registerHolders();
         net.blay09.mods.waystones.Waystones.register(MinecraftForge.EVENT_BUS);
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      }

   }

   public void preInit(FMLPreInitializationEvent event) {
      GameRegistry.registerTileEntity(TileWaystone.class, new ResourceLocation("waystones", "waystone"), TileWaystone::new);
      NetworkHandler.init();
      VillagerRegistry.instance().registerVillageCreationHandler(new VillageWaystoneCreationHandler());
      MapGenStructureIO.registerStructureComponent(ComponentVillageWaystone.class, "waystones:village_waystone");
      GameRegistry.registerWorldGenerator(new LegacyWorldGen(), 0);
      proxy.preInit(event);
      (new WarpDamageResetHandler()).register(MinecraftForge.EVENT_BUS);
   }

   public static void register(EventBus bus) {
      bus.register(OnConfigChangedEvent.class, Waystones::onConfigChanged);
      bus.registerGeneric(Register.class, Block.class, Waystones::registerBlocks);
      bus.registerGeneric(Register.class, Item.class, Waystones::registerItems);
      bus.register(ModelRegistryEvent.class, Waystones::registerModels);
   }

   public static void onConfigChanged(OnConfigChangedEvent event) {
      if ("waystones".equals(event.getModID())) {
         ConfigManager.sync("waystones", Type.INSTANCE);
      }

   }

   public static void registerBlocks(Register<Block> event) {
      event.getRegistry().registerAll(new Block[]{new BlockWaystone()});
   }

   public static void registerItems(Register<Item> event) {
      event.getRegistry().registerAll(new Item[]{(Item)(new ItemBlock(blockWaystone)).setRegistryName("waystone")});
      event.getRegistry().registerAll(new Item[]{(Item)(new ItemReturnScroll()).setRegistryName(ItemReturnScroll.registryName), (Item)(new ItemBoundScroll()).setRegistryName(ItemBoundScroll.registryName), new ItemWarpScroll(), new ItemWarpStone()});
   }

   public static void registerModels(ModelRegistryEvent event) {
      ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockWaystone), 0, new ModelResourceLocation(BlockWaystone.registryName, "inventory"));
      ModelLoader.setCustomModelResourceLocation(itemWarpStone, 0, new ModelResourceLocation(ItemWarpStone.registryName, "inventory"));
      ModelLoader.setCustomModelResourceLocation(itemReturnScroll, 0, new ModelResourceLocation(ItemReturnScroll.registryName, "inventory"));
      ModelLoader.setCustomModelResourceLocation(itemWarpScroll, 0, new ModelResourceLocation(ItemWarpScroll.registryName, "inventory"));
      ModelLoader.setCustomModelResourceLocation(itemBoundScroll, 0, new ModelResourceLocation(ItemBoundScroll.registryName, "inventory"));
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(blockWaystone), 0, TileWaystone.class);
   }

   public static void registerHolders() {
      ObjectHolderRegistry.INSTANCE.register(Block.class, new ResourceLocation("waystones:waystone"), v -> blockWaystone = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("waystones:return_scroll"), v -> itemReturnScroll = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("waystones:bound_scroll"), v -> itemBoundScroll = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("waystones:warp_scroll"), v -> itemWarpScroll = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("waystones:warp_stone"), v -> itemWarpStone = v);
   }
}

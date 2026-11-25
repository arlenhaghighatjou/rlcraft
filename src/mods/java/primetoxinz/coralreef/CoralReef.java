package primetoxinz.coralreef;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap.Builder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

@EventBusSubscriber(
   modid = "coralreef"
)
@Mod(
   modid = "coralreef",
   name = "CoralReef",
   version = "2.0",
   acceptedMinecraftVersions = "[1.12, 1.13)"
)
public class CoralReef {
   public static final String MODID = "coralreef";
   public static final String NAME = "CoralReef";
   public static final String VERSION = "2.0";
   public static BlockCoral CORAL = (BlockCoral)((Block)(new BlockCoral()).setRegistryName("coral")).setUnlocalizedName("coral");
   public static BlockReef REEF = (BlockReef)((Block)(new BlockReef()).setRegistryName("reef")).setUnlocalizedName("reef");
   @SidedProxy(
      clientSide = "primetoxinz.coralreef.CoralReef$ClientProxy",
      serverSide = "primetoxinz.coralreef.CoralReef$CommonProxy"
   )
   public static CoralReef.CommonProxy proxy;

   public CoralReef() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent e) {
      GameRegistry.registerWorldGenerator(new GeneratorReef(), 1);
   }

   @SubscribeEvent
   public static void registerBlocks(Register<Block> e) {
      e.getRegistry().registerAll(new Block[]{CORAL, REEF});
   }

   @SubscribeEvent
   public static void registerItem(Register<Item> e) {
      e.getRegistry().registerAll(new Item[]{(Item)(new ItemBlockMeta(CORAL)).setRegistryName(CORAL.getRegistryName()), (Item)(new ItemBlockMeta(REEF)).setRegistryName(REEF.getRegistryName())});
      proxy.registerItems();
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent e) {
      OreDictionary.registerOre("dyeOrange", new ItemStack(CORAL, 1, 0));
      OreDictionary.registerOre("dyeMagenta", new ItemStack(CORAL, 1, 1));
      OreDictionary.registerOre("dyePink", new ItemStack(CORAL, 1, 2));
      OreDictionary.registerOre("dyeCyan", new ItemStack(CORAL, 1, 3));
      OreDictionary.registerOre("dyeGreen", new ItemStack(CORAL, 1, 4));
      OreDictionary.registerOre("dyeGray", new ItemStack(CORAL, 1, 5));
   }

   @SubscribeEvent
   public static void onConfigChange(ConfigChangedEvent event) {
      if (event.getModID().equals("coralreef")) {
         ConfigManager.sync("coralreef", Type.INSTANCE);
      }

   }

   public static class ClientProxy extends CoralReef.CommonProxy {
      public ClientProxy() {
      }

      public void registerItems() {
         ModelLoader.setCustomStateMapper(CoralReef.CORAL, (new Builder()).ignore(new IProperty[]{BlockLiquid.LEVEL}).build());

         for(int i = 0; i <= 5; ++i) {
            this.registerItemModel(Item.getItemFromBlock(CoralReef.CORAL), i, "coralreef:coral" + i, "inventory");
         }

         for(int i = 0; i <= 1; ++i) {
            this.registerItemModel(Item.getItemFromBlock(CoralReef.REEF), i, "coralreef:reef", "types=" + i);
         }

      }

      public void registerItemModel(Item item, int meta, String name, String type) {
         ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(name, type));
      }
   }

   public static class CommonProxy {
      public CommonProxy() {
      }

      public void registerItems() {
      }
   }

   @Config(
      modid = "coralreef"
   )
   public static class ConfigHandler {
      @Comment({"Array of dimension ids in which Coral Reefs will spawn. Empty will allow all dimensions"})
      public static int[] dimensions = new int[]{0};
      @Comment({"Light level of coral"})
      @RequiresMcRestart
      public static int coralLightLevel = 15;
      @Comment({"Array of biomes which will allow coral reefs to spawn if there is water. Empty will allow all biomes"})
      public static String[] biomes = new String[]{"ocean", "deep ocean", "beach"};
      @Comment({"Bubble Particles from coral"})
      public static boolean bubbles = true;
      public static CoralReef.ConfigHandler.Reef reef = new CoralReef.ConfigHandler.Reef();
      public static CoralReef.ConfigHandler.Rock rock = new CoralReef.ConfigHandler.Rock();

      public ConfigHandler() {
      }

      public static class Reef {
         @RangeDouble(
            min = 0.0D,
            max = 1.0D
         )
         @Comment({"Percentage of the sparsity of the coral on a reef"})
         public double coralSparsity = 0.9D;
         @RangeDouble(
            min = 0.0D,
            max = 1.0D
         )
         @Comment({"Percentage of the sparsity of the reef blocks"})
         public double reefSparsity = 0.9D;

         public Reef() {
         }
      }

      public static class Rock {
         @Comment({"Chance for a Dry Reef to spawn"})
         public double chance = 0.5D;

         public Rock() {
         }
      }
   }
}

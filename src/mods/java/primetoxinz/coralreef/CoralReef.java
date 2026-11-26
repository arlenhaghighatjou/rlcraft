package primetoxinz.coralreef;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap.Builder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigEntry;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

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
   public static CoralReef.CommonProxy proxy;

   public CoralReef() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new CoralReef.ClientProxy() : new CoralReef.CommonProxy();
         CoralReef.ConfigHandler.register();
         primetoxinz.coralreef.CoralReef.register(MinecraftForge.EVENT_BUS);
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLPostInitializationEvent) {
         this.postInit((FMLPostInitializationEvent)event);
      }

   }

   public void preInit(FMLPreInitializationEvent e) {
      GameRegistry.registerWorldGenerator(new GeneratorReef(), 1);
   }

   public static void register(EventBus bus) {
      bus.registerGeneric(Register.class, Block.class, CoralReef::registerBlocks);
      bus.registerGeneric(Register.class, Item.class, CoralReef::registerItem);
      bus.register(ConfigChangedEvent.class, CoralReef::onConfigChange);
   }

   public static void registerBlocks(Register<Block> e) {
      e.getRegistry().registerAll(new Block[]{CORAL, REEF});
   }

   public static void registerItem(Register<Item> e) {
      e.getRegistry().registerAll(new Item[]{(Item)(new ItemBlockMeta(CORAL)).setRegistryName(CORAL.getRegistryName()), (Item)(new ItemBlockMeta(REEF)).setRegistryName(REEF.getRegistryName())});
      proxy.registerItems();
   }

   public void postInit(FMLPostInitializationEvent e) {
      OreDictionary.registerOre("dyeOrange", new ItemStack(CORAL, 1, 0));
      OreDictionary.registerOre("dyeMagenta", new ItemStack(CORAL, 1, 1));
      OreDictionary.registerOre("dyePink", new ItemStack(CORAL, 1, 2));
      OreDictionary.registerOre("dyeCyan", new ItemStack(CORAL, 1, 3));
      OreDictionary.registerOre("dyeGreen", new ItemStack(CORAL, 1, 4));
      OreDictionary.registerOre("dyeGray", new ItemStack(CORAL, 1, 5));
   }

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

      public static void register() {
         ConfigManager.register("coralreef", "", "general", null,
            ConfigEntry.value("dimensions", int[].class, () -> ConfigHandler.dimensions, v -> ConfigHandler.dimensions = (int[])v).comment("Array of dimension ids in which Coral Reefs will spawn. Empty will allow all dimensions").langKey("coralreef.general.dimensions"),
            ConfigEntry.value("coralLightLevel", int.class, () -> ConfigHandler.coralLightLevel, v -> ConfigHandler.coralLightLevel = (Integer)v).comment("Light level of coral").langKey("coralreef.general.corallightlevel").requiresMcRestart(),
            ConfigEntry.value("biomes", String[].class, () -> ConfigHandler.biomes, v -> ConfigHandler.biomes = (String[])v).comment("Array of biomes which will allow coral reefs to spawn if there is water. Empty will allow all biomes").langKey("coralreef.general.biomes"),
            ConfigEntry.value("bubbles", boolean.class, () -> ConfigHandler.bubbles, v -> ConfigHandler.bubbles = (Boolean)v).comment("Bubble Particles from coral").langKey("coralreef.general.bubbles"),
            ConfigEntry.category("reef",
               ConfigEntry.value("coralSparsity", double.class, () -> ConfigHandler.reef.coralSparsity, v -> ConfigHandler.reef.coralSparsity = (Double)v).comment("Percentage of the sparsity of the coral on a reef").langKey("coralreef.general.reef.coralsparsity").range(0.0, 1.0),
               ConfigEntry.value("reefSparsity", double.class, () -> ConfigHandler.reef.reefSparsity, v -> ConfigHandler.reef.reefSparsity = (Double)v).comment("Percentage of the sparsity of the reef blocks").langKey("coralreef.general.reef.reefsparsity").range(0.0, 1.0)).langKey("coralreef.general.reef"),
            ConfigEntry.category("rock",
               ConfigEntry.value("chance", double.class, () -> ConfigHandler.rock.chance, v -> ConfigHandler.rock.chance = (Double)v).comment("Chance for a Dry Reef to spawn").langKey("coralreef.general.rock.chance")).langKey("coralreef.general.rock"));
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

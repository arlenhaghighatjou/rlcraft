package ladysnake.spawnercontrol;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import ladysnake.spawnercontrol.config.CustomSpawnersConfig;
import ladysnake.spawnercontrol.config.MSCConfig;
import ladysnake.spawnercontrol.config.SpawnerConfigHolder;
import ladysnake.spawnercontrol.controlledspawner.CapabilityControllableSpawner;
import ladysnake.spawnercontrol.controlledspawner.IControllableSpawner;
import ladysnake.spawnercontrol.controlledspawner.TileEntityControlledSpawner;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.Logger;

@Mod(
   modid = "spawnercontrol",
   name = "Mob Spawner Control",
   version = "1.6.3b",
   acceptedMinecraftVersions = "[1.12, 1.13)",
   guiFactory = "ladysnake.spawnercontrol.client.MSCConfigFactory",
   dependencies = "required-after:forge@[14.23.2.2596,);",
   acceptableRemoteVersions = "*"
)
public class SpawnerControl {
   public static final String MOD_ID = "spawnercontrol";
   static final String MOD_NAME = "Mob Spawner Control";
   static final String VERSION = "1.6.3b";
   static final String ACCEPTED_VERSIONS = "[1.12, 1.13)";
   public static Logger LOGGER;
   public static CreativeTabs creativeTab;

   public SpawnerControl() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      LOGGER = event.getModLog();
      CustomSpawnersConfig.configDir = event.getModConfigurationDirectory();
      MSCConfig.portConfig();
      CustomSpawnersConfig.initCustomConfig();
      CapabilityManager.INSTANCE.register(IControllableSpawner.class, new CapabilityControllableSpawner.Storage(), CapabilityControllableSpawner.DefaultControllableSpawner::new);
      if (MSCConfig.customSpawners.length > 0) {
         GameRegistry.registerTileEntity(TileEntityControlledSpawner.class, "spawnercontrol:controlled_spawner");
      }

      if (MSCConfig.makeCreativeTab) {
         creativeTab = new CreativeTabs("spawnercontrol") {
            @Nonnull
            public ItemStack getTabIconItem() {
               return new ItemStack(Blocks.MOB_SPAWNER);
            }
         };
      }

   }

   @EventBusSubscriber
   public static class RegistryHandler {
      public RegistryHandler() {
      }

      @SubscribeEvent
      public static void onBlockRegister(Register<Block> event) {
         Stream var10000 = CustomSpawnersConfig.getCustomSpawnerConfigs().stream().map(SpawnerConfigHolder::createBlock);
         IForgeRegistry var10001 = event.getRegistry();
         var10000.forEach(var10001::register);
      }

      @SubscribeEvent
      public static void onItemRegister(Register<Item> event) {
         Stream var10000 = CustomSpawnersConfig.getCustomSpawnerConfigs().stream().map(SpawnerConfigHolder::createItem);
         IForgeRegistry var10001 = event.getRegistry();
         var10000.forEach(var10001::register);
      }

      @SubscribeEvent
      @SideOnly(Side.CLIENT)
      public static void registerRenders(ModelRegistryEvent event) {
         ModelResourceLocation mrl = new ModelResourceLocation("mob_spawner", "inventory");

         for(SpawnerConfigHolder spawnerConfigHolder : CustomSpawnersConfig.getCustomSpawnerConfigs()) {
            ModelLoader.setCustomModelResourceLocation(spawnerConfigHolder.getItem(), 0, mrl);
            mapToSpawnerModel(spawnerConfigHolder.getBlock(), mrl);
         }

      }

      @SideOnly(Side.CLIENT)
      private static void mapToSpawnerModel(Block block, final ModelResourceLocation mrl) {
         StateMapperBase ignoreState = new StateMapperBase() {
            @Nonnull
            protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState iBlockState) {
               return mrl;
            }
         };
         ModelLoader.setCustomStateMapper(block, ignoreState);
      }
   }
}

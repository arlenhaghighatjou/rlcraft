package ladysnake.spawnercontrol;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
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
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

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

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         ladysnake.spawnercontrol.config.MSCConfig.register();
         ladysnake.spawnercontrol.controlledspawner.CapabilityControllableSpawner.registerCapabilities();
         ladysnake.spawnercontrol.SpawnerControl.RegistryHandler.register(MinecraftForge.EVENT_BUS);
         ladysnake.spawnercontrol.SpawnerEventHandler.register(MinecraftForge.EVENT_BUS);
         ladysnake.spawnercontrol.config.MSCConfig.register(MinecraftForge.EVENT_BUS);
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      }

   }

   public void preInit(FMLPreInitializationEvent event) {
      LOGGER = event.getModLog();
      CustomSpawnersConfig.configDir = event.getModConfigurationDirectory();
      MSCConfig.portConfig();
      CustomSpawnersConfig.initCustomConfig();
      CapabilityManager.INSTANCE.register(IControllableSpawner.class, new CapabilityControllableSpawner.Storage(), CapabilityControllableSpawner.DefaultControllableSpawner::new);
      if (MSCConfig.customSpawners.length > 0) {
         GameRegistry.registerTileEntity(TileEntityControlledSpawner.class, new ResourceLocation("spawnercontrol:controlled_spawner"), TileEntityControlledSpawner::new);
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

   public static class RegistryHandler {
      public RegistryHandler() {
      }

      public static void register(EventBus bus) {
         bus.registerGeneric(Register.class, Block.class, RegistryHandler::onBlockRegister);
         bus.registerGeneric(Register.class, Item.class, RegistryHandler::onItemRegister);
         bus.register(ModelRegistryEvent.class, RegistryHandler::registerRenders);
      }

      public static void onBlockRegister(Register<Block> event) {
         for(SpawnerConfigHolder holder : CustomSpawnersConfig.getCustomSpawnerConfigs()) {
            event.getRegistry().register(holder.createBlock());
         }
      }

      public static void onItemRegister(Register<Item> event) {
         for(SpawnerConfigHolder holder : CustomSpawnersConfig.getCustomSpawnerConfigs()) {
            event.getRegistry().register(holder.createItem());
         }
      }

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

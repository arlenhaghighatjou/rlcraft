package artifacts;

import artifacts.common.IProxy;
import artifacts.common.init.ModEntities;
import artifacts.common.init.ModItems;
import artifacts.common.init.ModLootTables;
import artifacts.common.init.ModNetworkHandler;
import artifacts.common.init.ModRecipes;
import artifacts.common.init.ModSoundEvents;
import artifacts.common.init.ModWorldGen;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(
   modid = "artifacts",
   name = "RLArtifacts",
   version = "1.1.2",
   dependencies = "required-after:baubles"
)
public class Artifacts {
   public static final String MODID = "artifacts";
   public static final String MODNAME = "RLArtifacts";
   public static final String VERSION = "1.1.2";
   public static final CreativeTabs ARTIFACTS_TAB = new Artifacts.ArtifactTab();
   @Instance("artifacts")
   public static Artifacts instance;
   @SidedProxy(
      serverSide = "artifacts.common.ServerProxy",
      clientSide = "artifacts.client.ClientProxy"
   )
   public static IProxy proxy;

   public Artifacts() {
   }

   @EventHandler
   public static void preInit(FMLPreInitializationEvent event) {
      proxy.preInit();
      ModEntities.init();
   }

   @EventHandler
   public static void init(FMLInitializationEvent event) {
      proxy.init();
      ModNetworkHandler.init();
      ModLootTables.init();
      ModWorldGen.init();
   }

   @EventHandler
   public static void postInit(FMLInitializationEvent event) {
      proxy.postInit();
   }

   public static class ArtifactTab extends CreativeTabs {
      public ArtifactTab() {
         super("artifacts.creativetab");
      }

      public ItemStack getTabIconItem() {
         return new ItemStack(ModItems.PANIC_NECKLACE);
      }
   }

   @EventBusSubscriber
   public static class RegistrationHandler {
      public RegistrationHandler() {
      }

      @SubscribeEvent
      public static void registerSoundEvents(Register<SoundEvent> event) {
         ModSoundEvents.registerSoundEvents(event.getRegistry());
      }

      @SubscribeEvent
      public static void registerItems(Register<Item> event) {
         ModItems.registerItems(event.getRegistry());
      }

      @SubscribeEvent
      public static void registerRecipes(Register<IRecipe> event) {
         ModRecipes.registerRecipes(event.getRegistry());
      }

      @SubscribeEvent
      public static void registerItemModels(ModelRegistryEvent event) {
         ModItems.registerModels();
      }
   }
}

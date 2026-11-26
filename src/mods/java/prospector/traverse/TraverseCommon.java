package prospector.traverse;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.lax1dude.eaglercraft.v1_8.sp.server.WorldsDB;
import net.lax1dude.eaglercraft.v1_8.sp.server.EaglerSaveFormat;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import prospector.traverse.commands.CommandFindBiome;
import prospector.traverse.config.TraverseConfig;
import prospector.traverse.init.TraverseBlocks;
import prospector.traverse.shadow.shootingstar.ShootingStar;
import prospector.traverse.shadow.shootingstar.version.VersionUtils;
import prospector.traverse.util.TUtils;
import prospector.traverse.world.TraverseWorld;
import prospector.traverse.world.TraverseWorldVersion;

public class TraverseCommon {
   public static TraverseWorldVersion traverse_world_data = null;

   public TraverseCommon() {
   }

   static void registerSmeltingRecipe(ItemStack output, ItemStack input, float experience) {
      GameRegistry.addSmelting(input, output, experience);
   }

   public static void register(EventBus bus) {
      bus.register(ModelRegistryEvent.class, TraverseCommon::registerModels);
      bus.registerGeneric(Register.class, Block.class, TraverseCommon::registerBlocks);
      bus.registerGeneric(Register.class, Item.class, TraverseCommon::registerItems);
   }

   public static void registerModels(ModelRegistryEvent event) {
      ShootingStar.registerModels("traverse");
   }

   public static void registerBlocks(Register<Block> event) {
      ShootingStar.registerBlocks("traverse", event);
   }

   public static void registerItems(Register<Item> event) {
      ShootingStar.registerItems("traverse", event);
   }

   public void preInit(FMLPreInitializationEvent event) {
      TraverseBlocks.test();
      TraverseConfig.initialize(event);
      TraverseWorld.register(MinecraftForge.TERRAIN_GEN_BUS);
   }

   public void init(FMLInitializationEvent event) {
      for(Block block : TraverseBlocks.oreDictNames.keySet()) {
         OreDictionary.registerOre((String)TraverseBlocks.oreDictNames.get(block), block);
      }

      for(TraverseWorld.TraverseBiomeEntry traverseBiome : TraverseWorld.biomeList) {
         BiomeManager.addBiome(traverseBiome.getType(), traverseBiome.getEntry());
         if (traverseBiome.hasVillages()) {
            BiomeManager.addVillageBiome(traverseBiome.getBiome(), traverseBiome.canSpawn());
         }

         if (traverseBiome.canSpawn()) {
            BiomeManager.addSpawnBiome(traverseBiome.getBiome());
         }

         BiomeProvider.allowedBiomes.add(traverseBiome.getBiome());
      }

      registerSmeltingRecipe(new ItemStack(Items.COAL, 1, 1), new ItemStack(TUtils.getBlock("fir_log")), 0.15F);
      registerSmeltingRecipe(new ItemStack(TUtils.getBlock("red_rock")), new ItemStack(TUtils.getBlock("red_rock_cobblestone")), 0.1F);
      registerSmeltingRecipe(new ItemStack(TUtils.getBlock("blue_rock")), new ItemStack(TUtils.getBlock("blue_rock_cobblestone")), 0.1F);
   }

   public void postInit(FMLPostInitializationEvent event) {
   }

   public void serverStarting(FMLServerStartingEvent event) {
      event.registerServerCommand(new CommandFindBiome());
   }

   public void serverAboutToStart(FMLServerAboutToStartEvent event) {
      traverse_world_data = null;
      VFile2 worldDir = WorldsDB.newVFile(EaglerSaveFormat.worldsFolder, event.getServer().getFolderName());

      traverse_world_data = new TraverseWorldVersion(worldDir);

      for(TraverseWorld.TraverseBiomeEntry traverseBiome : TraverseWorld.biomeList) {
         BiomeProvider.allowedBiomes.remove(traverseBiome.getBiome());
         BiomeManager.removeSpawnBiome(traverseBiome.getBiome());
         BiomeManager.removeBiome(traverseBiome.getType(), traverseBiome.getEntry());
         boolean canRegister;
         if (!TraverseConfig.registerBiomesRegardless) {
            traverse_world_data.reloadVersionFile();
            canRegister = VersionUtils.isVersionLessOrEqual(traverseBiome.getVersionAdded(), traverse_world_data.version);
         } else {
            canRegister = true;
         }

         if (canRegister) {
            BiomeManager.addBiome(traverseBiome.getType(), traverseBiome.getEntry());
            if (traverseBiome.hasVillages()) {
               BiomeManager.addVillageBiome(traverseBiome.getBiome(), traverseBiome.canSpawn());
            }

            if (traverseBiome.canSpawn()) {
               BiomeManager.addSpawnBiome(traverseBiome.getBiome());
            }

            BiomeProvider.allowedBiomes.add(traverseBiome.getBiome());
         }
      }

   }

   public void serverStopping(FMLServerStoppingEvent event) {
   }
}

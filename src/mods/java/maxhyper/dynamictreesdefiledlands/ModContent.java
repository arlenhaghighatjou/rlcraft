package maxhyper.dynamictreesdefiledlands;

import com.ferreusveritas.dynamictrees.ModItems;
import com.ferreusveritas.dynamictrees.ModRecipes;
import com.ferreusveritas.dynamictrees.api.TreeRegistry;
import com.ferreusveritas.dynamictrees.api.WorldGenRegistry.BiomeDataBasePopulatorRegistryEvent;
import com.ferreusveritas.dynamictrees.api.client.ModelHelper;
import com.ferreusveritas.dynamictrees.api.treedata.ILeavesProperties;
import com.ferreusveritas.dynamictrees.blocks.BlockRooty;
import com.ferreusveritas.dynamictrees.blocks.LeavesPaging;
import com.ferreusveritas.dynamictrees.blocks.LeavesProperties;
import com.ferreusveritas.dynamictrees.items.DendroPotion.DendroPotionType;
import com.ferreusveritas.dynamictrees.systems.DirtHelper;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.trees.TreeFamily;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import lykrast.defiledlands.common.init.ModBlocks;
import maxhyper.dynamictreesdefiledlands.blocks.BlockRootyDefiledDirt;
import maxhyper.dynamictreesdefiledlands.trees.TreeTenebra;
import maxhyper.dynamictreesdefiledlands.worldgen.BiomeDataBasePopulator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.renderer.block.statemap.StateMap.Builder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

@EventBusSubscriber(
   modid = "dynamictreesdefiledlands"
)
@ObjectHolder("dynamictreesdefiledlands")
public class ModContent {
   public static ILeavesProperties dyingLeavesProperties;
   public static BlockRooty rootyDefiledDirt;
   public static ArrayList<TreeFamily> trees = new ArrayList();
   public static String CORRUPTDIRTLIKE = "corruptdirtlike";
   public static String CORRUPTSANDLIKE = "corruptsandlike";
   public static String CORRUPTGRAVELLIKE = "corruptgravellike";

   public ModContent() {
   }

   @SubscribeEvent
   public static void registerDataBasePopulators(BiomeDataBasePopulatorRegistryEvent event) {
      event.register(new BiomeDataBasePopulator());
   }

   @SubscribeEvent
   public static void registerBlocks(Register<Block> event) {
      DirtHelper.createNewAdjective(CORRUPTDIRTLIKE);
      DirtHelper.createNewAdjective(CORRUPTSANDLIKE);
      DirtHelper.createNewAdjective(CORRUPTGRAVELLIKE);
      IForgeRegistry<Block> registry = event.getRegistry();
      rootyDefiledDirt = new BlockRootyDefiledDirt(false);
      registry.register(rootyDefiledDirt);
      dyingLeavesProperties = new LeavesProperties(ModBlocks.tenebraLeaves.getDefaultState(), TreeRegistry.findCellKit("dynamictreesdefiledlands:sparse")) {
         public int getSmotherLeavesMax() {
            return 1;
         }

         public ItemStack getPrimitiveLeavesItemStack() {
            return new ItemStack(ModBlocks.tenebraLeaves);
         }
      };
      LeavesPaging.getLeavesBlockForSequence("dynamictreesdefiledlands", 0, dyingLeavesProperties);
      TreeFamily defiled = new TreeTenebra();
      Collections.addAll(trees, new TreeFamily[]{defiled});
      trees.forEach((tree) -> {
         tree.registerSpecies(Species.REGISTRY);
      });
      ArrayList<Block> treeBlocks = new ArrayList();
      trees.forEach((tree) -> {
         tree.getRegisterableBlocks(treeBlocks);
      });
      treeBlocks.addAll(LeavesPaging.getLeavesMapForModId("dynamictreesdefiledlands").values());
      registry.registerAll((IForgeRegistryEntry[])treeBlocks.toArray(new Block[treeBlocks.size()]));
      DirtHelper.registerSoil(rootyDefiledDirt, CORRUPTDIRTLIKE);
      DirtHelper.registerSoil(ModBlocks.dirtDefiled, CORRUPTDIRTLIKE);
      DirtHelper.registerSoil(ModBlocks.grassDefiled, CORRUPTDIRTLIKE);
      DirtHelper.registerSoil(ModBlocks.sandDefiled, CORRUPTSANDLIKE);
      DirtHelper.registerSoil(ModBlocks.gravelDefiled, CORRUPTGRAVELLIKE);
   }

   public static ILeavesProperties setUpLeaves(final Block leavesBlock, String cellKit) {
      ILeavesProperties leavesProperties = new LeavesProperties(leavesBlock.getDefaultState(), new ItemStack(leavesBlock, 1, 0), TreeRegistry.findCellKit(cellKit)) {
         public ItemStack getPrimitiveLeavesItemStack() {
            return new ItemStack(leavesBlock, 1, 0);
         }

         public int getLightRequirement() {
            return 1;
         }
      };
      return leavesProperties;
   }

   @SubscribeEvent
   public static void registerItems(Register<Item> event) {
      IForgeRegistry<Item> registry = event.getRegistry();
      ArrayList<Item> treeItems = new ArrayList();
      trees.forEach((tree) -> {
         tree.getRegisterableItems(treeItems);
      });
      registry.registerAll((IForgeRegistryEntry[])treeItems.toArray(new Item[treeItems.size()]));
   }

   @SubscribeEvent
   public static void registerRecipes(Register<IRecipe> event) {
   }

   public static void setUpSeedRecipes(String name, ItemStack treeSapling) {
      Species treeSpecies = TreeRegistry.findSpecies(new ResourceLocation("dynamictreesdefiledlands", name));
      ItemStack treeSeed = treeSpecies.getSeedStack(1);
      ItemStack treeTransformationPotion = ModItems.dendroPotion.setTargetTree(new ItemStack(ModItems.dendroPotion, 1, DendroPotionType.TRANSFORM.getIndex()), treeSpecies.getFamily());
      BrewingRecipeRegistry.addRecipe(new ItemStack(ModItems.dendroPotion, 1, DendroPotionType.TRANSFORM.getIndex()), treeSeed, treeTransformationPotion);
      ModRecipes.createDirtBucketExchangeRecipes(treeSapling, treeSeed, true);
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public static void registerModels(ModelRegistryEvent event) {
      for(TreeFamily tree : trees) {
         ModelHelper.regModel(tree.getDynamicBranch());
         ModelHelper.regModel(tree.getCommonSpecies().getSeed());
         ModelHelper.regModel(tree);
      }

      LeavesPaging.getLeavesMapForModId("dynamictreesdefiledlands").forEach((key, leaves) -> {
         ModelLoader.setCustomStateMapper(leaves, (new Builder()).ignore(new IProperty[]{BlockLeaves.DECAYABLE}).build());
      });
      ModelLoader.setCustomStateMapper(rootyDefiledDirt, (new Builder()).ignore(new IProperty[]{BlockRooty.LIFE}).build());
   }
}

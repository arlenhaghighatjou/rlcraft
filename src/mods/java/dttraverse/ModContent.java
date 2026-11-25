package dttraverse;

import com.ferreusveritas.dynamictrees.ModItems;
import com.ferreusveritas.dynamictrees.ModRecipes;
import com.ferreusveritas.dynamictrees.api.TreeRegistry;
import com.ferreusveritas.dynamictrees.api.WorldGenRegistry.BiomeDataBasePopulatorRegistryEvent;
import com.ferreusveritas.dynamictrees.api.client.ModelHelper;
import com.ferreusveritas.dynamictrees.api.treedata.ILeavesProperties;
import com.ferreusveritas.dynamictrees.blocks.LeavesPaging;
import com.ferreusveritas.dynamictrees.blocks.LeavesProperties;
import com.ferreusveritas.dynamictrees.items.DendroPotion.DendroPotionType;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.trees.TreeFamily;
import dttraverse.trees.SpeciesAutumnBrown;
import dttraverse.trees.SpeciesAutumnOrange;
import dttraverse.trees.SpeciesAutumnRed;
import dttraverse.trees.SpeciesAutumnYellow;
import dttraverse.trees.SpeciesMiniJungle;
import dttraverse.trees.SpeciesOakShrub;
import dttraverse.trees.TreeFir;
import dttraverse.worldgen.BiomeDataBasePopulator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import prospector.traverse.init.TraverseBlocks;

@EventBusSubscriber(
   modid = "dttraverse"
)
public final class ModContent {
   public static ILeavesProperties firLeavesProperties;
   public static ILeavesProperties autumnYellowLeavesProperties;
   public static ILeavesProperties autumnOrangeLeavesProperties;
   public static ILeavesProperties autumnRedLeavesProperties;
   public static ILeavesProperties autumnBrownLeavesProperties;
   public static final ArrayList<TreeFamily> TREES = new ArrayList();
   public static final ArrayList<Species> SPECIES = new ArrayList();

   public ModContent() {
   }

   @SubscribeEvent
   public static void registerDataBasePopulators(BiomeDataBasePopulatorRegistryEvent event) {
      event.register(new BiomeDataBasePopulator());
   }

   @SubscribeEvent
   public static void registerBlocks(Register<Block> event) {
      IForgeRegistry<Block> registry = event.getRegistry();
      TreeFamily oakTree = TreeRegistry.findSpecies(new ResourceLocation("dynamictrees", "oak")).getFamily();
      TreeFamily birchTree = TreeRegistry.findSpecies(new ResourceLocation("dynamictrees", "birch")).getFamily();
      TreeFamily jungleTree = TreeRegistry.findSpecies(new ResourceLocation("dynamictrees", "jungle")).getFamily();
      firLeavesProperties = setUpLeaves((Block)TraverseBlocks.blocks.get("fir_leaves"), "conifer", 5);
      autumnBrownLeavesProperties = setUpLeaves((Block)TraverseBlocks.blocks.get("brown_autumnal_leaves"), "deciduous", 1);
      autumnOrangeLeavesProperties = setUpLeaves((Block)TraverseBlocks.blocks.get("orange_autumnal_leaves"), "deciduous", 4);
      autumnRedLeavesProperties = setUpLeaves((Block)TraverseBlocks.blocks.get("red_autumnal_leaves"), "deciduous", 4);
      autumnYellowLeavesProperties = setUpLeaves((Block)TraverseBlocks.blocks.get("yellow_autumnal_leaves"), "deciduous", 4);
      LeavesPaging.getLeavesBlockForSequence("dttraverse", 0, firLeavesProperties);
      LeavesPaging.getLeavesBlockForSequence("dttraverse", 4, autumnYellowLeavesProperties);
      LeavesPaging.getLeavesBlockForSequence("dttraverse", 8, autumnOrangeLeavesProperties);
      LeavesPaging.getLeavesBlockForSequence("dttraverse", 12, autumnRedLeavesProperties);
      LeavesPaging.getLeavesBlockForSequence("dttraverse", 16, autumnBrownLeavesProperties);
      SPECIES.addAll(Arrays.asList(new SpeciesAutumnYellow(birchTree), new SpeciesAutumnOrange(oakTree), new SpeciesAutumnRed(oakTree), new SpeciesAutumnBrown(oakTree), new SpeciesMiniJungle(jungleTree), new SpeciesOakShrub(oakTree)));
      ArrayList var10000 = SPECIES;
      IForgeRegistry var10001 = Species.REGISTRY;
      Species.REGISTRY.getClass();
      var10000.forEach(var10001::register);
      TreeFamily firTree = new TreeFir();
      Collections.addAll(TREES, new TreeFamily[]{firTree});
      TREES.forEach((tree) -> {
         tree.registerSpecies(Species.REGISTRY);
      });
      ArrayList<Block> treeBlocks = new ArrayList();
      TREES.forEach((tree) -> {
         tree.getRegisterableBlocks(treeBlocks);
      });
      treeBlocks.addAll(LeavesPaging.getLeavesMapForModId("dttraverse").values());
      registry.registerAll((IForgeRegistryEntry[])treeBlocks.toArray(new Block[treeBlocks.size()]));
   }

   public static ILeavesProperties setUpLeaves(final Block primitiveLeavesBlock, String cellKit, final int smotherLeavesMax) {
      return new LeavesProperties(primitiveLeavesBlock.getDefaultState(), new ItemStack(primitiveLeavesBlock, 1, 0), TreeRegistry.findCellKit(cellKit)) {
         public ItemStack getPrimitiveLeavesItemStack() {
            return new ItemStack(primitiveLeavesBlock, 1, 0);
         }

         public int getSmotherLeavesMax() {
            return smotherLeavesMax;
         }

         public int getLightRequirement() {
            return 1;
         }
      };
   }

   @SubscribeEvent
   public static void registerItems(Register<Item> event) {
      IForgeRegistry<Item> registry = event.getRegistry();
      SPECIES.forEach((species) -> {
         if (species.getFamily().getCommonSpecies().getSeed() != species.getSeed()) {
            registry.register(species.getSeed());
         }

      });
      ArrayList<Item> treeItems = new ArrayList();
      TREES.forEach((tree) -> {
         tree.getRegisterableItems(treeItems);
      });
      registry.registerAll((IForgeRegistryEntry[])treeItems.toArray(new Item[treeItems.size()]));
   }

   @SubscribeEvent
   public static void registerRecipes(Register<IRecipe> event) {
      setupRecipes("fir", "fir_sapling");
      setupRecipes("autumn_yellow", "yellow_autumnal_sapling");
      setupRecipes("autumn_orange", "orange_autumnal_sapling");
      setupRecipes("autumn_red", "red_autumnal_sapling");
      setupRecipes("autumn_red", "red_brown_sapling");
   }

   public static void setupRecipes(String species, String sapling) {
      Species specie = TreeRegistry.findSpecies(new ResourceLocation("dttraverse", species));
      ItemStack seed = specie.getSeedStack(1);
      ItemStack transofrmationPotion = ModItems.dendroPotion.setTargetTree(new ItemStack(ModItems.dendroPotion, 1, DendroPotionType.TRANSFORM.getIndex()), specie.getFamily());
      BrewingRecipeRegistry.addRecipe(new ItemStack(ModItems.dendroPotion, 1, DendroPotionType.TRANSFORM.getIndex()), seed, transofrmationPotion);
      ModRecipes.createDirtBucketExchangeRecipes(new ItemStack((Block)TraverseBlocks.blocks.get(sapling)), seed, true);
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public static void registerModels(ModelRegistryEvent event) {
      for(TreeFamily tree : TREES) {
         ModelHelper.regModel(tree.getDynamicBranch());
         ModelHelper.regModel(tree.getCommonSpecies().getSeed());
         ModelHelper.regModel(tree);
      }

      ModelHelper.regModel(TreeRegistry.findSpecies(new ResourceLocation("dttraverse", "autumn_yellow")).getSeed());
      ModelHelper.regModel(TreeRegistry.findSpecies(new ResourceLocation("dttraverse", "autumn_orange")).getSeed());
      ModelHelper.regModel(TreeRegistry.findSpecies(new ResourceLocation("dttraverse", "autumn_red")).getSeed());
      ModelHelper.regModel(TreeRegistry.findSpecies(new ResourceLocation("dttraverse", "autumn_brown")).getSeed());
      LeavesPaging.getLeavesMapForModId("dttraverse").forEach((key, leaves) -> {
         ModelLoader.setCustomStateMapper(leaves, (new Builder()).ignore(new IProperty[]{BlockLeaves.DECAYABLE}).build());
      });
   }
}

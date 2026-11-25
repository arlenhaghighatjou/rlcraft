package knightminer.inspirations.library;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.event.RegisterEvent;
import knightminer.inspirations.library.recipe.cauldron.CauldronFluidRecipe;
import knightminer.inspirations.library.recipe.cauldron.CauldronFluidTransformRecipe;
import knightminer.inspirations.library.recipe.cauldron.FillCauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import knightminer.inspirations.library.util.RecipeMatchOredictStack;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDeadBush;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockLilyPad;
import net.minecraft.block.BlockMushroom;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import slimeknights.mantle.util.RecipeMatch;

public class InspirationsRegistry {
   public static final Logger log = Util.getLogger("api");
   private static Map<ItemMetaKey, Float> books = new HashMap();
   private static String[] bookKeywords = new String[0];
   private static Map<ItemMetaKey, Integer> flowers = new HashMap();
   private static Map<IBlockState, IBlockState> anvilSmashing = new HashMap();
   private static Map<Block, IBlockState> anvilSmashingBlocks = new HashMap();
   private static Set<Material> anvilBreaking = new HashSet();
   private static List<ICauldronRecipe> cauldronRecipes = new ArrayList();
   private static Set<ItemMetaKey> cauldronBlacklist = new HashSet();
   private static boolean cauldronBigger = false;
   private static boolean expensiveCauldronBrewing = false;
   private static Set<Fluid> cauldronWater = new HashSet();
   private static Set<Block> cauldronFireBlocks = new HashSet();
   private static Set<IBlockState> cauldronFireStates = new HashSet();
   private static Map<Block, ICauldronRecipe.CauldronState> cauldronBlockStates = new HashMap();
   private static Map<ICauldronRecipe.CauldronState, IBlockState> cauldronFullStates = new HashMap();

   public InspirationsRegistry() {
   }

   /** @deprecated */
   @Deprecated
   public static void setConfig(String key, boolean value) {
      byte var3 = -1;
      switch(key.hashCode()) {
      case -1148671402:
         if (key.equals("biggerCauldron")) {
            var3 = 0;
         }
         break;
      case 1659279827:
         if (key.equals("expensiveCauldronBrewing")) {
            var3 = 1;
         }
      }

      switch(var3) {
      case 0:
         cauldronBigger = value;
         break;
      case 1:
         expensiveCauldronBrewing = value;
         break;
      default:
         throw new IllegalArgumentException("Unexpected config key " + key);
      }

   }

   public static boolean isBook(ItemStack stack) {
      return !stack.isEmpty() && getBookEnchantingPower(stack) >= 0.0F;
   }

   public static float getBookEnchantingPower(ItemStack book) {
      return book.isEmpty() ? 0.0F : ((Float)books.computeIfAbsent(new ItemMetaKey(book), InspirationsRegistry::isBook)).floatValue();
   }

   @Nonnull
   private static Float isBook(ItemMetaKey key) {
      Item item = key.getItem();
      if (Block.getBlockFromItem(item) != Blocks.AIR) {
         return -1.0F;
      } else {
         ItemStack stack = key.makeItemStack();

         for(String keyword : bookKeywords) {
            if (item.getRegistryName().getResourcePath().contains(keyword) || stack.getUnlocalizedName().contains(keyword)) {
               return Config.defaultEnchantingPower;
            }
         }

         return -1.0F;
      }
   }

   /** @deprecated */
   @Deprecated
   public static void registerBook(ItemStack stack, boolean isBook) {
      registerBook(stack, isBook ? 1.5F : -1.0F);
   }

   /** @deprecated */
   @Deprecated
   public static void registerBook(Item item, int meta, boolean isBook) {
      books.put(new ItemMetaKey(item, meta), Float.valueOf(isBook ? 1.5F : -1.0F));
   }

   public static void registerBook(ItemStack stack, float power) {
      books.put(new ItemMetaKey(stack), Float.valueOf(power));
   }

   public static void setBookKeywords(String[] keywords) {
      bookKeywords = keywords;
   }

   public static boolean isFlower(ItemStack stack) {
      return getFlowerComparatorPower(stack) > 0;
   }

   public static int getFlowerComparatorPower(ItemStack stack) {
      return ((Integer)flowers.computeIfAbsent(new ItemMetaKey(stack), (key) -> {
         Block block = Block.getBlockFromItem(key.getItem());
         if (block instanceof BlockBush && !(block instanceof BlockDoublePlant) && !(block instanceof BlockTallGrass) && !(block instanceof BlockCrops) && !(block instanceof BlockLilyPad)) {
            if (block instanceof BlockSapling) {
               return Integer.valueOf(12);
            } else if (block instanceof BlockDeadBush) {
               return Integer.valueOf(10);
            } else {
               return block instanceof BlockMushroom ? Integer.valueOf(1) : Integer.valueOf(7);
            }
         } else {
            return Integer.valueOf(0);
         }
      })).intValue();
   }

   /** @deprecated */
   @Deprecated
   public static void registerFlower(ItemStack stack, boolean isFlower) {
      registerFlower(stack, isFlower ? 7 : 0);
   }

   public static void registerFlower(ItemStack stack, int power) {
      flowers.put(new ItemMetaKey(stack), Integer.valueOf(power));
   }

   /** @deprecated */
   @Deprecated
   public static void registerFlower(Block block, int meta, boolean isFlower) {
      registerFlower(block, meta, isFlower ? 7 : 0);
   }

   public static void registerFlower(Block block, int meta, int power) {
      Item item = Item.getItemFromBlock(block);
      if (item != Items.AIR) {
         flowers.put(new ItemMetaKey(item, meta), Integer.valueOf(power));
      }

   }

   public static void registerAnvilSmashing(IBlockState input, IBlockState result) {
      anvilSmashing.put(input, result);
   }

   public static void registerAnvilSmashing(IBlockState input, Block result) {
      registerAnvilSmashing(input, result.getDefaultState());
   }

   public static void registerAnvilBreaking(IBlockState input) {
      registerAnvilSmashing(input, Blocks.AIR);
   }

   public static void registerAnvilSmashing(Block input, IBlockState result) {
      anvilSmashingBlocks.put(input, result);
   }

   public static void registerAnvilSmashing(Block input, Block result) {
      registerAnvilSmashing(input, result.getDefaultState());
   }

   public static void registerAnvilBreaking(Block input) {
      registerAnvilSmashing(input, Blocks.AIR);
   }

   public static void registerAnvilBreaking(Material material) {
      anvilBreaking.add(material);
   }

   public static IBlockState getAnvilSmashResult(IBlockState state) {
      if (anvilSmashing.containsKey(state)) {
         return (IBlockState)anvilSmashing.get(state);
      } else {
         Block block = state.getBlock();
         if (anvilSmashingBlocks.containsKey(block)) {
            return (IBlockState)anvilSmashingBlocks.get(block);
         } else {
            return anvilBreaking.contains(state.getMaterial()) ? Blocks.AIR.getDefaultState() : null;
         }
      }
   }

   public static boolean hasAnvilSmashStateResult(IBlockState state) {
      return anvilSmashing.containsKey(state);
   }

   public static List<Entry<IBlockState, IBlockState>> getAllAnvilStateSmashing() {
      return ImmutableList.copyOf(anvilSmashing.entrySet());
   }

   public static List<Entry<Block, IBlockState>> getAllAnvilBlockSmashing() {
      return ImmutableList.copyOf(anvilSmashingBlocks.entrySet());
   }

   public static ICauldronRecipe getCauldronResult(ItemStack input, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
      for(ICauldronRecipe recipe : cauldronRecipes) {
         if (recipe.matches(input, boiling, level, state)) {
            return recipe;
         }
      }

      return null;
   }

   public static void addCauldronRecipe(ICauldronRecipe recipe) {
      if ((new RegisterEvent.RegisterCauldronRecipe(recipe)).fire()) {
         cauldronRecipes.add(recipe);
      } else {
         log.debug("Cauldron recipe '{}' canceled by event", recipe);
      }

   }

   public static void addCauldronRecipe(ItemStack input, ItemStack output, Boolean boiling) {
      addCauldronRecipe(new CauldronFluidRecipe(RecipeMatch.of(input), output, boiling));
   }

   public static void addCauldronRecipe(String input, ItemStack output, Boolean boiling) {
      addCauldronRecipe(new CauldronFluidRecipe(RecipeMatch.of(input), output, boiling));
   }

   public static void addCauldronScaledTransformRecipe(ItemStack stack, Fluid input, Fluid output, Boolean boiling) {
      addCauldronRecipe(new CauldronFluidTransformRecipe(RecipeMatch.of(stack, stack.getCount(), 1), input, output, boiling, 1));
      stack = stack.copy();
      int count = stack.getCount();
      stack.setCount(count * 2);
      if (Config.enableBiggerCauldron) {
         addCauldronRecipe(new CauldronFluidTransformRecipe(RecipeMatch.of(stack, stack.getCount(), 1), input, output, boiling, 2));
         stack = stack.copy();
         stack.setCount(count * 3);
      }

      addCauldronRecipe(new CauldronFluidTransformRecipe(RecipeMatch.of(stack, stack.getCount(), 1), input, output, boiling, Config.enableBiggerCauldron ? 4 : 3));
   }

   public static void addCauldronScaledTransformRecipe(String name, int count, Fluid input, Fluid output, Boolean boiling) {
      addCauldronRecipe(new CauldronFluidTransformRecipe(new RecipeMatchOredictStack(name, count, 1), input, output, boiling, 1));
      int fullCount = count * 2;
      if (Config.enableBiggerCauldron) {
         addCauldronRecipe(new CauldronFluidTransformRecipe(new RecipeMatchOredictStack(name, count * 2, 1), input, output, boiling, 2));
         fullCount = count * 3;
      }

      addCauldronRecipe(new CauldronFluidTransformRecipe(new RecipeMatchOredictStack(name, fullCount, 1), input, output, boiling, Config.enableBiggerCauldron ? 4 : 3));
   }

   public static void addCauldronFluidItem(ItemStack filled, ItemStack container, Fluid fluid, int amount) {
      addCauldronRecipe(new FillCauldronRecipe(RecipeMatch.of(filled), fluid, amount, container.copy()));
      addCauldronRecipe(new CauldronFluidRecipe(RecipeMatch.of(container), fluid, filled.copy(), (Boolean)null, amount, SoundEvents.ITEM_BOTTLE_FILL));
   }

   public static void addCauldronFluidItem(ItemStack filled, ItemStack container, Fluid fluid) {
      addCauldronFluidItem(filled, container, fluid, 1);
   }

   public static List<ICauldronRecipe> getAllCauldronRecipes() {
      return ImmutableList.copyOf(cauldronRecipes);
   }

   public static void addCauldronBlacklist(Item item, int meta) {
      cauldronBlacklist.add(new ItemMetaKey(item, meta));
   }

   public static boolean isCauldronBlacklist(ItemStack stack) {
      return cauldronBlacklist.contains(new ItemMetaKey(stack)) || cauldronBlacklist.contains(new ItemMetaKey(stack.getItem(), 32767));
   }

   public static int getCauldronMax() {
      return cauldronBigger ? 4 : 3;
   }

   public static boolean expensiveCauldronBrewing() {
      return expensiveCauldronBrewing;
   }

   public static void addCauldronWater(Fluid fluid) {
      cauldronWater.add(fluid);
   }

   public static boolean isCauldronWater(Fluid fluid) {
      return fluid != null && cauldronWater.contains(fluid);
   }

   public static void registerCauldronFire(Block block) {
      cauldronFireBlocks.add(block);
   }

   public static void registerCauldronFire(IBlockState block) {
      cauldronFireStates.add(block);
   }

   public static boolean isCauldronFire(IBlockState state) {
      return cauldronFireBlocks.contains(state.getBlock()) || cauldronFireStates.contains(state);
   }

   public static void registerDefaultCauldron() {
      cauldronBlockStates.put(Blocks.CAULDRON, ICauldronRecipe.CauldronState.WATER);
   }

   public static void registerFullCauldron(IBlockState state, ICauldronRecipe.CauldronState cauldronState) {
      cauldronBlockStates.put(state.getBlock(), cauldronState);
      cauldronFullStates.put(cauldronState, state);
   }

   public static boolean isNormalCauldron(IBlockState state) {
      return cauldronBlockStates.containsKey(state.getBlock());
   }

   public static ICauldronRecipe.CauldronState getCauldronState(IBlockState state) {
      Block block = state.getBlock();
      if (cauldronBlockStates.containsKey(block)) {
         return (ICauldronRecipe.CauldronState)cauldronBlockStates.get(block);
      } else {
         throw new IllegalArgumentException("Attempted to get state of a cauldron that is not registered");
      }
   }

   public static boolean hasFullCauldron(ICauldronRecipe.CauldronState state) {
      return cauldronFullStates.containsKey(state);
   }

   @Nullable
   public static IBlockState getFullCauldron(ICauldronRecipe.CauldronState state) {
      return (IBlockState)cauldronFullStates.get(state);
   }
}

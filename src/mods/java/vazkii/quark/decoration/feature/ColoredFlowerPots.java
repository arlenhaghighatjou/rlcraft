package vazkii.quark.decoration.feature;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDeadBush;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockLilyPad;
import net.minecraft.block.BlockMushroom;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.BlockTallGrass.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.commons.lang3.tuple.Pair;
import vazkii.arl.client.RetexturedModel;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.Quark;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.base.module.Feature;
import vazkii.quark.decoration.block.BlockColoredFlowerPot;

public class ColoredFlowerPots extends Feature {
   public static BlockColoredFlowerPot[] pots;
   public static boolean enableComparatorLogic;
   public static String[] overrides;
   private static final Map<Pair<Item, Integer>, Integer> flowers = new HashMap();
   private static boolean loadedConfig = false;
   private static final Set<Item> unsafeBlockColors = new HashSet();

   public ColoredFlowerPots() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      pots = new BlockColoredFlowerPot[EnumDyeColor.values().length];

      for(int i = 0; i < pots.length; ++i) {
         pots[i] = new BlockColoredFlowerPot(EnumDyeColor.byMetadata(i));
         RecipeHandler.addShapelessOreDictRecipe(ProxyRegistry.newStack(pots[i]), new Object[]{ProxyRegistry.newStack(Items.FLOWER_POT), LibMisc.OREDICT_DYES.get(15 - i)});
      }

   }

   public void setupConfig() {
      enableComparatorLogic = this.loadPropBool("Comparator Logic", "If true, filled flower pots will respond to comparators based on the contents.", true);
      overrides = this.loadPropStringList("Flower Overrides", "List of stacks to override default flower behavior, default checks for BlockBush.\nFormat is 'modid:name[:meta]->power'. Unset meta will default wildcard. Power refers to comparator power, non-zero makes it valid for a flower pot. Specific values:\n* 0 - not flower, blacklists from placing in a flower pot\n* 1 - mushroom\n* 4 - fern\n* 7 - flower\n* 10 - dead bush\n* 12 - sapling\n* 15 - cactus", new String[]{"biomesoplenty:mushroom->1", "biomesoplenty:flower_0->7", "biomesoplenty:flower_1->7", "biomesoplenty:sapling_0->12", "biomesoplenty:sapling_1->12", "biomesoplenty:sapling_2->12", "quark:glowshroom->1", "botania:specialflower->0", "botania:floatingspecialflower->0"});
   }

   public void init() {
      loadFlowersFromConfig();
   }

   public static void loadFlowersFromConfig() {
      if (!loadedConfig) {
         loadedConfig = true;
         registerFlower(new ItemStack(Blocks.CACTUS), 15);
         registerFlower(new ItemStack(Blocks.TALLGRASS, 1, EnumType.FERN.getMeta()), 4);

         for(String line : overrides) {
            String[] split = line.split("->");
            if (split.length != 2) {
               Quark.LOG.error("Invalid Quark flower pot override, expected format 'modid:name[:meta]->power'");
            } else {
               String[] itemData = split[0].split(":");
               if (itemData.length >= 2 && itemData.length <= 3) {
                  int power;
                  try {
                     power = Integer.parseInt(split[1]);
                  } catch (NumberFormatException var13) {
                     power = -1;
                  }

                  if (power >= 0 && power <= 15) {
                     ResourceLocation location = new ResourceLocation(itemData[0], itemData[1]);
                     Item item = (Item)GameRegistry.findRegistry(Item.class).getValue(location);
                     if (item != null && item != Items.AIR) {
                        if (itemData.length == 3) {
                           int meta;
                           try {
                              meta = Integer.parseInt(itemData[2]);
                           } catch (NumberFormatException var12) {
                              meta = -1;
                           }

                           if (meta < 0) {
                              Quark.LOG.error("Invalid Quark flower pot override, meta must be a valid positive number");
                           } else {
                              registerFlower(new ItemStack(item, 1, meta), power);
                           }
                        } else {
                           NonNullList<ItemStack> subItems = NonNullList.create();
                           item.getSubItems(CreativeTabs.SEARCH, subItems);

                           for(ItemStack stack : subItems) {
                              registerFlower(stack, power);
                           }
                        }
                     } else {
                        Quark.LOG.debug("Unable to find item {} for Quark flower override", location.toString());
                     }
                  } else {
                     Quark.LOG.error("Invalid Quark flower pot override, power must be a valid number from 0 to 15");
                  }
               } else {
                  Quark.LOG.error("Invalid Quark flower pot override, expected format 'modid:name[:meta]->power'");
               }
            }
         }

      }
   }

   public static boolean isFlower(ItemStack stack) {
      return getFlowerComparatorPower(stack) > 0;
   }

   public static int getFlowerComparatorPower(ItemStack stack) {
      return ((Integer)flowers.computeIfAbsent(Pair.of(stack.getItem(), Integer.valueOf(stack.getMetadata())), (key) -> {
         Block block = Block.getBlockFromItem((Item)key.getLeft());
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

   public static void registerFlower(ItemStack stack, int power) {
      flowers.put(Pair.of(stack.getItem(), Integer.valueOf(stack.getMetadata())), Integer.valueOf(power));
   }

   public static int getStackBlockColorsSafe(ItemStack stack, @Nullable IBlockAccess world, @Nullable BlockPos pos, int index) {
      if (stack.isEmpty()) {
         return -1;
      } else {
         Item item = stack.getItem();
         if (!unsafeBlockColors.contains(item)) {
            try {
               return getStackBlockColors(stack, world, pos, index);
            } catch (Exception var6) {
               (new RuntimeException(item.getRegistryName() + " errored on getting flower pot block colors", var6)).printStackTrace();
               unsafeBlockColors.add(item);
            }
         }

         return Minecraft.getMinecraft().getItemColors().colorMultiplier(stack, index);
      }
   }

   public static int getStackBlockColors(ItemStack stack, @Nullable IBlockAccess world, @Nullable BlockPos pos, int index) {
      if (!stack.isEmpty() && stack.getItem() instanceof ItemBlock) {
         ItemBlock item = (ItemBlock)stack.getItem();
         IBlockState iblockstate = item.getBlock().getStateFromMeta(item.getMetadata(stack));
         return Minecraft.getMinecraft().getBlockColors().colorMultiplier(iblockstate, world, pos, index);
      } else {
         return -1;
      }
   }

   public boolean hasSubscriptions() {
      return this.isClient();
   }

   @SubscribeEvent
   public void onModelBake(ModelBakeEvent event) {
      for(EnumDyeColor color : EnumDyeColor.values()) {
         ResourceLocation loc = pots[color.getMetadata()].getRegistryName();
         if (loc != null) {
            ModelResourceLocation location = new ModelResourceLocation(loc, "contents=custom");
            IModel model = ModelLoaderRegistry.getModelOrLogError(location, "Error loading model for " + location);
            IBakedModel standard = (IBakedModel)event.getModelRegistry().getObject(location);
            IBakedModel finalModel = new RetexturedModel(standard, model, DefaultVertexFormats.BLOCK, "plant");
            event.getModelRegistry().putObject(location, finalModel);
         }
      }

   }
}

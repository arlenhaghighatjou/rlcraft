package knightminer.inspirations.library.util;

import com.google.common.collect.UnmodifiableIterator;
import java.util.function.Consumer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.commons.lang3.tuple.Pair;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public final class RecipeUtil {
   private static final Logger log = LogManager.getLogger("inspirations-lib");

   private RecipeUtil() {
   }

   public static boolean isValidItemStack(String string, boolean allowWildcard) {
      String metaString = allowWildcard ? "(-1|[0-9]+)" : "[0-9]+";
      return string.matches("^[a-z0-9_.-]+:[a-z0-9_.-]+(:" + metaString + ")?$");
   }

   public static ItemStack getItemStackFromString(String string, boolean allowWildcard) {
      if (!isValidItemStack(string, allowWildcard)) {
         log.warn("Invalid stack string {}", string);
         return ItemStack.EMPTY;
      } else {
         String[] parts = string.split(":");
         Item item = (Item)GameRegistry.findRegistry(Item.class).getValue(new ResourceLocation(parts[0], parts[1]));
         if (item != null && item != Items.AIR) {
            int meta = allowWildcard ? -1 : 0;
            if (parts.length > 2) {
               meta = Integer.parseInt(parts[2]);
            }

            if (meta == -1) {
               meta = 32767;
            }

            return new ItemStack(item, 1, meta);
         } else {
            log.debug("Failed to find stack {}", string);
            return ItemStack.EMPTY;
         }
      }
   }

   public static void forStackInString(String string, Consumer<ItemStack> callback) {
      ItemStack stack = getItemStackFromString(string, true);
      if (!stack.isEmpty()) {
         if (stack.getMetadata() == 32767) {
            NonNullList<ItemStack> subItems = NonNullList.create();
            stack.getItem().getSubItems(CreativeTabs.SEARCH, subItems);

            for(ItemStack subStack : subItems) {
               callback.accept(subStack);
            }
         } else {
            callback.accept(stack);
         }

      }
   }

   private static Pair<Block, Integer> getBlockFromString(String string) {
      if (!isValidItemStack(string, false)) {
         log.warn("Invalid block string {}", string);
         return null;
      } else {
         String[] parts = string.split(":");
         Block block = (Block)GameRegistry.findRegistry(Block.class).getValue(new ResourceLocation(parts[0], parts[1]));
         if (block != null && block != Blocks.AIR) {
            Integer meta = null;
            if (parts.length > 2) {
               meta = Integer.parseInt(parts[2]);
               if (meta.intValue() == -1) {
                  meta = null;
               }
            }

            return Pair.of(block, meta);
         } else {
            log.debug("Failed to find block {}", string);
            return null;
         }
      }
   }

   public static IBlockState getBlockStateFromString(String string) {
      Pair<Block, Integer> pair = getBlockFromString(string);
      if (pair == null) {
         return null;
      } else {
         return pair.getRight() == null ? ((Block)pair.getLeft()).getDefaultState() : ((Block)pair.getLeft()).getStateFromMeta(((Integer)pair.getRight()).intValue());
      }
   }

   public static void forBlockInString(String string, Consumer<IBlockState> stateConsumer, Consumer<Block> blockConsumer) {
      Pair<Block, Integer> pair = getBlockFromString(string);
      if (pair != null) {
         if (pair.getRight() == null) {
            if (blockConsumer == null) {
               UnmodifiableIterator var4 = ((Block)pair.getLeft()).getBlockState().getValidStates().iterator();

               while(var4.hasNext()) {
                  IBlockState state = (IBlockState)var4.next();
                  stateConsumer.accept(state);
               }
            } else {
               blockConsumer.accept(pair.getLeft());
            }
         } else {
            stateConsumer.accept(((Block)pair.getLeft()).getStateFromMeta(((Integer)pair.getRight()).intValue()));
         }

      }
   }
}

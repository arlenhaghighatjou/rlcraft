package slimeknights.mantle.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableList.Builder;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

public abstract class RecipeMatch {
   public final int amountNeeded;
   public final int amountMatched;

   public RecipeMatch(int amountMatched, int amountNeeded) {
      this.amountMatched = amountMatched;
      this.amountNeeded = amountNeeded;
   }

   public abstract List<ItemStack> getInputs();

   public abstract Optional<RecipeMatch.Match> matches(NonNullList<ItemStack> var1);

   public static RecipeMatch of(String oredict) {
      return of(oredict, 1);
   }

   public static RecipeMatch of(String oredict, int matched) {
      return of(oredict, 1, matched);
   }

   public static RecipeMatch of(String oredict, int amount, int matched) {
      return new RecipeMatch.Oredict(oredict, amount, matched);
   }

   public static RecipeMatch of(List<ItemStack> oredict) {
      return of(oredict, 1);
   }

   public static RecipeMatch of(List<ItemStack> oredict, int matched) {
      return of(oredict, 1, matched);
   }

   public static RecipeMatch of(List<ItemStack> oredict, int amount, int matched) {
      return new RecipeMatch.Oredict(oredict, amount, matched);
   }

   public static RecipeMatch of(net.minecraft.item.Item item) {
      return of(item, 1);
   }

   public static RecipeMatch of(net.minecraft.item.Item item, int matched) {
      return of(item, 1, matched);
   }

   public static RecipeMatch of(net.minecraft.item.Item item, int amount, int matched) {
      return new RecipeMatch.Item(new ItemStack(item), amount, matched);
   }

   public static RecipeMatch of(Block block) {
      return of(block, 1);
   }

   public static RecipeMatch of(Block block, int matched) {
      return of(block, 1, matched);
   }

   public static RecipeMatch of(Block block, int amount, int matched) {
      return new RecipeMatch.Item(new ItemStack(block), amount, matched);
   }

   public static RecipeMatch of(ItemStack stack) {
      return of(stack, 1);
   }

   public static RecipeMatch of(ItemStack stack, int matched) {
      return of(stack, 1, matched);
   }

   public static RecipeMatch of(ItemStack stack, int amount, int matched) {
      return new RecipeMatch.Item(stack.copy(), amount, matched);
   }

   public static RecipeMatch ofNBT(ItemStack stack) {
      return ofNBT(stack, 1);
   }

   public static RecipeMatch ofNBT(ItemStack stack, int matched) {
      return new RecipeMatch.ItemCombination(matched, new ItemStack[]{stack});
   }

   public static void removeMatch(NonNullList<ItemStack> stacks, RecipeMatch.Match match) {
      int[] amountsRemoved = new int[match.stacks.size()];
      removeOrder(stacks, match.stacks, amountsRemoved);
      removeRemaining(stacks, match.stacks, amountsRemoved);
   }

   private static void removeOrder(NonNullList<ItemStack> stacks, List<ItemStack> toRemove, int[] amountsRemoved) {
      int i = 0;

      for(int j = 0; j < amountsRemoved.length; ++j) {
         for(ItemStack stack = (ItemStack)toRemove.get(j); i < stacks.size(); ++i) {
            if (ItemStack.areItemsEqual(stack, (ItemStack)stacks.get(i)) && ItemStack.areItemStackTagsEqual(stack, (ItemStack)stacks.get(i))) {
               if (((ItemStack)stacks.get(i)).getCount() >= stack.getCount()) {
                  ((ItemStack)stacks.get(i)).shrink(stack.getCount());
                  if (((ItemStack)stacks.get(i)).getCount() == 0) {
                     stacks.set(i, ItemStack.EMPTY);
                  }

                  amountsRemoved[j] += stack.getCount();
               }

               ++i;
               break;
            }
         }
      }

   }

   private static void removeRemaining(NonNullList<ItemStack> stacks, List<ItemStack> toRemove, int[] amountsRemoved) {
      for(int j = 0; j < amountsRemoved.length; ++j) {
         ItemStack stack = (ItemStack)toRemove.get(j);
         int needed = stack.getCount() - amountsRemoved[j];

         for(int i = 0; i < stacks.size() && needed > 0; ++i) {
            if (ItemStack.areItemsEqual(stack, (ItemStack)stacks.get(i)) && ItemStack.areItemStackTagsEqual(stack, (ItemStack)stacks.get(i))) {
               int change = Math.min(((ItemStack)stacks.get(i)).getCount(), needed);
               ((ItemStack)stacks.get(i)).shrink(change);
               if (((ItemStack)stacks.get(i)).getCount() == 0) {
                  stacks.set(i, ItemStack.EMPTY);
               }

               needed -= change;
            }
         }
      }

   }

   public static class Item extends RecipeMatch {
      private final ItemStack template;

      public Item(ItemStack template, int amountNeeded) {
         this(template, amountNeeded, 1);
      }

      public Item(ItemStack template, int amountNeeded, int amountMatched) {
         super(amountMatched, amountNeeded);
         this.template = template;
      }

      public List<ItemStack> getInputs() {
         return ImmutableList.of(this.template);
      }

      public Optional<RecipeMatch.Match> matches(NonNullList<ItemStack> stacks) {
         List<ItemStack> found = Lists.newLinkedList();
         int stillNeeded = this.amountNeeded;

         for(ItemStack stack : stacks) {
            if (OreDictionary.itemMatches(this.template, stack, false)) {
               ItemStack copy = stack.copy();
               copy.setCount(Math.min(copy.getCount(), stillNeeded));
               found.add(copy);
               stillNeeded -= copy.getCount();
               if (stillNeeded <= 0) {
                  return Optional.of(new RecipeMatch.Match(found, this.amountMatched));
               }
            }
         }

         return Optional.empty();
      }
   }

   public static class ItemCombination extends RecipeMatch {
      protected final NonNullList<ItemStack> itemStacks;

      public ItemCombination(int amountMatched, ItemStack... stacks) {
         super(amountMatched, 0);
         NonNullList<ItemStack> nonNullStacks = NonNullList.withSize(stacks.length, ItemStack.EMPTY);

         for(int i = 0; i < stacks.length; ++i) {
            if (!stacks[i].isEmpty()) {
               nonNullStacks.set(i, stacks[i].copy());
            }
         }

         this.itemStacks = nonNullStacks;
      }

      public List<ItemStack> getInputs() {
         return ImmutableList.copyOf(this.itemStacks);
      }

      public Optional<RecipeMatch.Match> matches(NonNullList<ItemStack> stacks) {
         List<ItemStack> found = Lists.newLinkedList();
         Set<Integer> needed = Sets.newHashSet();

         for(int i = 0; i < this.itemStacks.size(); ++i) {
            if (!((ItemStack)this.itemStacks.get(i)).isEmpty()) {
               needed.add(Integer.valueOf(i));
            }
         }

         label36:
         for(ItemStack stack : stacks) {
            Iterator<Integer> iter = needed.iterator();

            while(true) {
               if (!iter.hasNext()) {
                  continue label36;
               }

               int index = ((Integer)iter.next()).intValue();
               ItemStack template = (ItemStack)this.itemStacks.get(index);
               if (ItemStack.areItemsEqual(template, stack) && ItemStack.areItemStackTagsEqual(template, stack)) {
                  break;
               }
            }

            ItemStack copy = stack.copy();
            copy.setCount(1);
            found.add(copy);
            iter.remove();
         }

         if (needed.isEmpty()) {
            return Optional.of(new RecipeMatch.Match(found, this.amountMatched));
         } else {
            return Optional.empty();
         }
      }
   }

   public static class Match {
      public List<ItemStack> stacks;
      public int amount;

      public Match(List<ItemStack> stacks, int amount) {
         this.stacks = stacks;
         this.amount = amount;
      }
   }

   public static class Oredict extends RecipeMatch {
      private final List<ItemStack> oredictEntry;

      public Oredict(List<ItemStack> oredictEntry, int amountNeeded) {
         this(oredictEntry, amountNeeded, 1);
      }

      public Oredict(List<ItemStack> oredictEntry, int amountNeeded, int amountMatched) {
         super(amountMatched, amountNeeded);
         this.oredictEntry = oredictEntry;
      }

      public Oredict(String oredictEntry, int amountNeeded) {
         this(oredictEntry, amountNeeded, 1);
      }

      public Oredict(String oredictEntry, int amountNeeded, int amountMatched) {
         super(amountMatched, amountNeeded);
         this.oredictEntry = OreDictionary.getOres(oredictEntry);
      }

      public List<ItemStack> getInputs() {
         Builder<ItemStack> builder = ImmutableList.builder();
         this.oredictEntry.forEach((stack) -> {
            IntStream.range(0, this.amountNeeded).forEach((i) -> {
               builder.add(stack);
            });
         });
         return builder.build();
      }

      public Optional<RecipeMatch.Match> matches(NonNullList<ItemStack> stacks) {
         List<ItemStack> found = Lists.newLinkedList();
         int stillNeeded = this.amountNeeded;

         for(ItemStack ore : this.oredictEntry) {
            for(ItemStack stack : stacks) {
               if (OreDictionary.itemMatches(ore, stack, false)) {
                  ItemStack copy = stack.copy();
                  copy.setCount(Math.min(copy.getCount(), stillNeeded));
                  found.add(copy);
                  stillNeeded -= copy.getCount();
                  if (stillNeeded <= 0) {
                     return Optional.of(new RecipeMatch.Match(found, this.amountMatched));
                  }
               }
            }
         }

         return Optional.empty();
      }
   }
}

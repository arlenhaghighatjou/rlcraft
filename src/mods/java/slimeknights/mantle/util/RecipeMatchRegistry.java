package slimeknights.mantle.util;

import com.google.common.collect.Lists;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class RecipeMatchRegistry {
   protected final PriorityQueue<RecipeMatch> items = new PriorityQueue(1, RecipeMatchRegistry.RecipeComparator.INSTANCE);

   public RecipeMatchRegistry() {
   }

   public Optional<RecipeMatch.Match> matches(ItemStack... stacks) {
      NonNullList<ItemStack> nonNullStacks = NonNullList.withSize(stacks.length, ItemStack.EMPTY);

      for(int i = 0; i < stacks.length; ++i) {
         if (!stacks[i].isEmpty()) {
            nonNullStacks.set(i, stacks[i].copy());
         }
      }

      return this.matches(nonNullStacks);
   }

   public Optional<RecipeMatch.Match> matches(NonNullList<ItemStack> stacks) {
      for(RecipeMatch recipe : this.items) {
         Optional<RecipeMatch.Match> match = recipe.matches(stacks);
         if (match.isPresent()) {
            return match;
         }
      }

      return Optional.empty();
   }

   public Optional<RecipeMatch.Match> matches(NonNullList<ItemStack> stacks, int minAmount) {
      stacks = copyItemStackArray(stacks);
      List<RecipeMatch.Match> matches = Lists.newLinkedList();

      int sum;
      Optional<RecipeMatch.Match> matchOptional;
      RecipeMatch.Match match;
      for(sum = 0; sum < minAmount && (matchOptional = this.matches(stacks)).isPresent(); sum += match.amount) {
         match = (RecipeMatch.Match)matchOptional.get();
         matches.add(match);
         RecipeMatch.removeMatch(stacks, match);
      }

      if (sum < minAmount) {
         return Optional.empty();
      } else {
         match = Lists.newLinkedList();

         for(RecipeMatch.Match m : matches) {
            match.addAll(m.stacks);
         }

         return Optional.of(new RecipeMatch.Match(match, sum));
      }
   }

   public Optional<RecipeMatch.Match> matchesRecursively(NonNullList<ItemStack> stacks) {
      stacks = copyItemStackArray(stacks);
      List<RecipeMatch.Match> matches = Lists.newLinkedList();

      int sum;
      Optional<RecipeMatch.Match> matchOptional;
      RecipeMatch.Match match;
      for(sum = 0; (matchOptional = this.matches(stacks)).isPresent(); sum += match.amount) {
         match = (RecipeMatch.Match)matchOptional.get();
         matches.add(match);
         RecipeMatch.removeMatch(stacks, match);
      }

      match = Lists.newLinkedList();

      for(RecipeMatch.Match m : matches) {
         match.addAll(m.stacks);
      }

      return Optional.of(new RecipeMatch.Match(match, sum));
   }

   public void addItem(String oredictItem, int amountNeeded, int amountMatched) {
      this.items.add(new RecipeMatch.Oredict(oredictItem, amountNeeded, amountMatched));
   }

   public void addItem(String oredictItem) {
      this.addItem(oredictItem, 1, 1);
   }

   public void addItem(Block block, int amountMatched) {
      this.items.add(new RecipeMatch.Item(new ItemStack(block), 1, amountMatched));
   }

   public void addItem(Item item, int amountNeeded, int amountMatched) {
      this.items.add(new RecipeMatch.Item(new ItemStack(item), amountNeeded, amountMatched));
   }

   public void addItem(ItemStack item, int amountNeeded, int amountMatched) {
      this.items.add(new RecipeMatch.Item(item, amountNeeded, amountMatched));
   }

   public void addItem(Item item) {
      this.addItem(item, 1, 1);
   }

   public void addRecipeMatch(RecipeMatch match) {
      this.items.add(match);
   }

   public static NonNullList<ItemStack> copyItemStackArray(NonNullList<ItemStack> in) {
      NonNullList<ItemStack> stacksCopy = NonNullList.withSize(in.size(), ItemStack.EMPTY);

      for(int i = 0; i < in.size(); ++i) {
         if (!((ItemStack)in.get(i)).isEmpty()) {
            stacksCopy.set(i, ((ItemStack)in.get(i)).copy());
         }
      }

      return stacksCopy;
   }

   private static class RecipeComparator implements Comparator<RecipeMatch> {
      public static RecipeMatchRegistry.RecipeComparator INSTANCE = new RecipeMatchRegistry.RecipeComparator();

      private RecipeComparator() {
      }

      public int compare(RecipeMatch o1, RecipeMatch o2) {
         return o2.amountMatched - o1.amountMatched;
      }
   }
}

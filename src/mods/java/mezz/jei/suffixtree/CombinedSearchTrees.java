package mezz.jei.suffixtree;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.ArrayList;
import java.util.List;

public class CombinedSearchTrees implements ISearchTree {
   private final List<ISearchTree> searchTrees = new ArrayList();

   public CombinedSearchTrees() {
   }

   public IntSet search(String word) {
      IntSet searchResults = new IntOpenHashSet(0);

      for(ISearchTree searchTree : this.searchTrees) {
         IntSet search = searchTree.search(word);
         searchResults = union(searchResults, search);
      }

      return searchResults;
   }

   public void addSearchTree(ISearchTree searchTree) {
      this.searchTrees.add(searchTree);
   }

   private static IntSet union(IntSet set1, IntSet set2) {
      if (set1.size() > set2.size()) {
         set1.addAll(set2);
         return set1;
      } else {
         set2.addAll(set1);
         return set2;
      }
   }
}

package mezz.jei.ingredients;

import java.util.Collection;
import mezz.jei.config.Config;
import mezz.jei.gui.ingredients.IIngredientListElement;
import mezz.jei.suffixtree.GeneralizedSuffixTree;

class PrefixedSearchTree {
   private final GeneralizedSuffixTree tree;
   private final PrefixedSearchTree.IStringsGetter stringsGetter;
   private final PrefixedSearchTree.IModeGetter modeGetter;

   public PrefixedSearchTree(GeneralizedSuffixTree tree, PrefixedSearchTree.IStringsGetter stringsGetter, PrefixedSearchTree.IModeGetter modeGetter) {
      this.tree = tree;
      this.stringsGetter = stringsGetter;
      this.modeGetter = modeGetter;
   }

   public GeneralizedSuffixTree getTree() {
      return this.tree;
   }

   public PrefixedSearchTree.IStringsGetter getStringsGetter() {
      return this.stringsGetter;
   }

   public Config.SearchMode getMode() {
      return this.modeGetter.getMode();
   }

   @FunctionalInterface
   interface IModeGetter {
      Config.SearchMode getMode();
   }

   @FunctionalInterface
   interface IStringsGetter {
      Collection<String> getStrings(IIngredientListElement<?> var1);
   }
}

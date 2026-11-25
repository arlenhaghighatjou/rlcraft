package invtweaks.api;

import java.util.Collection;
import java.util.List;

public interface IItemTreeCategory {
   boolean contains(IItemTreeItem var1);

   void addCategory(IItemTreeCategory var1);

   void addItem(IItemTreeItem var1);

   Collection<IItemTreeCategory> getSubCategories();

   Collection<List<IItemTreeItem>> getItems();

   String getName();

   int getCategoryOrder();

   int findCategoryOrder(String var1);

   int findKeywordDepth(String var1);

   String findKeywordPath(String var1);
}

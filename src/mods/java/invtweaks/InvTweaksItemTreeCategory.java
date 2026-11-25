package invtweaks;

import invtweaks.api.IItemTreeCategory;
import invtweaks.api.IItemTreeItem;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class InvTweaksItemTreeCategory implements IItemTreeCategory {
   private final Map<String, List<IItemTreeItem>> items = new HashMap();
   private final List<String> matchingItems = new ArrayList();
   private final List<IItemTreeCategory> subCategories = new ArrayList();
   private String name;
   private int order = -1;

   public InvTweaksItemTreeCategory(String name_) {
      this.name = name_;
   }

   public boolean contains(@NotNull IItemTreeItem item) {
      List<IItemTreeItem> storedItems = (List)this.items.get(item.getId());
      if (storedItems != null) {
         for(IItemTreeItem storedItem : storedItems) {
            if (storedItem.equals(item)) {
               return true;
            }
         }
      }

      for(IItemTreeCategory category : this.subCategories) {
         if (category.contains(item)) {
            return true;
         }
      }

      return false;
   }

   public void addCategory(IItemTreeCategory category) {
      this.subCategories.add(category);
   }

   public void addItem(@NotNull IItemTreeItem item) {
      if (this.items.get(item.getId()) == null) {
         List<IItemTreeItem> itemList = new ArrayList();
         itemList.add(item);
         this.items.put(item.getId(), itemList);
      } else {
         ((List)this.items.get(item.getId())).add(item);
      }

      this.matchingItems.add(item.getName());
      if (this.order == -1 || this.order > item.getOrder()) {
         this.order = item.getOrder();
      }

   }

   public int getCategoryOrder() {
      if (this.order != -1) {
         return this.order;
      } else {
         for(IItemTreeCategory category : this.subCategories) {
            int catOrder = category.getCategoryOrder();
            if (catOrder != -1) {
               return catOrder;
            }
         }

         return -1;
      }
   }

   public int findCategoryOrder(@NotNull String keyword) {
      if (keyword.equals(this.name)) {
         return this.getCategoryOrder();
      } else {
         for(IItemTreeCategory category : this.subCategories) {
            int result = category.findCategoryOrder(keyword);
            if (result != -1) {
               return result;
            }
         }

         return -1;
      }
   }

   public int findKeywordDepth(String keyword) {
      if (this.name.equals(keyword)) {
         return 0;
      } else if (this.matchingItems.contains(keyword)) {
         return 1;
      } else {
         for(IItemTreeCategory category : this.subCategories) {
            int result = category.findKeywordDepth(keyword);
            if (result != -1) {
               return result + 1;
            }
         }

         return -1;
      }
   }

   public String findKeywordPath(String keyword) {
      if (this.name.equals(keyword)) {
         return keyword;
      } else if (this.matchingItems.contains(keyword)) {
         return this.name + "\\" + keyword;
      } else {
         for(IItemTreeCategory category : this.subCategories) {
            String result = category.findKeywordPath(keyword);
            if (result.length() > 0) {
               return this.name + "\\" + result;
            }
         }

         return "";
      }
   }

   @NotNull
   public Collection<IItemTreeCategory> getSubCategories() {
      return this.subCategories;
   }

   @NotNull
   public Collection<List<IItemTreeItem>> getItems() {
      return this.items.values();
   }

   public String getName() {
      return this.name;
   }

   @NotNull
   public String toString() {
      return this.name + " (" + this.subCategories.size() + " cats, " + this.items.size() + " items)";
   }
}

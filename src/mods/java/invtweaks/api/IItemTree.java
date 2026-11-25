package invtweaks.api;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;

public interface IItemTree {
   void registerOre(String var1, String var2, String var3, int var4, String var5);

   boolean matches(List<IItemTreeItem> var1, String var2);

   boolean isKeywordValid(String var1);

   Collection<IItemTreeCategory> getAllCategories();

   IItemTreeCategory getRootCategory();

   void setRootCategory(IItemTreeCategory var1);

   IItemTreeCategory getCategory(String var1);

   boolean isItemUnknown(String var1, int var2);

   List<IItemTreeItem> getItems(String var1, int var2, NBTTagCompound var3);

   List<IItemTreeItem> getItems(String var1, int var2);

   List<IItemTreeItem> getItems(String var1);

   IItemTreeItem getRandomItem(Random var1);

   boolean containsItem(String var1);

   boolean containsCategory(String var1);

   IItemTreeCategory addCategory(String var1, String var2) throws NullPointerException;

   void addCategory(String var1, IItemTreeCategory var2) throws NullPointerException;

   IItemTreeItem addItem(String var1, String var2, String var3, int var4, int var5) throws NullPointerException;

   IItemTreeItem addItem(String var1, String var2, String var3, int var4, NBTTagCompound var5, int var6) throws NullPointerException;

   void addItem(String var1, IItemTreeItem var2) throws NullPointerException;

   int getKeywordDepth(String var1);

   int getKeywordOrder(String var1);
}

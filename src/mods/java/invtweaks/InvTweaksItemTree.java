package invtweaks;

import invtweaks.api.IItemTree;
import invtweaks.api.IItemTreeCategory;
import invtweaks.api.IItemTreeItem;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreDictionary.OreRegisterEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InvTweaksItemTree implements IItemTree {
   public static final String UNKNOWN_ITEM = "unknown";
   private static final Logger log = InvTweaks.log;
   @Nullable
   private static List<IItemTreeItem> defaultItems = null;
   @NotNull
   private Map<String, IItemTreeCategory> categories = new HashMap();
   @NotNull
   private Map<String, List<IItemTreeItem>> itemsById = new HashMap(500);
   @NotNull
   private Map<String, List<IItemTreeItem>> itemsByName = new HashMap(500);
   private String rootCategory;
   @NotNull
   private List<InvTweaksItemTree.OreDictInfo> oresRegistered = new ArrayList();
   @NotNull
   private List<ItemStack> allGameItems = new ArrayList();
   private int highestOrder = 0;
   private int lastTreeOrder = 0;

   public InvTweaksItemTree() {
      this.reset();
   }

   public void reset() {
      if (defaultItems == null) {
         defaultItems = new ArrayList();
         defaultItems.add(new InvTweaksItemTreeItem("unknown", (String)null, 32767, (NBTTagCompound)null, Integer.MAX_VALUE, this.getRootCategory().getName()));
      }

      this.categories.clear();
      this.itemsByName.clear();
      this.itemsById.clear();
   }

   public boolean matches(@Nullable List<IItemTreeItem> items, @NotNull String keyword) {
      if (items == null) {
         return false;
      } else {
         for(IItemTreeItem item : items) {
            if (item.getName() != null && item.getName().equals(keyword)) {
               return true;
            }
         }

         IItemTreeCategory category = this.getCategory(keyword);
         if (category != null) {
            for(IItemTreeItem item : items) {
               if (category.contains(item)) {
                  return true;
               }
            }
         }

         return keyword.equals(this.rootCategory);
      }
   }

   public int getKeywordDepth(String keyword) {
      try {
         return this.getRootCategory().findKeywordDepth(keyword);
      } catch (NullPointerException var3) {
         log.error("The root category is missing: " + var3.getMessage());
         return 0;
      }
   }

   public int getKeywordOrder(String keyword) {
      List<IItemTreeItem> items = this.getItems(keyword);
      if (items != null && items.size() != 0) {
         return ((IItemTreeItem)items.get(0)).getOrder();
      } else {
         try {
            return this.getRootCategory().findCategoryOrder(keyword);
         } catch (NullPointerException var4) {
            log.error("The root category is missing: " + var4.getMessage());
            return -1;
         }
      }
   }

   public boolean isKeywordValid(String keyword) {
      if (this.containsItem(keyword)) {
         return true;
      } else {
         IItemTreeCategory category = this.getCategory(keyword);
         return category != null;
      }
   }

   @NotNull
   public Collection<IItemTreeCategory> getAllCategories() {
      return this.categories.values();
   }

   public IItemTreeCategory getRootCategory() {
      return (IItemTreeCategory)this.categories.get(this.rootCategory);
   }

   public void setRootCategory(@NotNull IItemTreeCategory category) {
      this.rootCategory = category.getName();
      this.categories.put(this.rootCategory, category);
   }

   public IItemTreeCategory getCategory(String keyword) {
      return (IItemTreeCategory)this.categories.get(keyword);
   }

   public boolean isItemUnknown(String id, int damage) {
      return this.itemsById.get(id) == null;
   }

   @NotNull
   public List<IItemTreeItem> getItems(@Nullable String id, int damage, @Nullable NBTTagCompound extra) {
      if (id == null) {
         return new ArrayList();
      } else {
         List<IItemTreeItem> items = (List)this.itemsById.get(id);
         List<IItemTreeItem> filteredItems = new ArrayList();
         if (items != null) {
            filteredItems.addAll(items);
         }

         if (items != null && !items.isEmpty()) {
            items.stream().filter((item) -> {
               return item.getDamage() != 32767 && item.getDamage() != damage;
            }).forEach(filteredItems::remove);
         }

         items = filteredItems;
         filteredItems = new ArrayList(filteredItems);
         if (extra != null && !items.isEmpty()) {
            items.stream().filter((item) -> {
               return !NBTUtil.areNBTEquals(item.getExtraData(), extra, true);
            }).forEach(filteredItems::remove);
         }

         if (filteredItems.isEmpty()) {
            int newItemOrder = this.highestOrder + 1;
            IItemTreeItem newItemId = new InvTweaksItemTreeItem(String.format("%s-%d", id, damage), id, damage, (NBTTagCompound)null, newItemOrder, this.getRootCategory().getName() + "\\_uncategorized\\" + String.format("%s-%d", id, damage));
            IItemTreeItem newItemDamage = new InvTweaksItemTreeItem(id, id, 32767, (NBTTagCompound)null, newItemOrder, this.getRootCategory().getName() + "\\_uncategorized\\" + id);
            this.addItem(this.getRootCategory().getName(), newItemId);
            this.addItem(this.getRootCategory().getName(), newItemDamage);
            filteredItems.add(newItemId);
            filteredItems.add(newItemDamage);
         }

         filteredItems.removeIf(Objects::isNull);
         return filteredItems;
      }
   }

   @NotNull
   public List<IItemTreeItem> getItems(String id, int damage) {
      return this.getItems(id, damage, (NBTTagCompound)null);
   }

   public List<IItemTreeItem> getItems(String name) {
      return (List)this.itemsByName.get(name);
   }

   @NotNull
   public IItemTreeItem getRandomItem(@NotNull EaglercraftRandom r) {
      return (IItemTreeItem)this.itemsByName.values().toArray()[r.nextInt(this.itemsByName.size())];
   }

   public boolean containsItem(String name) {
      return this.itemsByName.containsKey(name);
   }

   public boolean containsCategory(String name) {
      return this.categories.containsKey(name);
   }

   @NotNull
   public IItemTreeCategory addCategory(String parentCategory, String newCategory) throws NullPointerException {
      IItemTreeCategory addedCategory = new InvTweaksItemTreeCategory(newCategory);
      this.addCategory(parentCategory, addedCategory);
      return addedCategory;
   }

   @NotNull
   public IItemTreeItem addItem(String parentCategory, String name, String id, int damage, int order) throws NullPointerException {
      return this.addItem(parentCategory, name, id, damage, (NBTTagCompound)null, order);
   }

   @NotNull
   public IItemTreeItem addItem(String parentCategory, String name, String id, int damage, NBTTagCompound extra, int order) throws NullPointerException {
      InvTweaksItemTreeItem addedItem = new InvTweaksItemTreeItem(name, id, damage, extra, order, this.getRootCategory().findKeywordPath(parentCategory) + "\\" + name);
      this.addItem(parentCategory, addedItem);
      return addedItem;
   }

   public void addCategory(String parentCategory, @NotNull IItemTreeCategory newCategory) throws NullPointerException {
      ((IItemTreeCategory)this.categories.get(parentCategory)).addCategory(newCategory);
      this.categories.put(newCategory.getName(), newCategory);
   }

   public void addItem(String parentCategory, @NotNull IItemTreeItem newItem) throws NullPointerException {
      this.highestOrder = Math.max(this.highestOrder, newItem.getOrder());
      ((IItemTreeCategory)this.categories.get(parentCategory)).addItem(newItem);
      if (this.itemsByName.containsKey(newItem.getName())) {
         ((List)this.itemsByName.get(newItem.getName())).add(newItem);
      } else {
         List<IItemTreeItem> list = new ArrayList();
         list.add(newItem);
         this.itemsByName.put(newItem.getName(), list);
      }

      if (this.itemsById.containsKey(newItem.getId())) {
         ((List)this.itemsById.get(newItem.getId())).add(newItem);
      } else {
         List<IItemTreeItem> list = new ArrayList();
         list.add(newItem);
         this.itemsById.put(newItem.getId(), list);
      }

   }

   public int getHighestOrder() {
      return this.highestOrder;
   }

   public int getLastTreeOrder() {
      return this.lastTreeOrder;
   }

   public void registerOre(String category, String name, String oreName, int order, String path) {
      for(ItemStack i : OreDictionary.getOres(oreName, false)) {
         if (i != null) {
            this.addItem(category, new InvTweaksItemTreeItem(name, i.getItem().getRegistryName().toString(), i.getItemDamage(), (NBTTagCompound)null, order, path));
         } else {
            log.warn(String.format("An OreDictionary entry for %s is null", oreName));
         }
      }

      this.oresRegistered.add(new InvTweaksItemTree.OreDictInfo(category, name, oreName, order, path));
   }

   @SubscribeEvent
   public void oreRegistered(@NotNull OreRegisterEvent ev) {
      this.oresRegistered.stream().filter((ore) -> {
         return ore.oreName.equals(ev.getName());
      }).forEach((ore) -> {
         ItemStack evOre = ev.getOre();
         if (!evOre.isEmpty()) {
            this.addItem(ore.category, new InvTweaksItemTreeItem(ore.name, evOre.getItem().getRegistryName().toString(), evOre.getItemDamage(), (NBTTagCompound)null, ore.order, ore.orePath));
         } else {
            log.warn(String.format("An OreDictionary entry for %s is null", ev.getName()));
         }

      });
   }

   public void registerClass(String category, String name, String className, NBTTagCompound extraData, int order, String path) {
      if (this.allGameItems.size() == 0) {
         this.populateGameItems();
      }

      for(ItemStack stack : this.allGameItems) {
         Item item = stack.getItem();
         boolean isClass = this.InstanceOfClassNameKind(item, className);
         if (isClass) {
            boolean doIt = true;
            if (extraData != null) {
               if (doIt && extraData.hasKey("toolclass")) {
                  String tclass = extraData.getString("toolclass");
                  doIt = tclass.equals(InvTweaks.getToolClass(stack, item));
               }

               if (doIt && extraData.hasKey("armortype") && item instanceof ItemArmor) {
                  ItemArmor armor = (ItemArmor)item;
                  String keyArmorType = extraData.getString("armortype");
                  String itemArmorType = armor.armorType.getName().toLowerCase();
                  doIt = keyArmorType.equals(itemArmorType);
                  armor = null;
               }

               if (doIt && extraData.hasKey("isshield")) {
                  doIt = item.isShield(stack, (EntityLivingBase)null);
               }
            }

            if (doIt) {
               int dmg = item.isDamageable() ? 32767 : stack.getItemDamage();
               this.addItem(category, new InvTweaksItemTreeItem(name, item.getRegistryName().toString(), dmg, (NBTTagCompound)null, order, path));
            }
         }
      }

   }

   private void populateGameItems() {
      for(Entry<ResourceLocation, Item> entry : ForgeRegistries.ITEMS.getEntries()) {
         Item item = (Item)entry.getValue();
         if (item.getHasSubtypes()) {
            for(CreativeTabs tab : item.getCreativeTabs()) {
               if (tab != null) {
                  NonNullList<ItemStack> stacks = NonNullList.create();
                  item.getSubItems(tab, stacks);

                  for(ItemStack stack : stacks) {
                     this.allGameItems.add(stack);
                  }
               }
            }
         } else {
            this.allGameItems.add(item.getDefaultInstance());
         }
      }

   }

   private boolean InstanceOfClassNameKind(Object o, String className) {
      for(Class testClass = o.getClass(); testClass != null; testClass = testClass.getSuperclass()) {
         if (testClass.getName().toLowerCase().endsWith(className)) {
            return true;
         }
      }

      return false;
   }

   public void endFileRead() {
      this.allGameItems.clear();
      this.lastTreeOrder = this.highestOrder;
   }

   private static class OreDictInfo {
      String category;
      String name;
      String oreName;
      int order;
      String orePath;

      OreDictInfo(String category_, String name_, String oreName_, int order_, String orePath_) {
         this.category = category_;
         this.name = name_;
         this.oreName = oreName_;
         this.order = order_;
         this.orePath = orePath_;
      }
   }
}

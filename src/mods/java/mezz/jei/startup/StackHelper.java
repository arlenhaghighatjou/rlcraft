package mezz.jei.startup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.recipe.IStackHelper;
import mezz.jei.util.ErrorUtil;
import mezz.jei.util.Log;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

public class StackHelper implements IStackHelper {
   private final ISubtypeRegistry subtypeRegistry;
   private final Map<StackHelper.UidMode, Map<ItemStack, String>> uidCache = new EnumMap(StackHelper.UidMode.class);
   private boolean uidCacheEnabled = true;

   public StackHelper(ISubtypeRegistry subtypeRegistry) {
      this.subtypeRegistry = subtypeRegistry;

      for(StackHelper.UidMode mode : StackHelper.UidMode.values()) {
         this.uidCache.put(mode, new IdentityHashMap());
      }

   }

   public void enableUidCache() {
      this.uidCacheEnabled = true;
   }

   public void disableUidCache() {
      for(StackHelper.UidMode mode : StackHelper.UidMode.values()) {
         this.uidCache.put(mode, new IdentityHashMap());
      }

      this.uidCacheEnabled = false;
   }

   @Nullable
   public String getOreDictEquivalent(Collection<ItemStack> itemStacks) {
      if (itemStacks.size() < 2) {
         return null;
      } else {
         ItemStack firstStack = (ItemStack)itemStacks.iterator().next();
         if (firstStack != null) {
            for(int oreId : OreDictionary.getOreIDs(firstStack)) {
               String oreName = OreDictionary.getOreName(oreId);
               List<ItemStack> ores = OreDictionary.getOres(oreName);
               ores = this.getAllSubtypes(ores);
               if (this.containsSameStacks(itemStacks, ores)) {
                  return oreName;
               }
            }
         }

         return null;
      }
   }

   public StackHelper.MatchingItemsResult getMatchingItems(Map<Integer, ItemStack> availableItemStacks, Map<Integer, ? extends IGuiIngredient<ItemStack>> ingredientsMap) {
      StackHelper.MatchingItemsResult matchingItemResult = new StackHelper.MatchingItemsResult();
      int recipeSlotNumber = -1;

      for(Integer key : new TreeSet(ingredientsMap.keySet())) {
         IGuiIngredient<ItemStack> ingredient = (IGuiIngredient)ingredientsMap.get(key);
         if (ingredient.isInput()) {
            ++recipeSlotNumber;
            List<ItemStack> requiredStacks = ingredient.getAllIngredients();
            if (!requiredStacks.isEmpty()) {
               Integer matching = this.containsAnyStackIndexed(availableItemStacks, requiredStacks);
               if (matching == null) {
                  matchingItemResult.missingItems.add(key);
               } else {
                  ItemStack matchingStack = (ItemStack)availableItemStacks.get(matching);
                  matchingStack.shrink(1);
                  if (matchingStack.getCount() == 0) {
                     availableItemStacks.remove(matching);
                  }

                  matchingItemResult.matchingItems.put(Integer.valueOf(recipeSlotNumber), matching);
               }
            }
         }
      }

      return matchingItemResult;
   }

   public boolean containsSameStacks(Collection<ItemStack> stacks, Collection<ItemStack> contains) {
      return this.containsSameStacks(new StackHelper.MatchingIterable(stacks), new StackHelper.MatchingIterable(contains));
   }

   public <R> boolean containsSameStacks(Iterable<StackHelper.ItemStackMatchable<R>> stacks, Iterable<StackHelper.ItemStackMatchable<R>> contains) {
      for(StackHelper.ItemStackMatchable stack : contains) {
         if (this.containsStack(stacks, stack) == null) {
            return false;
         }
      }

      for(StackHelper.ItemStackMatchable stack : stacks) {
         if (this.containsStack(contains, stack) == null) {
            return false;
         }
      }

      return true;
   }

   @Nullable
   public Integer containsAnyStackIndexed(Map<Integer, ItemStack> stacks, Iterable<ItemStack> contains) {
      StackHelper.MatchingIndexed matchingStacks = new StackHelper.MatchingIndexed(stacks);
      StackHelper.MatchingIterable matchingContains = new StackHelper.MatchingIterable(contains);
      return (Integer)this.containsStackMatchable(matchingStacks, matchingContains);
   }

   @Nullable
   public ItemStack containsStack(Iterable<ItemStack> stacks, ItemStack contains) {
      List<ItemStack> containsList = Collections.singletonList(contains);
      return this.containsAnyStack(stacks, containsList);
   }

   @Nullable
   public ItemStack containsAnyStack(Iterable<ItemStack> stacks, Iterable<ItemStack> contains) {
      ErrorUtil.checkNotNull(stacks, "stacks");
      ErrorUtil.checkNotNull(contains, "contains");
      StackHelper.MatchingIterable matchingStacks = new StackHelper.MatchingIterable(stacks);
      StackHelper.MatchingIterable matchingContains = new StackHelper.MatchingIterable(contains);
      return (ItemStack)this.containsStackMatchable(matchingStacks, matchingContains);
   }

   @Nullable
   public <R, T> R containsStackMatchable(Iterable<StackHelper.ItemStackMatchable<R>> stacks, Iterable<StackHelper.ItemStackMatchable<T>> contains) {
      for(StackHelper.ItemStackMatchable<?> containStack : contains) {
         R matchingStack = (R)this.containsStack(stacks, containStack);
         if (matchingStack != null) {
            return matchingStack;
         }
      }

      return (R)null;
   }

   @Nullable
   public <R> R containsStack(Iterable<StackHelper.ItemStackMatchable<R>> stacks, StackHelper.ItemStackMatchable<?> contains) {
      for(StackHelper.ItemStackMatchable<R> stack : stacks) {
         if (this.isEquivalent(contains.getStack(), stack.getStack())) {
            return stack.getResult();
         }
      }

      return (R)null;
   }

   public boolean isEquivalent(@Nullable ItemStack lhs, @Nullable ItemStack rhs) {
      if (lhs == rhs) {
         return true;
      } else if (lhs != null && rhs != null) {
         if (lhs.getItem() != rhs.getItem()) {
            return false;
         } else {
            String keyLhs = this.getUniqueIdentifierForStack(lhs, StackHelper.UidMode.NORMAL);
            String keyRhs = this.getUniqueIdentifierForStack(rhs, StackHelper.UidMode.NORMAL);
            return keyLhs.equals(keyRhs);
         }
      } else {
         return false;
      }
   }

   public List<ItemStack> getMatchingStacks(Ingredient ingredient) {
      if (ingredient == Ingredient.EMPTY) {
         return Collections.emptyList();
      } else {
         ItemStack[] matchingStacks = ingredient.getMatchingStacks();
         if (matchingStacks == null) {
            return Collections.emptyList();
         } else {
            return matchingStacks.length > 0 ? Arrays.asList(matchingStacks) : this.getAllSubtypes(Arrays.asList(ingredient.matchingStacks));
         }
      }
   }

   public List<ItemStack> getSubtypes(@Nullable ItemStack itemStack) {
      if (itemStack != null && !itemStack.isEmpty()) {
         if (itemStack.getItemDamage() != 32767) {
            return Collections.singletonList(itemStack);
         } else {
            NonNullList<ItemStack> subtypes = NonNullList.create();
            this.addSubtypesToList(subtypes, itemStack);
            return subtypes;
         }
      } else {
         return Collections.emptyList();
      }
   }

   private void addSubtypesToList(List<ItemStack> subtypeList, ItemStack itemStack) {
      Item item = itemStack.getItem();
      int stackSize = itemStack.getCount();

      for(CreativeTabs itemTab : item.getCreativeTabs()) {
         if (itemTab == null) {
            ItemStack copy = itemStack.copy();
            copy.setItemDamage(0);
            subtypeList.add(copy);
         } else {
            this.addSubtypesFromCreativeTabToList(subtypeList, item, stackSize, itemTab);
         }
      }

   }

   public void addSubtypesToList(List<ItemStack> subtypeList, Item item) {
      for(CreativeTabs itemTab : item.getCreativeTabs()) {
         if (itemTab == null) {
            subtypeList.add(new ItemStack(item, 1));
         } else {
            this.addSubtypesFromCreativeTabToList(subtypeList, item, 1, itemTab);
         }
      }

   }

   private void addSubtypesFromCreativeTabToList(List<ItemStack> subtypeList, Item item, int stackSize, CreativeTabs itemTab) {
      NonNullList<ItemStack> subItems = NonNullList.create();

      try {
         item.getSubItems(itemTab, subItems);
      } catch (LinkageError | RuntimeException var9) {
         Log.get().warn("Caught a crash while getting sub-items of {}", item, var9);
      }

      for(ItemStack subItem : subItems) {
         if (subItem.isEmpty()) {
            Log.get().warn("Found an empty subItem of {}", item);
         } else if (subItem.getMetadata() == 32767) {
            String itemStackInfo = ErrorUtil.getItemStackInfo(subItem);
            Log.get().error("Found an subItem of {} with wildcard metadata: {}", item, itemStackInfo);
         } else if (subItem.getCount() != stackSize) {
            ItemStack subItemCopy = subItem.copy();
            subItemCopy.setCount(stackSize);
            subtypeList.add(subItemCopy);
         } else {
            subtypeList.add(subItem);
         }
      }

   }

   public List<ItemStack> getAllSubtypes(@Nullable Iterable stacks) {
      if (stacks == null) {
         return Collections.emptyList();
      } else {
         List<ItemStack> allSubtypes = new ArrayList();
         this.addSubtypesToList(allSubtypes, stacks);
         return isAllNulls(allSubtypes) ? Collections.emptyList() : allSubtypes;
      }
   }

   private static boolean isAllNulls(Iterable<?> iterable) {
      for(Object element : iterable) {
         if (element != null) {
            return false;
         }
      }

      return true;
   }

   private void addSubtypesToList(List<ItemStack> subtypesList, Iterable stacks) {
      for(Object obj : stacks) {
         if (obj instanceof ItemStack) {
            ItemStack itemStack = (ItemStack)obj;
            if (!itemStack.isEmpty()) {
               if (itemStack.getItemDamage() == 32767) {
                  this.addSubtypesToList(subtypesList, itemStack);
               } else {
                  subtypesList.add(itemStack);
               }
            }
         } else if (obj instanceof Iterable) {
            this.addSubtypesToList(subtypesList, (Iterable)obj);
         } else if (obj != null) {
            Log.get().error("Unknown object found: {}", obj);
         } else {
            subtypesList.add((Object)null);
         }
      }

   }

   public List<List<ItemStack>> expandRecipeItemStackInputs(@Nullable List inputs) {
      return inputs == null ? Collections.emptyList() : this.expandRecipeItemStackInputs(inputs, true);
   }

   public List<List<ItemStack>> expandRecipeItemStackInputs(List inputs, boolean expandSubtypes) {
      List<List<ItemStack>> expandedInputs = new ArrayList();

      for(Object input : inputs) {
         List<ItemStack> expandedInput = this.toItemStackList(input, expandSubtypes);
         expandedInputs.add(expandedInput);
      }

      return expandedInputs;
   }

   public NonNullList<ItemStack> toItemStackList(@Nullable Object stacks) {
      return stacks == null ? NonNullList.create() : this.toItemStackList(stacks, true);
   }

   public NonNullList<ItemStack> toItemStackList(Object stacks, boolean expandSubtypes) {
      UniqueItemStackListBuilder itemStackListBuilder = new UniqueItemStackListBuilder(this);
      this.toItemStackList(itemStackListBuilder, stacks, expandSubtypes);
      return itemStackListBuilder.build();
   }

   private void toItemStackList(UniqueItemStackListBuilder itemStackListBuilder, @Nullable Object input, boolean expandSubtypes) {
      if (input instanceof ItemStack) {
         this.toItemStackList(itemStackListBuilder, (ItemStack)input, expandSubtypes);
      } else if (input instanceof String) {
         for(ItemStack stack : OreDictionary.getOres((String)input)) {
            this.toItemStackList(itemStackListBuilder, stack, expandSubtypes);
         }
      } else if (input instanceof Ingredient) {
         for(ItemStack stack : this.getMatchingStacks((Ingredient)input)) {
            this.toItemStackList(itemStackListBuilder, stack, expandSubtypes);
         }
      } else if (input instanceof Iterable) {
         for(Object obj : (Iterable)input) {
            this.toItemStackList(itemStackListBuilder, obj, expandSubtypes);
         }
      } else if (input != null) {
         Log.get().error("Unknown object found: {}", input);
      }

   }

   private void toItemStackList(UniqueItemStackListBuilder itemStackListBuilder, @Nullable ItemStack itemStack, boolean expandSubtypes) {
      if (itemStack != null) {
         if (expandSubtypes && itemStack.getMetadata() == 32767) {
            for(ItemStack subtype : this.getSubtypes(itemStack)) {
               itemStackListBuilder.add(subtype);
            }
         } else {
            itemStackListBuilder.add(itemStack);
         }
      }

   }

   public String getUniqueIdentifierForStack(ItemStack stack) {
      return this.getUniqueIdentifierForStack(stack, StackHelper.UidMode.NORMAL);
   }

   public String getUniqueIdentifierForStack(ItemStack stack, StackHelper.UidMode mode) {
      ErrorUtil.checkNotEmpty(stack, "stack");
      if (this.uidCacheEnabled) {
         String result = (String)((Map)this.uidCache.get(mode)).get(stack);
         if (result != null) {
            return result;
         }
      }

      Item item = stack.getItem();
      ResourceLocation itemName = item.getRegistryName();
      if (itemName == null) {
         String stackInfo = ErrorUtil.getItemStackInfo(stack);
         throw new IllegalStateException("Item has no registry name: " + stackInfo);
      } else {
         StringBuilder itemKey = new StringBuilder(itemName.toString());
         int metadata = stack.getMetadata();
         if (mode != StackHelper.UidMode.WILDCARD && metadata != 32767) {
            String subtypeInfo = this.subtypeRegistry.getSubtypeInfo(stack);
            if (subtypeInfo != null) {
               if (!subtypeInfo.isEmpty()) {
                  itemKey.append(':').append(subtypeInfo);
               }
            } else if (mode == StackHelper.UidMode.FULL) {
               itemKey.append(':').append(metadata);
               NBTTagCompound serializedNbt = stack.serializeNBT();
               NBTTagCompound nbtTagCompound = serializedNbt.getCompoundTag("tag").copy();
               if (serializedNbt.hasKey("ForgeCaps")) {
                  NBTTagCompound forgeCaps = serializedNbt.getCompoundTag("ForgeCaps");
                  if (!forgeCaps.hasNoTags()) {
                     nbtTagCompound.setTag("ForgeCaps", forgeCaps);
                  }
               }

               if (!nbtTagCompound.hasNoTags()) {
                  itemKey.append(':').append(nbtTagCompound);
               }
            } else if (stack.getHasSubtypes()) {
               itemKey.append(':').append(metadata);
            }
         }

         String result = itemKey.toString();
         if (this.uidCacheEnabled) {
            ((Map)this.uidCache.get(mode)).put(stack, result);
         }

         return result;
      }
   }

   private abstract static class DelegateIterator<T, R> implements Iterator<R> {
      protected final Iterator<T> delegate;

      public DelegateIterator(Iterator<T> delegate) {
         this.delegate = delegate;
      }

      public boolean hasNext() {
         return this.delegate.hasNext();
      }

      public void remove() {
         this.delegate.remove();
      }
   }

   private interface ItemStackMatchable<R> {
      @Nullable
      ItemStack getStack();

      @Nullable
      R getResult();
   }

   private static class MatchingIndexed implements Iterable<StackHelper.ItemStackMatchable<Integer>> {
      private final Map<Integer, ItemStack> map;

      public MatchingIndexed(Map<Integer, ItemStack> map) {
         this.map = map;
      }

      public Iterator<StackHelper.ItemStackMatchable<Integer>> iterator() {
         return new StackHelper.DelegateIterator<Entry<Integer, ItemStack>, StackHelper.ItemStackMatchable<Integer>>(this.map.entrySet().iterator()) {
            public StackHelper.ItemStackMatchable<Integer> next() {
               final Entry<Integer, ItemStack> entry = (Entry)this.delegate.next();
               return new StackHelper.ItemStackMatchable<Integer>() {
                  public ItemStack getStack() {
                     return (ItemStack)entry.getValue();
                  }

                  public Integer getResult() {
                     return (Integer)entry.getKey();
                  }
               };
            }
         };
      }
   }

   public static class MatchingItemsResult {
      public final Map<Integer, Integer> matchingItems = new HashMap();
      public final List<Integer> missingItems = new ArrayList();

      public MatchingItemsResult() {
      }
   }

   private static class MatchingIterable implements Iterable<StackHelper.ItemStackMatchable<ItemStack>> {
      private final Iterable<ItemStack> list;

      public MatchingIterable(Iterable<ItemStack> list) {
         this.list = list;
      }

      public Iterator<StackHelper.ItemStackMatchable<ItemStack>> iterator() {
         Iterator<ItemStack> stacks = this.list.iterator();
         return new StackHelper.DelegateIterator<ItemStack, StackHelper.ItemStackMatchable<ItemStack>>(stacks) {
            public StackHelper.ItemStackMatchable<ItemStack> next() {
               final ItemStack stack = (ItemStack)this.delegate.next();
               return new StackHelper.ItemStackMatchable<ItemStack>() {
                  @Nullable
                  public ItemStack getStack() {
                     return stack;
                  }

                  @Nullable
                  public ItemStack getResult() {
                     return stack;
                  }
               };
            }
         };
      }
   }

   public static enum UidMode {
      NORMAL,
      WILDCARD,
      FULL;

      private UidMode() {
      }
   }
}

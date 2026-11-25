package mezz.jei.ingredients;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import mezz.jei.Internal;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IIngredientType;
import mezz.jei.config.Config;
import mezz.jei.gui.ingredients.IIngredientListElement;
import mezz.jei.startup.IModIdHelper;
import mezz.jei.util.ErrorUtil;
import mezz.jei.util.IngredientSet;
import mezz.jei.util.Log;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionHelper;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.NonNullList;

public class IngredientRegistry implements IIngredientRegistry {
   private final IModIdHelper modIdHelper;
   private final IngredientBlacklistInternal blacklist;
   private final Map<IIngredientType, IngredientSet> ingredientsMap;
   private final ImmutableMap<IIngredientType, IIngredientHelper> ingredientHelperMap;
   private final ImmutableMap<IIngredientType, IIngredientRenderer> ingredientRendererMap;
   private final ImmutableMap<Class, IIngredientType> ingredientTypeMap;
   private final NonNullList<ItemStack> fuels = NonNullList.create();
   private final NonNullList<ItemStack> potionIngredients = NonNullList.create();

   public IngredientRegistry(IModIdHelper modIdHelper, IngredientBlacklistInternal blacklist, Map<IIngredientType, IngredientSet> ingredientsMap, ImmutableMap<IIngredientType, IIngredientHelper> ingredientHelperMap, ImmutableMap<IIngredientType, IIngredientRenderer> ingredientRendererMap) {
      this.modIdHelper = modIdHelper;
      this.blacklist = blacklist;
      this.ingredientsMap = ingredientsMap;
      this.ingredientHelperMap = ingredientHelperMap;
      this.ingredientRendererMap = ingredientRendererMap;
      Builder<Class, IIngredientType> ingredientTypeBuilder = ImmutableMap.builder();

      for(IIngredientType ingredientType : ingredientsMap.keySet()) {
         ingredientTypeBuilder.put(ingredientType.getIngredientClass(), ingredientType);
      }

      this.ingredientTypeMap = ingredientTypeBuilder.build();

      for(ItemStack itemStack : this.getAllIngredients(VanillaTypes.ITEM)) {
         this.getStackProperties(itemStack);
      }

   }

   private void getStackProperties(ItemStack itemStack) {
      try {
         if (TileEntityFurnace.isItemFuel(itemStack)) {
            this.fuels.add(itemStack);
         }
      } catch (LinkageError | RuntimeException var5) {
         String itemStackInfo = ErrorUtil.getItemStackInfo(itemStack);
         Log.get().error("Failed to check if item is fuel {}.", itemStackInfo, var5);
      }

      try {
         if (PotionHelper.isReagent(itemStack)) {
            this.potionIngredients.add(itemStack);
         }
      } catch (LinkageError | RuntimeException var4) {
         String itemStackInfo = ErrorUtil.getItemStackInfo(itemStack);
         Log.get().error("Failed to check if item is a potion ingredient {}.", itemStackInfo, var4);
      }

   }

   /** @deprecated */
   @Deprecated
   public <V> List<V> getIngredients(Class<V> ingredientClass) {
      ErrorUtil.checkNotNull(ingredientClass, "ingredientClass");
      IIngredientType<V> ingredientType = this.getIngredientType(ingredientClass);
      IngredientSet<V> ingredients = (IngredientSet)this.ingredientsMap.get(ingredientType);
      return ingredients == null ? ImmutableList.of() : ImmutableList.copyOf(ingredients);
   }

   public <V> Collection<V> getAllIngredients(IIngredientType<V> ingredientType) {
      IngredientSet<V> ingredients = (IngredientSet)this.ingredientsMap.get(ingredientType);
      return (Collection<V>)(ingredients == null ? Collections.emptySet() : Collections.unmodifiableCollection(ingredients));
   }

   @Nullable
   public <V> V getIngredientByUid(IIngredientType<V> ingredientType, String uid) {
      IngredientSet<V> ingredients = (IngredientSet)this.ingredientsMap.get(ingredientType);
      return (V)(ingredients == null ? null : ingredients.getByUid(uid));
   }

   /** @deprecated */
   @Deprecated
   public <V> Collection<V> getAllIngredients(Class<V> ingredientClass) {
      IIngredientType<V> ingredientType = this.getIngredientType(ingredientClass);
      return this.getAllIngredients(ingredientType);
   }

   public <V> boolean isValidIngredient(V ingredient) {
      try {
         IIngredientHelper<V> ingredientHelper = this.getIngredientHelper(ingredient);
         return ingredientHelper.isValidIngredient(ingredient);
      } catch (RuntimeException var3) {
         return false;
      }
   }

   public <V> IIngredientHelper<V> getIngredientHelper(V ingredient) {
      ErrorUtil.checkNotNull(ingredient, "ingredient");
      IIngredientType<V> ingredientType = this.getIngredientType(ingredient);
      return this.getIngredientHelper(ingredientType);
   }

   public <V> IIngredientHelper<V> getIngredientHelper(IIngredientType<V> ingredientType) {
      ErrorUtil.checkNotNull(ingredientType, "ingredientType");
      IIngredientHelper<V> ingredientHelper = (IIngredientHelper)this.ingredientHelperMap.get(ingredientType);
      if (ingredientHelper != null) {
         return ingredientHelper;
      } else {
         throw new IllegalArgumentException("Unknown ingredient type: " + ingredientType);
      }
   }

   /** @deprecated */
   @Deprecated
   public <V> IIngredientHelper<V> getIngredientHelper(Class<? extends V> ingredientClass) {
      ErrorUtil.checkNotNull(ingredientClass, "ingredientClass");
      IIngredientType<V> ingredientType = this.getIngredientType(ingredientClass);
      return this.getIngredientHelper(ingredientType);
   }

   public <V> IIngredientRenderer<V> getIngredientRenderer(V ingredient) {
      ErrorUtil.checkNotNull(ingredient, "ingredient");
      IIngredientType<V> ingredientType = this.getIngredientType(ingredient);
      return this.getIngredientRenderer(ingredientType);
   }

   public <V> IIngredientRenderer<V> getIngredientRenderer(IIngredientType<V> ingredientType) {
      ErrorUtil.checkNotNull(ingredientType, "ingredientType");
      IIngredientRenderer<V> ingredientRenderer = (IIngredientRenderer)this.ingredientRendererMap.get(ingredientType);
      if (ingredientRenderer == null) {
         throw new IllegalArgumentException("Could not find ingredient renderer for " + ingredientType);
      } else {
         return ingredientRenderer;
      }
   }

   /** @deprecated */
   @Deprecated
   public <V> IIngredientRenderer<V> getIngredientRenderer(Class<? extends V> ingredientClass) {
      ErrorUtil.checkNotNull(ingredientClass, "ingredientClass");
      IIngredientType<V> ingredientType = this.getIngredientType(ingredientClass);
      return this.getIngredientRenderer(ingredientType);
   }

   public Collection<IIngredientType> getRegisteredIngredientTypes() {
      return this.ingredientTypeMap.values();
   }

   /** @deprecated */
   @Deprecated
   public Collection<Class> getRegisteredIngredientClasses() {
      return this.ingredientTypeMap.keySet();
   }

   public List<ItemStack> getFuels() {
      return Collections.unmodifiableList(this.fuels);
   }

   public List<ItemStack> getPotionIngredients() {
      return Collections.unmodifiableList(this.potionIngredients);
   }

   public <V> void addIngredientsAtRuntime(IIngredientType<V> ingredientType, Collection<V> ingredients) {
      this.addIngredientsAtRuntime(ingredientType, ingredients, Internal.getIngredientFilter());
   }

   /** @deprecated */
   @Deprecated
   public <V> void addIngredientsAtRuntime(Class<V> ingredientClass, List<V> ingredients) {
      IIngredientType<V> ingredientType = this.getIngredientType(ingredientClass);
      this.addIngredientsAtRuntime(ingredientType, ingredients);
   }

   /** @deprecated */
   @Deprecated
   public <V> void addIngredientsAtRuntime(Class<V> ingredientClass, Collection<V> ingredients) {
      IIngredientType<V> ingredientType = this.getIngredientType(ingredientClass);
      this.addIngredientsAtRuntime(ingredientType, ingredients);
   }

   public <V> void addIngredientsAtRuntime(IIngredientType<V> ingredientType, Collection<V> ingredients, IngredientFilter ingredientFilter) {
      ErrorUtil.assertMainThread();
      ErrorUtil.checkNotNull(ingredientType, "ingredientType");
      ErrorUtil.checkNotEmpty(ingredients, "ingredients");
      Collection<EnchantmentData> enchantmentData = this.hack_getBookEnchantmentData(ingredientType, ingredients);
      if (!enchantmentData.isEmpty()) {
         this.addIngredientsAtRuntime(VanillaTypes.ENCHANT, enchantmentData, ingredientFilter);
         if (ingredients.isEmpty()) {
            return;
         }
      }

      Log.get().info("Ingredients are being added at runtime: {} {}", Integer.valueOf(ingredients.size()), ingredientType.getIngredientClass().getName());
      IIngredientHelper<V> ingredientHelper = this.getIngredientHelper(ingredientType);
      Set<V> set = (Set)this.ingredientsMap.computeIfAbsent(ingredientType, (k) -> {
         return (void)IngredientSet.create(ingredientType, ingredientHelper);
      });

      for(V ingredient : ingredients) {
         set.add(ingredient);
         if (ingredient instanceof ItemStack) {
            this.getStackProperties((ItemStack)ingredient);
         }
      }

      for(IIngredientListElement<V> element : IngredientListElementFactory.createList(this, ingredientType, ingredients, this.modIdHelper)) {
         List<IIngredientListElement<V>> matchingElements = ingredientFilter.findMatchingElements(element);
         if (!matchingElements.isEmpty()) {
            for(IIngredientListElement<V> matchingElement : matchingElements) {
               this.blacklist.removeIngredientFromBlacklist(matchingElement.getIngredient(), ingredientHelper);
               ingredientFilter.updateHiddenState(matchingElement);
            }

            if (Config.isDebugModeEnabled()) {
               Log.get().debug("Updated ingredient: {}", ingredientHelper.getErrorInfo(element.getIngredient()));
            }
         } else {
            this.blacklist.removeIngredientFromBlacklist(element.getIngredient(), ingredientHelper);
            ingredientFilter.addIngredient(element);
            if (Config.isDebugModeEnabled()) {
               Log.get().debug("Added ingredient: {}", ingredientHelper.getErrorInfo(element.getIngredient()));
            }
         }
      }

      ingredientFilter.invalidateCache();
   }

   /** @deprecated */
   @Deprecated
   public <V> void removeIngredientsAtRuntime(Class<V> ingredientClass, List<V> ingredients) {
      IIngredientType<V> ingredientType = this.getIngredientType(ingredientClass);
      this.removeIngredientsAtRuntime(ingredientType, ingredients, Internal.getIngredientFilter());
   }

   /** @deprecated */
   @Deprecated
   public <V> void removeIngredientsAtRuntime(Class<V> ingredientClass, Collection<V> ingredients) {
      IIngredientType<V> ingredientType = this.getIngredientType(ingredientClass);
      this.removeIngredientsAtRuntime(ingredientType, ingredients, Internal.getIngredientFilter());
   }

   public <V> void removeIngredientsAtRuntime(IIngredientType<V> ingredientType, Collection<V> ingredients) {
      this.removeIngredientsAtRuntime(ingredientType, ingredients, Internal.getIngredientFilter());
   }

   public <V> IIngredientType<V> getIngredientType(V ingredient) {
      ErrorUtil.checkNotNull(ingredient, "ingredient");
      Class<? extends V> ingredientClass = ingredient.getClass();
      return this.getIngredientType(ingredientClass);
   }

   public <V> IIngredientType<V> getIngredientType(Class<? extends V> ingredientClass) {
      ErrorUtil.checkNotNull(ingredientClass, "ingredientClass");
      IIngredientType<V> ingredientType = (IIngredientType)this.ingredientTypeMap.get(ingredientClass);
      if (ingredientType != null) {
         return ingredientType;
      } else {
         UnmodifiableIterator var3 = this.ingredientTypeMap.values().iterator();

         while(var3.hasNext()) {
            IIngredientType<?> type = (IIngredientType)var3.next();
            if (type.getIngredientClass().isAssignableFrom(ingredientClass)) {
               return type;
            }
         }

         throw new IllegalArgumentException("Unknown ingredient class: " + ingredientClass);
      }
   }

   public <V> void removeIngredientsAtRuntime(IIngredientType<V> ingredientType, Collection<V> ingredients, IngredientFilter ingredientFilter) {
      ErrorUtil.assertMainThread();
      ErrorUtil.checkNotNull(ingredientType, "ingredientType");
      ErrorUtil.checkNotEmpty(ingredients, "ingredients");
      Collection<EnchantmentData> enchantmentData = this.hack_getBookEnchantmentData(ingredientType, ingredients);
      if (!enchantmentData.isEmpty()) {
         this.removeIngredientsAtRuntime(VanillaTypes.ENCHANT, enchantmentData, ingredientFilter);
         if (ingredients.isEmpty()) {
            return;
         }
      }

      Log.get().info("Ingredients are being removed at runtime: {} {}", Integer.valueOf(ingredients.size()), ingredientType.getIngredientClass().getName());
      IngredientSet<V> set = (IngredientSet)this.ingredientsMap.get(ingredientType);
      if (set != null) {
         set.removeAll(ingredients);
      }

      IIngredientHelper<V> ingredientHelper = this.getIngredientHelper(ingredientType);

      for(IIngredientListElement<V> element : IngredientListElementFactory.createList(this, ingredientType, ingredients, this.modIdHelper)) {
         List<IIngredientListElement<V>> matchingElements = ingredientFilter.findMatchingElements(element);
         if (matchingElements.isEmpty()) {
            V ingredient = element.getIngredient();
            String errorInfo = ingredientHelper.getErrorInfo(ingredient);
            Log.get().error("Could not find any matching ingredients to remove: {}", errorInfo);
         } else if (Config.isDebugModeEnabled()) {
            Log.get().debug("Removed ingredient: {}", ingredientHelper.getErrorInfo(element.getIngredient()));
         }

         for(IIngredientListElement<V> matchingElement : matchingElements) {
            this.blacklist.addIngredientToBlacklist(matchingElement.getIngredient(), ingredientHelper);
            matchingElement.setVisible(false);
         }
      }

      ingredientFilter.invalidateCache();
   }

   public <V> boolean isIngredientVisible(V ingredient, IngredientFilter ingredientFilter) {
      IIngredientType<V> ingredientType = this.getIngredientType(ingredient);
      IIngredientListElement<V> element = IngredientListElementFactory.<V>createUnorderedElement(this, ingredientType, ingredient, this.modIdHelper);
      if (element == null) {
         return false;
      } else {
         List<IIngredientListElement<V>> matchingElements = ingredientFilter.findMatchingElements(element);
         if (matchingElements.isEmpty()) {
            return true;
         } else {
            for(IIngredientListElement matchingElement : matchingElements) {
               if (matchingElement.isVisible()) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   private <V> Collection<EnchantmentData> hack_getBookEnchantmentData(IIngredientType<V> ingredientType, Collection<V> ingredients) {
      if (ingredientType == VanillaTypes.ITEM) {
         List<EnchantmentData> enchantmentData = new ArrayList();
         Iterator<V> iterator = ingredients.iterator();

         while(iterator.hasNext()) {
            V ingredient = (V)iterator.next();
            ItemStack itemStack = (ItemStack)VanillaTypes.ITEM.getIngredientClass().cast(ingredient);
            EnchantmentData bookEnchantmentData = this.getBookEnchantmentData(itemStack);
            if (bookEnchantmentData != null) {
               enchantmentData.add(bookEnchantmentData);
               iterator.remove();
            }
         }

         return enchantmentData;
      } else {
         return Collections.emptyList();
      }
   }

   @Nullable
   private EnchantmentData getBookEnchantmentData(ItemStack itemStack) {
      Item item = itemStack.getItem();
      if (item instanceof ItemEnchantedBook) {
         NBTTagList enchantments = ItemEnchantedBook.getEnchantments(itemStack);
         return this.getBookEnchantmentData(enchantments);
      } else {
         return null;
      }
   }

   @Nullable
   private EnchantmentData getBookEnchantmentData(NBTTagList enchantments) {
      EnchantmentData bookEnchantment = null;

      for(NBTBase nbt : enchantments) {
         if (nbt instanceof NBTTagCompound) {
            NBTTagCompound nbttagcompound = (NBTTagCompound)nbt;
            int id = nbttagcompound.getShort("id");
            int level = nbttagcompound.getShort("lvl");
            Enchantment enchantment = Enchantment.getEnchantmentByID(id);
            if (enchantment != null && level > 0) {
               if (bookEnchantment != null) {
                  return null;
               }

               bookEnchantment = new EnchantmentData(enchantment, level);
            }
         }
      }

      return bookEnchantment;
   }
}

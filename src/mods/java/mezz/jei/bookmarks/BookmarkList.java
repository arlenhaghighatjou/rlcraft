package mezz.jei.bookmarks;

import java.nio.charset.StandardCharsets;
import net.lax1dude.eaglercraft.v1_8.IOUtils;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nullable;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IIngredientType;
import mezz.jei.config.Config;
import mezz.jei.gui.ingredients.IIngredientListElement;
import mezz.jei.gui.overlay.IIngredientGridSource;
import mezz.jei.ingredients.IngredientListElementFactory;
import mezz.jei.ingredients.IngredientRegistry;
import mezz.jei.startup.ForgeModIdHelper;
import mezz.jei.util.LegacyUtil;
import mezz.jei.util.Log;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

public class BookmarkList implements IIngredientGridSource {
   private static final String MARKER_OTHER = "O:";
   private static final String MARKER_STACK = "T:";
   private final List<Object> list = new LinkedList();
   private final List<IIngredientListElement> ingredientListElements = new LinkedList();
   private final IngredientRegistry ingredientRegistry;
   private final List<IIngredientGridSource.Listener> listeners = new ArrayList();

   public BookmarkList(IngredientRegistry ingredientRegistry) {
      this.ingredientRegistry = ingredientRegistry;
   }

   public <T> boolean add(T ingredient) {
      Object normalized = this.normalize(ingredient);
      if (!this.contains(normalized) && this.addToLists(normalized, true)) {
         this.notifyListenersOfChange();
         this.saveBookmarks();
         return true;
      } else {
         return false;
      }
   }

   protected <T> T normalize(T ingredient) {
      IIngredientHelper<T> ingredientHelper = this.ingredientRegistry.getIngredientHelper(ingredient);
      T copy = (T)LegacyUtil.getIngredientCopy(ingredient, ingredientHelper);
      if (copy instanceof ItemStack) {
         ((ItemStack)copy).setCount(1);
      } else if (copy instanceof FluidStack) {
         ((FluidStack)copy).amount = 1000;
      }

      return copy;
   }

   private boolean contains(Object ingredient) {
      IIngredientHelper<Object> ingredientHelper = this.ingredientRegistry.getIngredientHelper(ingredient);

      for(Object existing : this.list) {
         if (ingredient == existing) {
            return true;
         }

         if (existing != null && existing.getClass() == ingredient.getClass() && ingredientHelper.getUniqueId(existing).equals(ingredientHelper.getUniqueId(ingredient))) {
            return true;
         }
      }

      return false;
   }

   public boolean remove(Object ingredient) {
      int index = 0;

      for(Object existing : this.list) {
         if (ingredient == existing) {
            this.list.remove(index);
            this.ingredientListElements.remove(index);
            this.notifyListenersOfChange();
            this.saveBookmarks();
            return true;
         }

         ++index;
      }

      return false;
   }

   public void saveBookmarks() {
      List<String> strings = new ArrayList();

      for(IIngredientListElement<?> element : this.ingredientListElements) {
         Object object = element.getIngredient();
         if (object instanceof ItemStack) {
            strings.add("T:" + ((ItemStack)object).writeToNBT(new NBTTagCompound()).toString());
         } else {
            strings.add("O:" + getUid(element));
         }
      }

      VFile2 file = Config.getBookmarkFile();
      if (file != null) {
         StringBuilder builder = new StringBuilder();

         for(int i = 0; i < strings.size(); ++i) {
            builder.append((String)strings.get(i)).append('\n');
         }

         file.setAllChars(builder.toString());
      }

   }

   private static <T> String getUid(IIngredientListElement<T> element) {
      IIngredientHelper<T> ingredientHelper = element.getIngredientHelper();
      return ingredientHelper.getUniqueId(element.getIngredient());
   }

   public void loadBookmarks() {
      VFile2 file = Config.getBookmarkFile();
      if (file != null && file.exists()) {
         List<String> ingredientJsonStrings = IOUtils.readLines(file.getInputStream(), StandardCharsets.UTF_8);
         if (ingredientJsonStrings == null) {
            Log.get().error("Failed to load bookmarks from file {}", file);
            return;
         }

         Collection<IIngredientType> otherIngredientTypes = new ArrayList(this.ingredientRegistry.getRegisteredIngredientTypes());
         otherIngredientTypes.remove(VanillaTypes.ITEM);
         this.list.clear();
         this.ingredientListElements.clear();

         for(String ingredientJsonString : ingredientJsonStrings) {
            if (ingredientJsonString.startsWith("T:")) {
               String itemStackAsJson = ingredientJsonString.substring("T:".length());

               try {
                  NBTTagCompound itemStackAsNbt = JsonToNBT.getTagFromJson(itemStackAsJson);
                  ItemStack itemStack = new ItemStack(itemStackAsNbt);
                  if (!itemStack.isEmpty()) {
                     ItemStack normalized = (ItemStack)this.normalize(itemStack);
                     this.addToLists(normalized, false);
                  } else {
                     Log.get().warn("Failed to load bookmarked ItemStack from json string, the item no longer exists:\n{}", itemStackAsJson);
                  }
               } catch (NBTException var17) {
                  Log.get().error("Failed to load bookmarked ItemStack from json string:\n{}", itemStackAsJson, var17);
               }
            } else if (ingredientJsonString.startsWith("O:")) {
               String uid = ingredientJsonString.substring("O:".length());
               Object ingredient = this.getUnknownIngredientByUid(otherIngredientTypes, uid);
               if (ingredient != null) {
                  Object normalized = this.normalize(ingredient);
                  this.addToLists(normalized, false);
               }
            } else {
               Log.get().error("Failed to load unknown bookmarked ingredient:\n{}", ingredientJsonString);
            }
         }

         this.notifyListenersOfChange();
      }
   }

   @Nullable
   private Object getUnknownIngredientByUid(Collection<IIngredientType> ingredientTypes, String uid) {
      for(IIngredientType<?> ingredientType : ingredientTypes) {
         Object ingredient = this.ingredientRegistry.getIngredientByUid(ingredientType, uid);
         if (ingredient != null) {
            return ingredient;
         }
      }

      return null;
   }

   private <T> boolean addToLists(T ingredient, boolean addToFront) {
      IIngredientType<T> ingredientType = this.ingredientRegistry.getIngredientType(ingredient);
      IIngredientListElement<T> element = IngredientListElementFactory.<T>createUnorderedElement(this.ingredientRegistry, ingredientType, ingredient, ForgeModIdHelper.getInstance());
      if (element != null) {
         if (addToFront) {
            this.list.add(0, ingredient);
            this.ingredientListElements.add(0, element);
         } else {
            this.list.add(ingredient);
            this.ingredientListElements.add(element);
         }

         return true;
      } else {
         return false;
      }
   }

   public List<IIngredientListElement> getIngredientList() {
      return this.ingredientListElements;
   }

   public int size() {
      return this.ingredientListElements.size();
   }

   public boolean isEmpty() {
      return this.ingredientListElements.isEmpty();
   }

   public void addListener(IIngredientGridSource.Listener listener) {
      this.listeners.add(listener);
   }

   private void notifyListenersOfChange() {
      for(IIngredientGridSource.Listener listener : this.listeners) {
         listener.onChange();
      }

   }
}

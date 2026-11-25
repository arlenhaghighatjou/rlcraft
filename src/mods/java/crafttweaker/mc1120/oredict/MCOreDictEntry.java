package crafttweaker.mc1120.oredict;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemCondition;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.item.IItemTransformer;
import crafttweaker.api.item.IItemTransformerNew;
import crafttweaker.api.item.IngredientOr;
import crafttweaker.api.item.IngredientStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.oredict.IOreDictEntry;
import crafttweaker.api.oredict.IngredientOreDict;
import crafttweaker.api.player.IPlayer;
import crafttweaker.mc1120.CraftTweaker;
import crafttweaker.mc1120.actions.ActionOreDictAddAll;
import crafttweaker.mc1120.actions.ActionOreDictAddItem;
import crafttweaker.mc1120.actions.ActionOreDictMirror;
import crafttweaker.mc1120.actions.ActionOreDictRemoveItem;
import crafttweaker.mc1120.util.CraftTweakerHacks;
import crafttweaker.util.ArrayUtil;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreIngredient;

public class MCOreDictEntry implements IOreDictEntry {
   private static final List<NonNullList<ItemStack>> OREDICT_CONTENTS = CraftTweakerHacks.getOreIdStacks();
   private static final List<NonNullList<ItemStack>> OREDICT_CONTENTS_UN = CraftTweakerHacks.getOreIdStacksUn();
   private static final Field ORE_FIELD;
   public static final Map<String, List<IItemStack>> REMOVED_CONTENTS = new HashMap();
   private final String id;

   public MCOreDictEntry(String id) {
      this.id = id;
   }

   public static MCOreDictEntry getFromIngredient(OreIngredient oreIngredient) {
      if (ORE_FIELD != null) {
         try {
            NonNullList<ItemStack> ores = (NonNullList)ORE_FIELD.get(oreIngredient);

            for(String oreName : OreDictionary.getOreNames()) {
               if (ores == OreDictionary.getOres(oreName, false)) {
                  return new MCOreDictEntry(oreName);
               }
            }
         } catch (IllegalAccessException var6) {
            CraftTweaker.LOG.catching(var6);
         }
      }

      return null;
   }

   public static List<NonNullList<ItemStack>> getOredictContents() {
      return OREDICT_CONTENTS;
   }

   public static List<NonNullList<ItemStack>> getOredictContentsUn() {
      return OREDICT_CONTENTS_UN;
   }

   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.id;
   }

   public boolean isEmpty() {
      return OreDictionary.getOres(this.getName()).isEmpty();
   }

   public IItemStack getFirstItem() {
      List<IItemStack> items = this.getItems();
      if (items.isEmpty()) {
         CraftTweakerAPI.logInfo("No first item found for oredict " + this.getName() + "! Replacing with null reference");
         return null;
      } else {
         return (IItemStack)items.get(0);
      }
   }

   public void add(IItemStack... items) {
      List<IItemStack> removedContents = (List)REMOVED_CONTENTS.get(this.id);

      for(IItemStack item : items) {
         if (removedContents != null) {
            removedContents.removeIf((removedItem) -> {
               return removedItem.matches(item);
            });
         }

         ItemStack stack = CraftTweakerMC.getItemStack(item);
         if (!stack.isEmpty()) {
            CraftTweakerAPI.apply(new ActionOreDictAddItem(this.id, stack));
         }
      }

   }

   public void addItems(IItemStack[] items) {
      this.add(items);
   }

   public void addAll(IOreDictEntry... entries) {
      for(IOreDictEntry entry : entries) {
         if (entry instanceof MCOreDictEntry) {
            CraftTweakerAPI.apply(new ActionOreDictAddAll(this.id, ((MCOreDictEntry)entry).id));
         } else {
            CraftTweakerAPI.logError("not a valid entry! " + entry.toString());
         }
      }

   }

   public void remove(IItemStack... items) {
      REMOVED_CONTENTS.computeIfAbsent(this.id, (s) -> {
         return new ArrayList();
      });

      for(IItemStack item : items) {
         ((List)REMOVED_CONTENTS.get(this.id)).add(item);
         ItemStack result = ItemStack.EMPTY;

         for(ItemStack itemStack : OreDictionary.getOres(this.id)) {
            if (item.matches(CraftTweakerMC.getIItemStackForMatching(itemStack, true))) {
               result = itemStack;
               break;
            }
         }

         if (!result.isEmpty()) {
            CraftTweakerAPI.apply(new ActionOreDictRemoveItem(this.id, result));
         }
      }

   }

   public void removeItems(IItemStack[] items) {
      this.remove(items);
   }

   public boolean contains(IItemStack item) {
      for(ItemStack itemStack : OreDictionary.getOres(this.getName())) {
         if (CraftTweakerMC.matches(item, itemStack)) {
            return true;
         }
      }

      return false;
   }

   public void mirror(IOreDictEntry other) {
      if (other instanceof MCOreDictEntry) {
         CraftTweakerAPI.apply(new ActionOreDictMirror(this.id, ((MCOreDictEntry)other).id));
      } else {
         CraftTweakerAPI.logError("not a valid oredict entry");
      }

   }

   public String getMark() {
      return null;
   }

   public int getAmount() {
      return 1;
   }

   public List<IItemStack> getItems() {
      return (List)OreDictionary.getOres(this.getName()).stream().map(CraftTweakerMC::getIItemStackWildcardSize).collect(Collectors.toList());
   }

   public IItemStack[] getItemArray() {
      List<IItemStack> items = this.getItems();
      return (IItemStack[])items.toArray(new IItemStack[0]);
   }

   public List<ILiquidStack> getLiquids() {
      return Collections.emptyList();
   }

   public IIngredient amount(int amount) {
      return new IngredientStack(this, amount);
   }

   public IIngredient transformNew(IItemTransformerNew transformer) {
      return new IngredientOreDict(this, (String)null, ArrayUtil.EMPTY_CONDITIONS, new IItemTransformerNew[]{transformer}, ArrayUtil.EMPTY_TRANSFORMERS_NEW);
   }

   public IIngredient only(IItemCondition condition) {
      return new IngredientOreDict(this, (String)null, new IItemCondition[]{condition}, ArrayUtil.EMPTY_TRANSFORMERS, ArrayUtil.EMPTY_TRANSFORMERS_NEW);
   }

   public IIngredient marked(String mark) {
      return new IngredientOreDict(this, mark, ArrayUtil.EMPTY_CONDITIONS, ArrayUtil.EMPTY_TRANSFORMERS, ArrayUtil.EMPTY_TRANSFORMERS_NEW);
   }

   public IIngredient or(IIngredient ingredient) {
      return new IngredientOr(this, ingredient);
   }

   public boolean matches(IItemStack item) {
      return this.contains(item);
   }

   public boolean matchesExact(IItemStack item) {
      return this.contains(item);
   }

   public boolean matches(ILiquidStack liquid) {
      return false;
   }

   public boolean contains(IIngredient ingredient) {
      if (ingredient == null) {
         return false;
      } else {
         for(IItemStack item : ingredient.getItems()) {
            if (!this.matches(item)) {
               return false;
            }
         }

         return true;
      }
   }

   public IItemStack applyTransform(IItemStack item, IPlayer byPlayer) {
      return item;
   }

   public IItemStack applyNewTransform(IItemStack item) {
      return item;
   }

   public boolean hasNewTransformers() {
      return false;
   }

   public boolean hasTransformers() {
      return false;
   }

   public IIngredient transform(IItemTransformer transformer) {
      return new IngredientOreDict(this, (String)null, ArrayUtil.EMPTY_CONDITIONS, ArrayUtil.EMPTY_TRANSFORMERS, new IItemTransformer[]{transformer});
   }

   public Object getInternal() {
      return this.id;
   }

   public String toCommandString() {
      return this.toString();
   }

   public String toString() {
      return "<ore:" + this.id + ">";
   }

   public int hashCode() {
      return this.id.hashCode();
   }

   public boolean equals(Object other) {
      return other instanceof MCOreDictEntry && Objects.equals(((MCOreDictEntry)other).id, this.id);
   }

   static {
      Field toSet = null;

      try {
         toSet = OreIngredient.class.getDeclaredField("ores");
         toSet.setAccessible(true);
      } catch (NoSuchFieldException var2) {
         CraftTweaker.LOG.catching(var2);
      }

      ORE_FIELD = toSet;
   }
}

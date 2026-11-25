package crafttweaker.mods.jei.commands;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;
import com.google.common.collect.UnmodifiableIterator;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.mc1120.CraftTweaker;
import crafttweaker.mc1120.commands.CraftTweakerCommand;
import crafttweaker.mc1120.commands.SpecialMessagesChat;
import crafttweaker.mods.jei.JEIAddonPlugin;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TIntObjectProcedure;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.function.Consumer;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.wrapper.ICraftingRecipeWrapper;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import mezz.jei.ingredients.Ingredients;
import mezz.jei.plugins.vanilla.crafting.CraftingRecipeCategory;
import mezz.jei.plugins.vanilla.crafting.TippedArrowRecipeWrapper;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.Loader;

public class ConflictCommand extends CraftTweakerCommand {
   public static final ConflictCommand.CraftRecipeEntryComparator CRAFT_RECIPE_ENTRY_COMPARATOR = new ConflictCommand.CraftRecipeEntryComparator();
   private List<ConflictCommand.CraftingRecipeEntry> craftingRecipeEntries = new ArrayList();
   private TIntObjectMap<Range<Integer>> rangeMap;

   public ConflictCommand() {
      super("conflict");
   }

   protected void init() {
      this.setDescription(new ITextComponent[]{SpecialMessagesChat.getClickableCommandText("\u00a72/ct conflict", "/ct conflict", true), SpecialMessagesChat.getNormalMessage(" \u00a73Lists all conflicting crafting recipes in the game"), SpecialMessagesChat.getNormalMessage(" \u00a73Might take a bit of time depending on the size of the pack"), SpecialMessagesChat.getNormalMessage(" \u00a73This needs to be run on a client and with JEI installed")});
   }

   public void executeCommand(MinecraftServer server, final ICommandSender sender, String[] args) {
      if (JEIAddonPlugin.recipeRegistry != null && Loader.isModLoaded("jei")) {
         sender.sendMessage(SpecialMessagesChat.getNormalMessage("\u00a76Running Conflict scan. This might take a while"));
         (new Thread("ConflictScan_mc_thread") {
            public void run() {
               try {
                  ConflictCommand.this.runConflictScan();
                  sender.sendMessage(SpecialMessagesChat.getLinkToCraftTweakerLog("Conflicting Recipe list generated", sender));
               } catch (Exception var2) {
                  var2.printStackTrace();
               }

            }
         }).start();
      } else {
         sender.sendMessage(SpecialMessagesChat.getNormalMessage("\u00a74This command can only be executed on the Client and with JEI installed"));
         CraftTweakerAPI.logWarning("JEI plugin not loaded!");
      }

   }

   private void runConflictScan() {
      if (JEIAddonPlugin.recipeRegistry != null && Loader.isModLoaded("jei")) {
         this.craftingRecipeEntries.clear();
         if (this.rangeMap != null) {
            this.rangeMap.clear();
         }

         this.gatherRecipes();
         this.craftingRecipeEntries.sort(CRAFT_RECIPE_ENTRY_COMPARATOR);
         this.rangeMap = this.getRangeForSize();
         this.rangeMap.forEachEntry((k, v) -> {
            CraftTweaker.LOG.info(k + ": " + v);
            return true;
         });
         List<Entry<ConflictCommand.CraftingRecipeEntry, ConflictCommand.CraftingRecipeEntry>> conflictingRecipes = new ArrayList();
         int maxCount = this.craftingRecipeEntries.size();
         float lastProgress = 0.0F;

         for(int i = 0; i < maxCount; ++i) {
            ((ConflictCommand.CraftingRecipeEntry)this.craftingRecipeEntries.get(i)).checkAllConflicting(conflictingRecipes);
            float progress = (float)i / (float)maxCount;
            if ((double)(progress - lastProgress) > 0.05D) {
               int percentage = (int)(progress * 100.0F);
               CraftTweaker.LOG.info("Current Progress on the scan: " + percentage + "%");
               lastProgress = progress;
            }
         }

         CraftTweakerAPI.logInfo("Conflicting: " + conflictingRecipes.size());
         conflictingRecipes.forEach((it) -> {
            String shape1 = ((ConflictCommand.CraftingRecipeEntry)it.getKey()).shapedRecipe ? "[Shaped] " : "[Shapeless] ";
            String shape2 = ((ConflictCommand.CraftingRecipeEntry)it.getValue()).shapedRecipe ? "[Shaped] " : "[Shapeless] ";
            String name1 = "<" + ((ConflictCommand.CraftingRecipeEntry)it.getKey()).output.getItem().getRegistryName() + ":" + ((ConflictCommand.CraftingRecipeEntry)it.getKey()).output.getMetadata() + "> * " + ((ConflictCommand.CraftingRecipeEntry)it.getKey()).output.getCount();
            String name2 = "<" + ((ConflictCommand.CraftingRecipeEntry)it.getValue()).output.getItem().getRegistryName() + ":" + ((ConflictCommand.CraftingRecipeEntry)it.getValue()).output.getMetadata() + "> * " + ((ConflictCommand.CraftingRecipeEntry)it.getValue()).output.getCount();
            String name1dp = "[" + ((ConflictCommand.CraftingRecipeEntry)it.getKey()).recipeName + "](" + ((ConflictCommand.CraftingRecipeEntry)it.getKey()).output.getDisplayName() + ") ";
            String name2dp = "[" + ((ConflictCommand.CraftingRecipeEntry)it.getValue()).recipeName + "](" + ((ConflictCommand.CraftingRecipeEntry)it.getValue()).output.getDisplayName() + ") ";
            CraftTweakerAPI.logInfo(shape1 + name1dp + name1 + " conflicts with " + shape2 + name2dp + name2);
         });
      } else {
         CraftTweakerAPI.logWarning("JEI plugin not loaded yet!");
         CraftTweakerAPI.logWarning("This command can only be used on a client and with JEI installed");
      }
   }

   private TIntObjectMap<Range<Integer>> getRangeForSize() {
      TIntObjectMap<Range<Integer>> rangeMap = new TIntObjectHashMap();
      int recipeSize = 0;
      int recipeCount = 0;

      for(int i = 0; i < this.craftingRecipeEntries.size(); ++i) {
         ConflictCommand.CraftingRecipeEntry current = (ConflictCommand.CraftingRecipeEntry)this.craftingRecipeEntries.get(i);
         if (recipeSize == current.recipeSize) {
            ++recipeCount;
         }

         if (current.recipeSize > recipeSize) {
            rangeMap.put(recipeSize, Range.closedOpen(Integer.valueOf(i - recipeCount), Integer.valueOf(i)));
            recipeSize = current.recipeSize;
            recipeCount = 1;
         }
      }

      rangeMap.put(recipeSize, Range.closedOpen(Integer.valueOf(this.craftingRecipeEntries.size() - recipeCount), Integer.valueOf(this.craftingRecipeEntries.size())));
      return rangeMap;
   }

   private void gatherRecipes() {
      IRecipeRegistry reg = JEIAddonPlugin.recipeRegistry;

      for(IRecipeCategory<?> category : reg.getRecipeCategories()) {
         if (category instanceof CraftingRecipeCategory) {
            for(IRecipeWrapper wrapper : reg.getRecipeWrappers(category)) {
               if (wrapper instanceof ICraftingRecipeWrapper && !(wrapper instanceof TippedArrowRecipeWrapper)) {
                  ICraftingRecipeWrapper wrap = (ICraftingRecipeWrapper)wrapper;
                  IIngredients ing = new Ingredients();
                  wrap.getIngredients(ing);
                  List<List<ItemStack>> inputs = ing.getInputs(VanillaTypes.ITEM);
                  List<List<ItemStack>> outputs = ing.getOutputs(VanillaTypes.ITEM);
                  ItemStack output = outputs.size() > 0 ? (outputs.get(0) != null ? (((List)outputs.get(0)).size() > 0 ? (ItemStack)((List)outputs.get(0)).get(0) : null) : null) : null;
                  if (output != null) {
                     for(List<ItemStack> input : inputs) {
                        if (input.size() > 50) {
                           CraftTweakerAPI.logInfo("trimming list of size = " + input.size() + " for recipe with output " + output.getItem().getRegistryName() + ":" + output.getMetadata());
                           Set<Entry<Item, Integer>> hasAlready = new HashSet();
                           Iterator<ItemStack> iter = input.iterator();

                           while(iter.hasNext()) {
                              ItemStack stack = (ItemStack)iter.next();
                              SimpleEntry<Item, Integer> entry = new SimpleEntry(stack.getItem(), stack.getMetadata());
                              if (hasAlready.contains(entry)) {
                                 iter.remove();
                              } else {
                                 hasAlready.add(entry);
                              }
                           }
                        }
                     }

                     if (wrapper instanceof IShapedCraftingRecipeWrapper) {
                        this.craftingRecipeEntries.add(new ConflictCommand.CraftingRecipeEntry(inputs, output, ((IShapedCraftingRecipeWrapper)wrapper).getWidth(), ((IShapedCraftingRecipeWrapper)wrapper).getHeight(), wrap.getRegistryName() == null ? "noname" : wrap.getRegistryName().toString()));
                     } else {
                        this.craftingRecipeEntries.add(new ConflictCommand.CraftingRecipeEntry(inputs, output, wrap.getRegistryName() == null ? "noname" : wrap.getRegistryName().toString()));
                     }
                  }
               }
            }
         }
      }

   }

   public void dumpRecipes() {
      for(ConflictCommand.CraftingRecipeEntry entry : this.craftingRecipeEntries) {
         String s = entry.toString();
         CraftTweakerAPI.logInfo(s);
      }

   }

   private static class CraftRecipeEntryComparator implements Comparator<ConflictCommand.CraftingRecipeEntry>, Serializable {
      private static final long serialVersionUID = 9061636968374415469L;

      private CraftRecipeEntryComparator() {
      }

      public int compare(ConflictCommand.CraftingRecipeEntry o1, ConflictCommand.CraftingRecipeEntry o2) {
         return Integer.compare(o1.recipeSize, o2.recipeSize);
      }
   }

   public class CraftingRecipeEntry {
      List<List<ItemStack>> inputs;
      ItemStack output;
      int width;
      int height;
      boolean shapedRecipe;
      String recipeName;
      int recipeSize;

      CraftingRecipeEntry(List<List<ItemStack>> inputs, ItemStack output, int width, int height, String recipeName) {
         this.inputs = inputs;
         this.output = output;
         this.width = width;
         this.height = height;
         this.recipeName = recipeName;
         this.recipeSize = inputs.size();
         this.shapedRecipe = true;
      }

      CraftingRecipeEntry(List<List<ItemStack>> inputs, ItemStack output, String recipeName) {
         this.inputs = inputs;
         this.output = output;
         this.recipeName = recipeName;
         this.recipeSize = inputs.size();
         this.shapedRecipe = false;
      }

      public String toString() {
         StringBuilder sb = new StringBuilder();
         sb.append(this.shapedRecipe ? "(shaped)" : "(shapeless)");
         sb.append(this.output.toString());
         sb.append(" > ");
         int i = 0;

         for(List<ItemStack> list : this.inputs) {
            sb.append("\nList ").append(i++).append(" ").append("[").append(list.size()).append("]: ");

            for(ItemStack stack : list) {
               sb.append(stack.toString()).append(" | ");
            }
         }

         return sb.toString();
      }

      void checkAllConflicting(List<Entry<ConflictCommand.CraftingRecipeEntry, ConflictCommand.CraftingRecipeEntry>> entryList) {
         Range<Integer> range = (Range)ConflictCommand.this.rangeMap.get(this.recipeSize);
         UnmodifiableIterator var3 = ContiguousSet.create(range, DiscreteDomain.integers()).iterator();

         while(var3.hasNext()) {
            int index = ((Integer)var3.next()).intValue();
            ConflictCommand.CraftingRecipeEntry otherEntry = (ConflictCommand.CraftingRecipeEntry)ConflictCommand.this.craftingRecipeEntries.get(index);
            if (otherEntry != this && this.checkConflict(otherEntry) && !entryList.contains(new SimpleEntry(this, otherEntry)) && !entryList.contains(new SimpleEntry(otherEntry, this))) {
               entryList.add(new SimpleEntry(this, otherEntry));
            }
         }

      }

      boolean checkConflict(ConflictCommand.CraftingRecipeEntry other) {
         if (this.shapedRecipe && other.shapedRecipe) {
            return this.checkShapedShaped(other);
         } else if (!this.shapedRecipe && !other.shapedRecipe) {
            return this.checkShapelessShapeless(other);
         } else {
            return this.shapedRecipe && this.checkShapedShapeless(other);
         }
      }

      boolean checkForEmptySlots(List<ItemStack> inputThis, List<ItemStack> inputOther) {
         if (inputThis.isEmpty() && inputOther.isEmpty()) {
            return true;
         } else if (inputThis.size() == 1 && inputOther.size() == 1 && ((ItemStack)inputThis.get(0)).isEmpty() && ((ItemStack)inputOther.get(0)).isEmpty()) {
            return true;
         } else if (inputThis.isEmpty() && inputOther.size() == 1 && ((ItemStack)inputOther.get(0)).isEmpty()) {
            return true;
         } else {
            return inputOther.isEmpty() && inputThis.size() == 1 ? ((ItemStack)inputThis.get(0)).isEmpty() : false;
         }
      }

      boolean checkShapedShaped(ConflictCommand.CraftingRecipeEntry other) {
         int overlapping = 0;
         if (this.width == other.width && this.height == other.height) {
            for(int i = 0; i < this.recipeSize && overlapping == i; ++i) {
               List<ItemStack> inputThis = (List)this.inputs.get(i);
               List<ItemStack> inputOther = (List)other.inputs.get(i);
               if (this.checkForEmptySlots(inputThis, inputOther)) {
                  ++overlapping;
               } else if (this.checkConflictingItemStacks(inputThis, inputOther)) {
                  ++overlapping;
               }
            }

            return overlapping == this.recipeSize && !this.compareItemStack(this.output, other.output);
         } else {
            return false;
         }
      }

      boolean checkConflictingItemStacks(List<ItemStack> stackListThis, List<ItemStack> stackListOther) {
         boolean hasOneConflict = false;

         for(ItemStack stackListThi : stackListThis) {
            for(ItemStack aStackListOther : stackListOther) {
               if (this.compareItemStack(stackListThi, aStackListOther)) {
                  hasOneConflict = true;
               }
            }
         }

         return hasOneConflict;
      }

      boolean checkShapelessShapeless(ConflictCommand.CraftingRecipeEntry other) {
         int overlapping = 0;
         if (this.recipeSize != other.recipeSize) {
            return false;
         } else {
            for(int i = 0; i < this.recipeSize && overlapping == i; ++i) {
               List<ItemStack> inputThis = (List)this.inputs.get(i);
               List<ItemStack> inputOther = (List)other.inputs.get(i);
               boolean hasOneConflict = false;

               for(ItemStack stack : inputThis) {
                  for(ItemStack otherStack : inputOther) {
                     if (this.compareItemStack(stack, otherStack)) {
                        hasOneConflict = true;
                     }
                  }
               }

               if (hasOneConflict) {
                  ++overlapping;
               }
            }

            return overlapping == this.recipeSize && !this.compareItemStack(this.output, other.output);
         }
      }

      boolean checkShapedShapeless(ConflictCommand.CraftingRecipeEntry other) {
         int overlapping = 0;
         if (this.recipeSize == 9 && other.recipeSize == 9) {
            for(int i = 0; i < this.recipeSize && overlapping == i; ++i) {
               List<ItemStack> inputThis = (List)this.inputs.get(i);
               List<ItemStack> inputOther = (List)other.inputs.get(i);
               if (!inputThis.isEmpty()) {
                  boolean hasOneConflict = false;

                  for(ItemStack stack : inputThis) {
                     for(ItemStack otherStack : inputOther) {
                        if (this.compareItemStack(stack, otherStack)) {
                           hasOneConflict = true;
                        }
                     }
                  }

                  if (hasOneConflict) {
                     ++overlapping;
                  }
               }
            }
         }

         return overlapping == this.recipeSize && !this.compareItemStack(this.output, other.output);
      }

      boolean compareItemStack(ItemStack stack1, ItemStack stack2) {
         if (stack1 != null && stack2 != null && !stack1.isEmpty() && !stack2.isEmpty()) {
            boolean itemsAreSame = stack1.getItem() == stack2.getItem() && stack1.getMetadata() == stack2.getMetadata();
            return itemsAreSame && Objects.equals(stack1.getTagCompound(), stack2.getTagCompound());
         } else {
            return false;
         }
      }

      public boolean equals(Object obj) {
         return obj instanceof ConflictCommand.CraftingRecipeEntry && this.inputs.equals(((ConflictCommand.CraftingRecipeEntry)obj).inputs) && this.output.equals(((ConflictCommand.CraftingRecipeEntry)obj).output) && this.width == ((ConflictCommand.CraftingRecipeEntry)obj).width && this.height == ((ConflictCommand.CraftingRecipeEntry)obj).height && this.shapedRecipe == ((ConflictCommand.CraftingRecipeEntry)obj).shapedRecipe && this.recipeName.equals(((ConflictCommand.CraftingRecipeEntry)obj).recipeName) && this.recipeSize == ((ConflictCommand.CraftingRecipeEntry)obj).recipeSize;
      }

      public int hashCode() {
         return Arrays.deepHashCode(new Object[]{this.inputs, this.output, this.recipeName, this.recipeSize, this.shapedRecipe});
      }
   }
}

package leviathan143.loottweaker.common.zenscript.wrapper;

import com.google.common.collect.Lists;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.DataMap;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import leviathan143.loottweaker.common.DeprecationWarningManager;
import leviathan143.loottweaker.common.lib.Arguments;
import leviathan143.loottweaker.common.lib.LootConditions;
import leviathan143.loottweaker.common.lib.LootFunctions;
import leviathan143.loottweaker.common.lib.QualifiedPoolIdentifier;
import leviathan143.loottweaker.common.mutable_loot.MutableLootPool;
import leviathan143.loottweaker.common.mutable_loot.entry.MutableLootEntry;
import leviathan143.loottweaker.common.mutable_loot.entry.MutableLootEntryEmpty;
import leviathan143.loottweaker.common.mutable_loot.entry.MutableLootEntryItem;
import leviathan143.loottweaker.common.mutable_loot.entry.MutableLootEntryTable;
import leviathan143.loottweaker.common.zenscript.LootTweakerContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.SetCount;
import net.minecraft.world.storage.loot.functions.SetDamage;
import net.minecraft.world.storage.loot.functions.SetMetadata;
import net.minecraft.world.storage.loot.functions.SetNBT;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("loottweaker.vanilla.loot.LootPool")
public class ZenLootPoolWrapper {
   private static final String ENTRY_NAME_PREFIX = "loottweaker#";
   private static final int DEFAULT_QUALITY = 0;
   private final LootTweakerContext context;
   private final List<ZenLootPoolWrapper.LootPoolTweaker> tweakers = new ArrayList();
   private final QualifiedPoolIdentifier qualifiedId;
   private int nextEntryNameId = 1;

   public ZenLootPoolWrapper(LootTweakerContext context, ResourceLocation parentTableId, String id) {
      this.context = context;
      this.qualifiedId = new QualifiedPoolIdentifier(parentTableId, id);
   }

   @ZenMethod
   public void addConditions(ZenLootConditionWrapper[] conditions) {
      if (Arguments.nonNull(this.context.getErrorHandler(), "conditions", conditions)) {
         List<LootCondition> parsedConditions = (List)Arrays.stream(conditions).filter(ZenLootConditionWrapper::isValid).map(ZenLootConditionWrapper::unwrap).collect(Collectors.toList());
         this.enqueueTweaker((pool) -> {
            pool.addConditions(parsedConditions);
         }, "Added %d conditions to %s", parsedConditions.size(), this.qualifiedId);
      }
   }

   @ZenMethod
   public void addConditionsHelper(ZenLootConditionWrapper[] conditions) {
      DeprecationWarningManager.addWarning();
      this.addConditions(conditions);
   }

   @ZenMethod
   public void addConditionsJson(ZenLootConditionWrapper[] conditions) {
      DeprecationWarningManager.addWarning();
      this.addConditions(conditions);
   }

   @ZenMethod
   public void clearConditions() {
      this.enqueueTweaker(MutableLootPool::clearConditions, "Queuing all conditions of %s for removal", this.qualifiedId);
   }

   @ZenMethod
   public void clearEntries() {
      this.enqueueTweaker(MutableLootPool::clearEntries, "Queuing all entries of %s for removal", this.qualifiedId);
   }

   @ZenMethod
   public void removeEntry(String entryName) {
      this.enqueueTweaker((pool) -> {
         if (pool.removeEntry(entryName) == null) {
            this.context.getErrorHandler().error("No entry with name %s exists in %s", entryName, this.qualifiedId);
         }

      }, "Queueing entry %s of %s for removal", entryName, this.qualifiedId);
   }

   @ZenMethod
   public void addItemEntry(IItemStack stack, int weight, @Optional String name) {
      if (Arguments.nonNull(this.context.getErrorHandler(), "stack", stack)) {
         this.addItemEntryInternal(stack, weight, 0, LootFunctions.NONE, LootConditions.NONE, name);
      }
   }

   @ZenMethod
   public void addItemEntry(IItemStack stack, int weight, int quality, @Optional String name) {
      if (Arguments.nonNull(this.context.getErrorHandler(), "stack", stack)) {
         this.addItemEntryInternal(stack, weight, quality, LootFunctions.NONE, LootConditions.NONE, name);
      }
   }

   @ZenMethod
   public void addItemEntry(IItemStack stack, int weight, int quality, ZenLootFunctionWrapper[] functions, ZenLootConditionWrapper[] conditions, @Optional String name) {
      if (Arguments.nonNull(this.context.getErrorHandler(), "stack", stack, "functions", functions, "conditions", conditions)) {
         LootFunction[] unwrappedFunctions = (LootFunction[])Arrays.stream(functions).filter(ZenLootFunctionWrapper::isValid).map(ZenLootFunctionWrapper::unwrap).toArray((x$0) -> {
            return new LootFunction[x$0];
         });
         LootCondition[] unwrappedConditions = (LootCondition[])Arrays.stream(conditions).filter(ZenLootConditionWrapper::isValid).map(ZenLootConditionWrapper::unwrap).toArray((x$0) -> {
            return new LootCondition[x$0];
         });
         this.addItemEntryInternal(stack, weight, quality, unwrappedFunctions, unwrappedConditions, name);
      }
   }

   @ZenMethod
   public void addItemEntryHelper(IItemStack stack, int weight, int quality, ZenLootFunctionWrapper[] functions, ZenLootConditionWrapper[] conditions, @Optional String name) {
      DeprecationWarningManager.addWarning();
      this.addItemEntry(stack, weight, quality, functions, conditions, name);
   }

   @ZenMethod
   public void addItemEntryJson(IItemStack stack, int weight, int quality, ZenLootFunctionWrapper[] functions, ZenLootConditionWrapper[] conditions, @Optional String name) {
      DeprecationWarningManager.addWarning();
      this.addItemEntry(stack, weight, quality, functions, conditions, name);
   }

   private void addItemEntryInternal(IItemStack stack, int weight, int quality, LootFunction[] functions, LootCondition[] conditions, @Optional String name) {
      if (stack != null) {
         String entryName = name != null ? name : this.generateName();
         Item item = CraftTweakerMC.getItemStack(stack).getItem();
         MutableLootEntryItem entry = new MutableLootEntryItem(entryName, weight, quality, Lists.newArrayList(conditions), item, this.withStackFunctions(stack, functions));
         this.addEntry(entry, "Queued item entry '%s' for addition to %s", entryName, this.qualifiedId);
      }
   }

   private List<LootFunction> withStackFunctions(IItemStack iStack, LootFunction[] existingFunctions) {
      ItemStack stack = CraftTweakerMC.getItemStack(iStack);
      boolean sizeFuncExists = false;
      boolean damageFuncExists = false;
      boolean nbtFuncExists = false;

      for(LootFunction lootFunction : existingFunctions) {
         if (lootFunction instanceof SetCount) {
            sizeFuncExists = true;
         }

         if (lootFunction instanceof SetDamage || lootFunction instanceof SetMetadata) {
            damageFuncExists = true;
         }

         if (lootFunction instanceof SetNBT) {
            nbtFuncExists = true;
         }
      }

      List<LootFunction> functionsOut = Lists.newArrayListWithCapacity(existingFunctions.length + 3);
      Collections.addAll(functionsOut, existingFunctions);
      if (iStack.getAmount() > 1 && !sizeFuncExists) {
         functionsOut.add(new SetCount(LootConditions.NONE, new RandomValueRange((float)iStack.getAmount())));
      }

      if (iStack.getDamage() > 0 && !damageFuncExists) {
         functionsOut.add(stack.isItemStackDamageable() ? new SetDamage(LootConditions.NONE, new RandomValueRange((float)stack.getItemDamage() / (float)stack.getMaxDamage())) : new SetMetadata(LootConditions.NONE, new RandomValueRange((float)iStack.getDamage())));
      }

      if (iStack.getTag() != DataMap.EMPTY && !nbtFuncExists) {
         functionsOut.add(new SetNBT(LootConditions.NONE, CraftTweakerMC.getNBTCompound(iStack.getTag())));
      }

      return functionsOut;
   }

   @ZenMethod
   public void addLootTableEntry(String tableName, int weight, @Optional String name) {
      this.addLootTableEntry(tableName, weight, 0, name);
   }

   @ZenMethod
   public void addLootTableEntry(String tableName, int weight, int quality, @Optional String name) {
      if (Arguments.nonNull(this.context.getErrorHandler(), "table name", tableName)) {
         this.addLootTableEntryInternal(tableName, weight, quality, LootConditions.NONE, name);
      }
   }

   @ZenMethod
   public void addLootTableEntry(String tableName, int weight, int quality, ZenLootConditionWrapper[] conditions, @Optional String name) {
      if (Arguments.nonNull(this.context.getErrorHandler(), "table name", tableName, "conditions", conditions)) {
         LootCondition[] unwrappedConditions = (LootCondition[])Arrays.stream(conditions).filter(ZenLootConditionWrapper::isValid).map(ZenLootConditionWrapper::unwrap).toArray((x$0) -> {
            return new LootCondition[x$0];
         });
         this.addLootTableEntryInternal(tableName, weight, quality, unwrappedConditions, name);
      }
   }

   @ZenMethod
   public void addLootTableEntryHelper(String tableName, int weight, int quality, ZenLootConditionWrapper[] conditions, @Optional String name) {
      DeprecationWarningManager.addWarning();
      this.addLootTableEntry(tableName, weight, quality, conditions, name);
   }

   @ZenMethod
   public void addLootTableEntryJson(String tableName, int weight, int quality, ZenLootConditionWrapper[] conditions, @Optional String name) {
      DeprecationWarningManager.addWarning();
      this.addLootTableEntry(tableName, weight, quality, conditions, name);
   }

   private void addLootTableEntryInternal(String tableName, int weight, int quality, LootCondition[] conditions, @Optional String name) {
      String entryName = name != null ? name : this.generateName();
      this.addEntry(new MutableLootEntryTable(entryName, weight, quality, conditions, new ResourceLocation(tableName)), "Queued loot table entry '%s' for addition to %s", entryName, this.qualifiedId);
   }

   @ZenMethod
   public void addEmptyEntry(int weight, @Optional String name) {
      this.addEmptyEntry(weight, 0, name);
   }

   @ZenMethod
   public void addEmptyEntry(int weight, int quality, @Optional String name) {
      this.addEmptyEntryInternal(weight, quality, LootConditions.NONE, name);
   }

   @ZenMethod
   public void addEmptyEntry(int weight, int quality, ZenLootConditionWrapper[] conditions, @Optional String name) {
      if (Arguments.nonNull(this.context.getErrorHandler(), "conditions", conditions)) {
         LootCondition[] unwrappedConditions = (LootCondition[])Arrays.stream(conditions).filter(ZenLootConditionWrapper::isValid).map(ZenLootConditionWrapper::unwrap).toArray((x$0) -> {
            return new LootCondition[x$0];
         });
         this.addEmptyEntryInternal(weight, quality, unwrappedConditions, name);
      }
   }

   @ZenMethod
   public void addEmptyEntryHelper(int weight, int quality, ZenLootConditionWrapper[] conditions, @Optional String name) {
      DeprecationWarningManager.addWarning();
      this.addEmptyEntry(weight, quality, conditions, name);
   }

   @ZenMethod
   public void addEmptyEntryJson(int weight, int quality, ZenLootConditionWrapper[] conditions, @Optional String name) {
      DeprecationWarningManager.addWarning();
      this.addEmptyEntry(weight, quality, conditions, name);
   }

   private void addEmptyEntryInternal(int weight, int quality, LootCondition[] conditions, @Optional String name) {
      String entryName = name != null ? name : this.generateName();
      this.addEntry(new MutableLootEntryEmpty(entryName, weight, quality, conditions), "Queued empty entry '%s' for addition to %s", entryName, this.qualifiedId);
   }

   private String generateName() {
      return "loottweaker#" + this.nextEntryNameId++;
   }

   @ZenMethod
   public void setRolls(float minRolls, float maxRolls) {
      this.enqueueTweaker((pool) -> {
         pool.setRolls(new RandomValueRange(minRolls, maxRolls));
      }, "Rolls of %s will be set to (%.0f, %.0f)", this.qualifiedId, minRolls, maxRolls);
   }

   @ZenMethod
   public void setBonusRolls(float minBonusRolls, float maxBonusRolls) {
      this.enqueueTweaker((pool) -> {
         pool.setBonusRolls(new RandomValueRange(minBonusRolls, maxBonusRolls));
      }, "Bonus rolls of %s will be set to (%.0f, %.0f)", this.qualifiedId, minBonusRolls, maxBonusRolls);
   }

   private void addEntry(MutableLootEntry entry, String format, Object... args) {
      this.enqueueTweaker((pool) -> {
         if (pool.getEntry(entry.getName()) != null) {
            this.context.getErrorHandler().error("Cannot add entry '%s' to %s. Entry names must be unique within their pool.", entry.getName(), pool.getName());
         } else {
            pool.addEntry(entry);
         }
      }, format, args);
   }

   private void enqueueTweaker(ZenLootPoolWrapper.LootPoolTweaker tweaker, String format, Object... args) {
      this.tweakers.add(tweaker);
      CraftTweakerAPI.logInfo(String.format(format, args));
   }

   public void tweak(MutableLootPool pool) {
      for(ZenLootPoolWrapper.LootPoolTweaker tweaker : this.tweakers) {
         tweaker.tweak(pool);
      }

   }

   public QualifiedPoolIdentifier getQualifiedId() {
      return this.qualifiedId;
   }

   @FunctionalInterface
   public interface LootPoolTweaker {
      void tweak(MutableLootPool var1);
   }
}

package org.jwaresoftware.mcmods.lib.loot;

import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryEmpty;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.conditions.RandomChance;
import net.minecraft.world.storage.loot.functions.EnchantWithLevels;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraft.world.storage.loot.functions.SetCount;
import net.minecraft.world.storage.loot.functions.SetDamage;
import net.minecraft.world.storage.loot.functions.SetMetadata;
import net.minecraft.world.storage.loot.functions.SetNBT;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.RandomStringUtils;
import org.jwaresoftware.mcmods.lib.ItemStackArray;
import org.jwaresoftware.mcmods.lib.ItemStacks;
import org.jwaresoftware.mcmods.lib.SharedGlue;
import org.jwaresoftware.mcmods.lib.api.ILootedListener;

public final class Loot {
   public static final String MINECRAFT_POOLNAME = "main";
   public static final String DEFAULT_EXTENSION_POOLNAME = "hord_xlootpool";
   static final Map<String, ConcurrentMap<ResourceLocation, Map<String, List<LootEntry>>>> lootDefsINSTANCE = new ConcurrentHashMap();
   public static final LootCondition[] _NOC = new LootCondition[0];
   private static final LootFunction[] _NOF = new LootFunction[0];
   static final Map<ResourceLocation, LootTable> xlootTablesINSTANCE = new ConcurrentHashMap();
   private static boolean _DEFAULTS_INITED = false;

   public static final boolean isEnabled(String modid) {
      return modid != null && lootDefsINSTANCE.containsKey(modid);
   }

   public static final void clearAll(String modid) {
      if (modid != null) {
         lootDefsINSTANCE.remove(modid);
      } else {
         lootDefsINSTANCE.clear();
      }

   }

   static final ConcurrentMap<ResourceLocation, Map<String, List<LootEntry>>> getModDefs(@Nonnull String modid) {
      ConcurrentMap<ResourceLocation, Map<String, List<LootEntry>>> modDefs = (ConcurrentMap)lootDefsINSTANCE.get(modid);
      if (modDefs == null) {
         modDefs = new ConcurrentHashMap();
         lootDefsINSTANCE.put(modid, modDefs);
      }

      return modDefs;
   }

   public static final LootEntryItem createEntryItem(String oid, Item item, int weight, int quality) {
      return new LootEntryItem(item, weight, quality < 0 ? 0 : quality, _NOF, _NOC, oid);
   }

   public static final LootEntryItem createEntryItem(String oid, Item item, int count, int weight, int quality) {
      return new LootEntryItem(item, weight, quality < 0 ? 0 : quality, new LootFunction[]{new SetCount(_NOC, new RandomValueRange((float)count))}, _NOC, oid);
   }

   public static final LootEntryItem createEntryItem(String oid, Item item, int min, int max, int weight, int quality) {
      return new LootEntryItem(item, weight, quality < 0 ? 0 : quality, new LootFunction[]{new SetCount(_NOC, new RandomValueRange((float)min, (float)max))}, _NOC, oid);
   }

   public static final LootEntryItem createEntryFlatItem(String oid, ItemStack item, int weight, int quality, float chance) {
      boolean nbt = item.hasTagCompound() && !item.getTagCompound().hasNoTags();
      int count = item.getCount();
      int nFunc = count > 1 ? 1 : 0;
      if (nbt) {
         ++nFunc;
      }

      LootFunction[] functions = new LootFunction[nFunc];
      int i = 0;
      if (count > 1) {
         functions[i++] = new SetCount(_NOC, new RandomValueRange((float)count));
      }

      if (nbt) {
         NBTTagCompound body = item.getTagCompound().copy();
         functions[i++] = new SetNBT(_NOC, body);
      }

      LootCondition[] conditions = _NOC;
      if (chance > 0.0F) {
         conditions = new LootCondition[]{new RandomChance(chance)};
      }

      return new LootEntryItem(item.getItem(), weight, quality < 0 ? 0 : quality, functions, conditions, oid);
   }

   public static final LootEntryItem createEntryItem(String oid, ItemStack item, int weight, int quality, float chance) {
      boolean nbt = item.hasTagCompound() && !item.getTagCompound().hasNoTags();
      int nFunc = item.getHasSubtypes() ? 1 : 0;
      int count = item.getCount();
      if (count > 1) {
         ++nFunc;
      }

      if (nbt) {
         ++nFunc;
      }

      LootFunction[] functions = new LootFunction[nFunc];
      int i = 0;
      if (item.getHasSubtypes()) {
         functions[i++] = new SetMetadata(_NOC, new RandomValueRange((float)item.getMetadata()));
      }

      if (count > 1) {
         functions[i++] = new SetCount(_NOC, new RandomValueRange((float)count));
      }

      if (nbt) {
         NBTTagCompound body = item.getTagCompound().copy();
         functions[i++] = new SetNBT(_NOC, body);
      }

      LootCondition[] conditions = _NOC;
      if (chance > 0.0F) {
         conditions = new LootCondition[]{new RandomChance(chance)};
      }

      return new LootEntryItem(item.getItem(), weight, quality < 0 ? 0 : quality, functions, conditions, oid);
   }

   public static final LootEntryItem createEntryItem(String oid, ItemStack item, int weight) {
      return createEntryItem(oid, item, weight, -1, 0.0F);
   }

   public static final LootEntryItem createEntryItem(String oid, ItemStack item, int min, int max, int weight, int quality, float chance) {
      boolean nbt = item.hasTagCompound() && !item.getTagCompound().hasNoTags();
      int nFunc = item.getHasSubtypes() ? 2 : 1;
      if (nbt) {
         ++nFunc;
      }

      LootFunction[] functions = new LootFunction[nFunc];
      int i = 0;
      if (item.getHasSubtypes()) {
         functions[i++] = new SetMetadata(_NOC, new RandomValueRange((float)item.getMetadata()));
      }

      functions[i++] = new SetCount(_NOC, new RandomValueRange((float)min, (float)max));
      if (nbt) {
         NBTTagCompound body = item.getTagCompound().copy();
         functions[i++] = new SetNBT(_NOC, body);
      }

      LootCondition[] conditions = _NOC;
      if (chance > 0.0F) {
         conditions = new LootCondition[]{new RandomChance(chance)};
      }

      return new LootEntryItem(item.getItem(), weight, quality < 0 ? 0 : quality, functions, conditions, oid);
   }

   public static final LootEntryItem createEntryItem(String oid, ItemStack item, int min, int max, int weight) {
      return createEntryItem(oid, item, min, max, weight, -1, 0.0F);
   }

   public static final LootEntryItem createEntryItem(String oid, Item item, int weight, int quality, LootFunction... functions) {
      return new LootEntryItem(item, weight, quality, functions, _NOC, oid);
   }

   public static final LootEntryItem createEntryItem(String oid, Item item, int weight, int quality, LootCondition[] conditions, LootFunction... functions) {
      return new LootEntryItem(item, weight, quality, functions, conditions, oid);
   }

   public static final LootEntryItem createDamagedItem(String oid, Item item, float min_damage, float max_damage, int weight, int quality) {
      return new LootEntryItem(item, weight, quality < 0 ? 0 : quality, new LootFunction[]{new SetDamage(_NOC, new RandomValueRange(min_damage, max_damage))}, _NOC, oid);
   }

   public static final LootEntryItem createDamagedItem(String oid, Item item, float min_damage, float max_damage, int min_elvl, int max_elvl, int weight, int quality) {
      return new LootEntryItem(item, weight, quality < 0 ? 0 : quality, new LootFunction[]{new SetDamage(_NOC, new RandomValueRange(min_damage, max_damage)), new EnchantWithLevels(_NOC, new RandomValueRange((float)min_elvl, (float)max_elvl), min_elvl > 19)}, _NOC, oid);
   }

   public static final void addLootEntry(String modid, ResourceLocation tableid, String poolid, LootEntry e) {
      ConcurrentMap<ResourceLocation, Map<String, List<LootEntry>>> modDefs = getModDefs(modid);
      Map<String, List<LootEntry>> poolDefs = (Map)modDefs.get(tableid);
      if (poolDefs == null) {
         poolDefs = new HashMap();
         modDefs.put(tableid, poolDefs);
      }

      if (poolid == null || poolid.isEmpty()) {
         poolid = "main";
      }

      List<LootEntry> loot = (List)poolDefs.get(poolid);
      if (loot == null) {
         loot = new ArrayList();
         poolDefs.put(poolid, loot);
      }

      loot.add(e);
   }

   public static final void addFiller(String modid, ResourceLocation tableid, String poolid, int weight) {
      LootEntryEmpty e = new LootEntryEmpty(weight, 0, _NOC, "empty_" + modid + "_" + RandomStringUtils.randomAlphanumeric(6));
      addLootEntry(modid, tableid, poolid, e);
   }

   public static final void addFiller(String modid, ResourceLocation tableid, String poolid, int weight, int n) {
      LootEntryEmpty e = new LootEntryEmpty(weight, 0, _NOC, "empty_" + modid + "_" + n);
      addLootEntry(modid, tableid, poolid, e);
   }

   public static final void addFishedUpJunk(String modid, String oid, Item item) {
      addLootEntry(modid, LootTableList.GAMEPLAY_FISHING_JUNK, (String)null, createEntryItem(oid, item, SharedGlue.JUNK_WEIGHT(), -1));
   }

   public static final void addFishedUpJunk(String modid, String oid, ItemStack item) {
      addLootEntry(modid, LootTableList.GAMEPLAY_FISHING_JUNK, (String)null, createEntryItem(oid, item, SharedGlue.JUNK_WEIGHT()));
   }

   public static final void addFishedUpJunk(String modid, String oid, Item item, float min_damage, float max_damage) {
      addLootEntry(modid, LootTableList.GAMEPLAY_FISHING_JUNK, (String)null, createDamagedItem(oid, item, min_damage, max_damage, SharedGlue.JUNK_WEIGHT() - 2, -1));
   }

   public static final void addFishedUpJunk(String modid, String oid, Item item, float min_damage, float max_damage, int min_elvl, int max_elvl) {
      addLootEntry(modid, LootTableList.GAMEPLAY_FISHING_JUNK, (String)null, createDamagedItem(oid, item, min_damage, max_damage, min_elvl, max_elvl, SharedGlue.JUNK_WEIGHT() - 2, -1));
   }

   public static final void addFishedUpUncommon(String modid, String oid, Item item) {
      addLootEntry(modid, LootTableList.GAMEPLAY_FISHING_JUNK, (String)null, createEntryItem(oid, item, SharedGlue.UNCOMMON_WEIGHT(), -1));
   }

   public static final void addFishedUpUncommon(String modid, String oid, ItemStack item) {
      addLootEntry(modid, LootTableList.GAMEPLAY_FISHING_JUNK, (String)null, createEntryItem(oid, item, SharedGlue.UNCOMMON_WEIGHT()));
   }

   public static final void addFishedUpTreasure(String modid, String oid, Item item) {
      addLootEntry(modid, LootTableList.GAMEPLAY_FISHING_TREASURE, (String)null, createEntryItem(oid, item, SharedGlue.TREASURE_WEIGHT(), -1));
   }

   public static final void addFishedUpTreasure(String modid, String oid, ItemStack item) {
      addLootEntry(modid, LootTableList.GAMEPLAY_FISHING_TREASURE, (String)null, createEntryItem(oid, item, SharedGlue.TREASURE_WEIGHT()));
   }

   public static final void merge(String modid, LootTableLoadEvent event) {
      if (isEnabled(modid)) {
         ConcurrentMap<ResourceLocation, Map<String, List<LootEntry>>> modDefs = getModDefs(modid);
         Map<String, List<LootEntry>> poolDefs = (Map)modDefs.get(event.getName());
         if (poolDefs != null) {
            for(String poolid : poolDefs.keySet()) {
               LootPool pool = event.getTable().getPool(poolid);
               if (pool != null) {
                  for(LootEntry e : (List)poolDefs.get(poolid)) {
                     pool.addEntry(e);
                  }
               } else {
                  if (isExtensionPool(poolid) && !isMainExtensionPool(poolid)) {
                     pool = new LootPool(new LootEntry[0], _NOC, new RandomValueRange(1.0F), new RandomValueRange(0.0F, 0.0F), poolid);
                     event.getTable().addPool(pool);

                     for(LootEntry e : (List)poolDefs.get(poolid)) {
                        pool.addEntry(e);
                     }
                  }

                  if (pool == null) {
                     System.err.println(modid + " unable to find pool id='" + poolid + "' for loot load for " + event.getName());
                  }
               }
            }
         }
      }

   }

   public static final boolean isMainExtensionPool(String poolid) {
      return "hord_xlootpool".equals(poolid);
   }

   public static final boolean isExtensionPool(String poolid) {
      return poolid != null && poolid.startsWith("hord_xlootpool");
   }

   public static final String asExtensionPool(String suffix) {
      return "hord_xlootpool_" + suffix;
   }

   @Nonnull
   public static final LootTable getModdedLootTable(@Nonnull ResourceLocation tableLoc, @Nonnull World world) {
      if (tableLoc.getResourcePath().startsWith("*RANDOM")) {
         return LootTable.EMPTY_LOOT_TABLE;
      } else if ("minecraft".equals(tableLoc.getResourceDomain())) {
         LootTable loottable_minecraft = world.getLootTableManager().getLootTableFromLocation(tableLoc);
         return loottable_minecraft != null ? loottable_minecraft : LootTable.EMPTY_LOOT_TABLE;
      } else {
         synchronized(xlootTablesINSTANCE) {
            LootTable loottable = (LootTable)xlootTablesINSTANCE.get(tableLoc);
            if (loottable == null) {
               loottable = world.getLootTableManager().getLootTableFromLocation(tableLoc);
               if (!loottable.isFrozen()) {
                  LootTable loottable_modded = ForgeEventFactory.loadLootTable(tableLoc, loottable, world.getLootTableManager());
                  if (loottable_modded == null) {
                     loottable_modded = LootTable.EMPTY_LOOT_TABLE;
                  }

                  if (loottable_modded != LootTable.EMPTY_LOOT_TABLE && !loottable_modded.isFrozen()) {
                     loottable_modded.freeze();
                  }

                  loottable = loottable_modded;
               }

               xlootTablesINSTANCE.put(tableLoc, loottable);
            }

            return loottable;
         }
      }
   }

   @Nonnull
   public static final List<Integer> getEmptySlotsRandomized(IItemHandler inventory, Random rand) {
      List<Integer> list = new ArrayList();
      int i = 0;

      for(int n = inventory.getSlots(); i < n; ++i) {
         if (inventory.getStackInSlot(i).isEmpty()) {
            list.add(Integer.valueOf(i));
         }
      }

      Collections.shuffle(list, rand);
      return list;
   }

   @Nullable
   public static final ResourceLocation getTopLevelLootTable(@Nonnull ItemStack from) {
      if (from.hasTagCompound()) {
         NBTTagCompound itemdata = from.getTagCompound();
         String rstring = null;
         if (itemdata.hasKey(SharedGlue.DEFAULT_LOOT_TABLE_TAGNAME(), 8)) {
            rstring = itemdata.getString(SharedGlue.DEFAULT_LOOT_TABLE_TAGNAME());
         } else if (itemdata.hasKey("withLoot", 8)) {
            rstring = itemdata.getString("withLoot");
         }

         if (rstring != null && !rstring.isEmpty()) {
            return new ResourceLocation(rstring);
         }
      }

      return null;
   }

   public static final ItemStack[] newStackArray(int slotCount) {
      return ItemStackArray.create(slotCount);
   }

   public static final List<Integer> getEmptySlots(IInventory inventory, Random rand) {
      ArrayList<Integer> empty_slots = new ArrayList();

      for(int i = 0; i < inventory.getSizeInventory(); ++i) {
         if (ItemStacks.isEmpty(inventory.getStackInSlot(i))) {
            empty_slots.add(Integer.valueOf(i));
         }
      }

      if (rand != null) {
         Collections.shuffle(empty_slots, rand);
      }

      return empty_slots;
   }

   public static final ItemStack createShulkerbox(EnumDyeColor color) {
      return BlockShulkerBox.getColoredItemStack(color == null ? EnumDyeColor.WHITE : color);
   }

   public static final ItemStack createUndyedShulkerbox() {
      return BlockShulkerBox.getColoredItemStack(EnumDyeColor.PURPLE);
   }

   protected Loot() {
   }

   public static final void init0() {
   }

   public static final void initDefaults() {
      if (!_DEFAULTS_INITED) {
         LootConditionManager.registerCondition(new RandomChanceWithLuckCondition.Serializer());
         LootConditionManager.registerCondition(new KilledByRealPlayerCondition.Serializer());
         LootFunctionManager.registerFunction(new SetUnbreakableFunction.Serializer(true));
         _DEFAULTS_INITED = true;
      }
   }

   public abstract static class OnCreatedFunction extends LootFunction {
      protected OnCreatedFunction(LootCondition[] conditions) {
         super(conditions);
      }

      public ItemStack apply(ItemStack stack, Random rand, LootContext context) {
         if (!stack.isEmpty()) {
            Entity e = context.getKillerPlayer();
            EntityPlayer player;
            if (e instanceof EntityPlayer) {
               player = (EntityPlayer)e;
            } else {
               player = FakePlayerFactory.getMinecraft(context.getWorld());
            }

            try {
               stack.getItem().onCreated(stack, context.getWorld(), player);
            } catch (Throwable var7) {
               ;
            }
         }

         return stack;
      }

      public abstract static class Serializer<K extends LootFunction> extends net.minecraft.world.storage.loot.functions.LootFunction.Serializer<K> {
         protected Serializer(String mod_id, Class<K> funcClass) {
            super(new ResourceLocation(mod_id, "on_created"), funcClass);
         }

         public void serialize(JsonObject object, K lfunction, JsonSerializationContext serializationContext) {
         }
      }
   }

   public abstract static class OnLootedFunction extends LootFunction {
      protected OnLootedFunction(LootCondition[] conditions) {
         super(conditions);
      }

      public ItemStack apply(ItemStack stack, Random rand, LootContext context) {
         if (!stack.isEmpty() && stack.getItem() instanceof ILootedListener) {
            Entity e = context.getKillerPlayer();
            EntityPlayer player;
            if (e instanceof EntityPlayer) {
               player = (EntityPlayer)e;
            } else {
               player = FakePlayerFactory.getMinecraft(context.getWorld());
            }

            try {
               ((ILootedListener)stack.getItem()).notifyLooted(stack, context.getWorld(), player, context.getLuck());
            } catch (Throwable var7) {
               ;
            }
         }

         return stack;
      }

      public abstract static class Serializer<K extends LootFunction> extends net.minecraft.world.storage.loot.functions.LootFunction.Serializer<K> {
         protected Serializer(String mod_id, Class<K> funcClass) {
            super(new ResourceLocation(mod_id, "on_looted"), funcClass);
         }

         public void serialize(JsonObject object, K lfunction, JsonSerializationContext serializationContext) {
         }
      }
   }
}

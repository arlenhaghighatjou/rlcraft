package com.charles445.rltweaker.reflect;

import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.ReflectUtil;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;

public class IceAndFireReflect {
   public final Class c_StoneEntityProperties = Class.forName("com.github.alexthe666.iceandfire.entity.StoneEntityProperties");
   public final Field f_StoneEntityProperties_isStone;
   public final Class c_ItemStoneStatue;
   public final Class c_EntityDragonBase = Class.forName("com.github.alexthe666.iceandfire.entity.EntityDragonBase");
   public final Class c_EntityMyrmexQueen;
   public final Class c_EntityMyrmexBase;
   public final Field f_EntityMyrmexBase_buyingList;
   public final Method m_EntityMyrmexBase_populateBuyingList;
   public final Method m_EntityMyrmexBase_isJungle;
   public final Class c_EntityGorgon;
   public final Class c_EntityStoneStatue;
   @Nullable
   public Class c_ItemDragonHornStatic;
   public final Class c_EntityPropertiesHandler;
   public final Field f_EntityPropertiesHandler_INSTANCE;
   public final Method m_EntityPropertiesHandler_getProperties;
   public final Object o_EntityPropertiesHandler_INSTANCE;
   private final Field f_LootTable_pools;
   private final Field f_LootPool_lootEntries;
   private final Field f_LootEntry_conditions;
   private final Field f_LootEntryItem_functions;

   public IceAndFireReflect() throws Exception {
      this.f_StoneEntityProperties_isStone = ReflectUtil.findField(this.c_StoneEntityProperties, "isStone");
      this.c_ItemStoneStatue = Class.forName("com.github.alexthe666.iceandfire.item.ItemStoneStatue");
      this.c_EntityMyrmexQueen = Class.forName("com.github.alexthe666.iceandfire.entity.EntityMyrmexQueen");
      this.c_EntityMyrmexBase = Class.forName("com.github.alexthe666.iceandfire.entity.EntityMyrmexBase");
      this.f_EntityMyrmexBase_buyingList = ReflectUtil.findField(this.c_EntityMyrmexBase, "buyingList");
      this.m_EntityMyrmexBase_populateBuyingList = ReflectUtil.findMethod(this.c_EntityMyrmexBase, "populateBuyingList");
      this.m_EntityMyrmexBase_isJungle = ReflectUtil.findMethod(this.c_EntityMyrmexBase, "isJungle");
      this.c_EntityGorgon = Class.forName("com.github.alexthe666.iceandfire.entity.EntityGorgon");
      this.c_EntityStoneStatue = Class.forName("com.github.alexthe666.iceandfire.entity.EntityStoneStatue");
      this.c_EntityPropertiesHandler = Class.forName("net.ilexiconn.llibrary.server.entity.EntityPropertiesHandler");
      this.f_EntityPropertiesHandler_INSTANCE = ReflectUtil.findField(this.c_EntityPropertiesHandler, "INSTANCE");
      this.m_EntityPropertiesHandler_getProperties = ReflectUtil.findMethod(this.c_EntityPropertiesHandler, "getProperties");
      this.o_EntityPropertiesHandler_INSTANCE = this.f_EntityPropertiesHandler_INSTANCE.get((Object)null);

      try {
         this.c_ItemDragonHornStatic = Class.forName("com.github.alexthe666.iceandfire.item.ItemDragonHornStatic");
      } catch (Exception var2) {
         this.c_ItemDragonHornStatic = null;
      }

      this.f_LootTable_pools = ReflectUtil.findFieldAny(LootTable.class, "pools", "pools");
      this.f_LootPool_lootEntries = ReflectUtil.findFieldAny(LootPool.class, "lootEntries", "lootEntries");
      this.f_LootEntry_conditions = ReflectUtil.findFieldAny(LootEntry.class, "conditions", "conditions");
      this.f_LootEntryItem_functions = ReflectUtil.findFieldAny(LootEntryItem.class, "functions", "functions");
   }

   public boolean getIsStone(Entity entity) {
      try {
         Object stoneProperty = this.getStoneProperty(entity);
         return stoneProperty == null ? false : this.f_StoneEntityProperties_isStone.getBoolean(stoneProperty);
      } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var3) {
         ErrorUtil.logSilent("IceAndFire Error getIsStone");
         return false;
      }
   }

   @Nullable
   public Object getStoneProperty(Entity entity) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return this.getProperties(entity, this.c_StoneEntityProperties);
   }

   protected Object getProperties(Entity entity, Class propertiesClazz) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return this.m_EntityPropertiesHandler_getProperties.invoke(this.o_EntityPropertiesHandler_INSTANCE, entity, propertiesClazz);
   }

   @Nullable
   public MerchantRecipeList getMyrmexTrades(Object myrmexBase) throws IllegalArgumentException, IllegalAccessException {
      return (MerchantRecipeList)this.f_EntityMyrmexBase_buyingList.get(myrmexBase);
   }

   public void resetMyrmexTrades(Object myrmexBase) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
      this.f_EntityMyrmexBase_buyingList.set(myrmexBase, (Object)null);
      this.m_EntityMyrmexBase_populateBuyingList.invoke(myrmexBase);
   }

   public boolean isMyrmexJungle(Object myrmexBase) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return ((Boolean)this.m_EntityMyrmexBase_isJungle.invoke(myrmexBase)).booleanValue();
   }

   @Nullable
   public List<LootPool> getPools(LootTable table) throws IllegalArgumentException, IllegalAccessException {
      return table == null ? null : (List)this.f_LootTable_pools.get(table);
   }

   @Nullable
   public List<LootEntry> getEntries(LootPool pool) throws IllegalArgumentException, IllegalAccessException {
      return pool == null ? null : (List)this.f_LootPool_lootEntries.get(pool);
   }

   @Nullable
   public LootCondition[] getConditions(LootEntry entry) throws IllegalArgumentException, IllegalAccessException {
      return entry == null ? null : (LootCondition[])((LootCondition[])this.f_LootEntry_conditions.get(entry));
   }

   public LootFunction[] getFunctions(LootEntryItem entry) throws IllegalArgumentException, IllegalAccessException {
      return entry == null ? null : (LootFunction[])((LootFunction[])this.f_LootEntryItem_functions.get(entry));
   }
}

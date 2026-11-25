package com.charles445.rltweaker.reflect;

import com.charles445.rltweaker.util.ReflectUtil;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;
import net.minecraft.item.ItemStack;

public class AquacultureReflect {
   public final Class c_WeightedLootSet = Class.forName("com.teammetallurgy.aquaculture.loot.WeightedLootSet");
   public final Method m_WeightedLootSet_addLoot;
   public final Class c_ItemNeptunesBounty;
   public final Field f_ItemNeptunesBounty_loot;
   public final Method m_ItemNeptunesBounty_initLoot;
   public final Class c_AquacultureItems;
   public final Field f_AquacultureItems_neptunesBounty;
   public final Field f_AquacultureItems_neptuniumBar;
   public final Class c_Config;
   public final Field f_Config_enableNeptuniumLoot;
   public final Class c_SubItem;
   public final Method m_SubItem_getItemStack;
   public final Class c_BiomeType;
   public final Field f_BiomeType_biomeMap;
   public final Field f_BiomeType_freshwater;
   public final Method m_BiomeType_addBiome;

   public AquacultureReflect() throws Exception {
      this.m_WeightedLootSet_addLoot = ReflectUtil.findMethod(this.c_WeightedLootSet, "addLoot", ItemStack.class, Integer.TYPE, Integer.TYPE, Integer.TYPE);
      this.c_ItemNeptunesBounty = Class.forName("com.teammetallurgy.aquaculture.items.ItemNeptunesBounty");
      this.f_ItemNeptunesBounty_loot = ReflectUtil.findField(this.c_ItemNeptunesBounty, "loot");
      this.m_ItemNeptunesBounty_initLoot = ReflectUtil.findMethod(this.c_ItemNeptunesBounty, "initLoot");
      this.c_AquacultureItems = Class.forName("com.teammetallurgy.aquaculture.items.AquacultureItems");
      this.f_AquacultureItems_neptunesBounty = ReflectUtil.findField(this.c_AquacultureItems, "neptunesBounty");
      this.f_AquacultureItems_neptuniumBar = ReflectUtil.findField(this.c_AquacultureItems, "neptuniumBar");
      this.c_Config = Class.forName("com.teammetallurgy.aquaculture.handlers.Config");
      this.f_Config_enableNeptuniumLoot = ReflectUtil.findField(this.c_Config, "enableNeptuniumLoot");
      this.c_SubItem = Class.forName("com.teammetallurgy.aquaculture.items.meta.SubItem");
      this.m_SubItem_getItemStack = ReflectUtil.findMethod(this.c_SubItem, "getItemStack", Integer.TYPE);
      this.c_BiomeType = Class.forName("com.teammetallurgy.aquaculture.loot.BiomeType");
      this.f_BiomeType_biomeMap = ReflectUtil.findField(this.c_BiomeType, "biomeMap");
      this.f_BiomeType_freshwater = ReflectUtil.findField(this.c_BiomeType, "freshwater");
      this.m_BiomeType_addBiome = ReflectUtil.findMethod(this.c_BiomeType, "addBiome");
   }

   public Object getNeptunesBountyLoot() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
      Object neptunesBounty = this.f_AquacultureItems_neptunesBounty.get((Object)null);
      neptunesBounty = this.c_ItemNeptunesBounty.cast(neptunesBounty);
      Object loot = this.f_ItemNeptunesBounty_loot.get(neptunesBounty);
      if (loot == null) {
         this.m_ItemNeptunesBounty_initLoot.invoke(neptunesBounty);
         loot = this.f_ItemNeptunesBounty_loot.get(neptunesBounty);
      }

      return loot;
   }

   public boolean getNeptuniumLootEnabled() throws IllegalArgumentException, IllegalAccessException {
      return ((Boolean)this.f_Config_enableNeptuniumLoot.get((Object)null)).booleanValue();
   }

   public void addLoot(Object weightedLootSet, ItemStack stack, int weight, int min, int max) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      this.m_WeightedLootSet_addLoot.invoke(weightedLootSet, stack, weight, min, max);
   }

   public ItemStack getNeptuniumBarStack() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return (ItemStack)this.m_SubItem_getItemStack.invoke(this.f_AquacultureItems_neptuniumBar.get((Object)null), Integer.valueOf(1));
   }

   public Map<Integer, ArrayList<Object>> getBiomeMap() throws IllegalArgumentException, IllegalAccessException {
      return (Map)this.f_BiomeType_biomeMap.get((Object)null);
   }

   public Object getFreshwaterBiomeType() throws IllegalArgumentException, IllegalAccessException {
      return this.f_BiomeType_freshwater.get((Object)null);
   }
}

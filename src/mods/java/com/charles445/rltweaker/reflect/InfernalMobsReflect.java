package com.charles445.rltweaker.reflect;

import com.charles445.rltweaker.util.ReflectUtil;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.enchantment.Enchantment;

public class InfernalMobsReflect {
   public final Class c_InfernalMobsCore = Class.forName("atomicstryker.infernalmobs.common.InfernalMobsCore");
   public final Field f_InfernalMobsCore_instance;
   public final Object o_InfernalMobsCore_instance;
   public final Field f_InfernalMobsCore_networkHelper;
   public final Object o_InfernalMobsCore_networkHelper;
   public final Field f_InfernalMobsCore_enchantmentList;
   public final Method m_InfernalMobsCore_getRandomEnchantment;
   public final Class c_NetworkHelper;
   public final Field f_NetworkHelper_isCurrentlySendingSemaphor;

   public InfernalMobsReflect() throws Exception {
      this.f_InfernalMobsCore_instance = ReflectUtil.findField(this.c_InfernalMobsCore, "instance");
      this.o_InfernalMobsCore_instance = this.f_InfernalMobsCore_instance.get((Object)null);
      this.f_InfernalMobsCore_networkHelper = ReflectUtil.findField(this.c_InfernalMobsCore, "networkHelper");
      this.o_InfernalMobsCore_networkHelper = this.f_InfernalMobsCore_networkHelper.get(this.o_InfernalMobsCore_instance);
      this.f_InfernalMobsCore_enchantmentList = ReflectUtil.findField(this.c_InfernalMobsCore, "enchantmentList");
      this.m_InfernalMobsCore_getRandomEnchantment = ReflectUtil.findMethod(this.c_InfernalMobsCore, "getRandomEnchantment");
      this.c_NetworkHelper = Class.forName("atomicstryker.infernalmobs.common.network.NetworkHelper");
      this.f_NetworkHelper_isCurrentlySendingSemaphor = ReflectUtil.findField(this.c_NetworkHelper, "isCurrentlySendingSemaphor");
   }

   public ArrayList<Enchantment> getEnchantmentList() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
      this.populateEnchantmentList();
      return (ArrayList)this.f_InfernalMobsCore_enchantmentList.get(this.o_InfernalMobsCore_instance);
   }

   private void populateEnchantmentList() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      this.m_InfernalMobsCore_getRandomEnchantment.invoke(this.o_InfernalMobsCore_instance, new Random());
   }

   public boolean getSemaphor() throws IllegalArgumentException, IllegalAccessException {
      return this.f_NetworkHelper_isCurrentlySendingSemaphor.getBoolean(this.o_InfernalMobsCore_networkHelper);
   }

   public void setSemaphor(boolean val) throws IllegalArgumentException, IllegalAccessException {
      this.f_NetworkHelper_isCurrentlySendingSemaphor.setBoolean(this.o_InfernalMobsCore_networkHelper, val);
   }
}

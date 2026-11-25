package com.charles445.simpledifficulty.compat.mod;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.SDDamageSources;
import com.charles445.simpledifficulty.util.ReflectUtil;
import com.charles445.simpledifficulty.util.VersionDelimiter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.DamageSource;

public class FirstAidCompat {
   private Class c_FirstAid;
   private Field f_FirstAid_VERSION;
   private VersionDelimiter version;
   private Class c_IStandardDamageDistributionBuilder;
   private Method m_IStandardDamageDistributionBuilder_addDistributionLayer;
   private Method m_IStandardDamageDistributionBuilder_disableNeighbourRestDistribution;
   private Method m_IStandardDamageDistributionBuilder_ignoreOrder;
   private Class c_DamageDistributionBuilderFactory;
   private Method m_DamageDistributionBuilderFactory_getInstance;
   private Method m_DamageDistributionBuilderFactory_newStandardBuilder;
   private Class c_EnumPlayerPart;
   private Object o_EnumPlayerPart_BODY;
   private Object o_EnumPlayerPart_HEAD;
   private Class c_BaseDamageDistributionBuilder;
   private Method m_BaseDamageDistributionBuilder_build;
   private Class c_IDamageDistribution;
   private Class c_FirstAidRegistryImpl;
   private Field f_FirstAidRegistryImpl_INSTANCE;
   private Method m_FirstAidRegistryImpl_registerDistribution_Array;

   public FirstAidCompat() {
      try {
         this.c_FirstAid = Class.forName("ichttt.mods.firstaid.FirstAid");
         this.f_FirstAid_VERSION = ReflectUtil.findField(this.c_FirstAid, "VERSION");
         String versionStr = (String)this.f_FirstAid_VERSION.get((Object)null);
         this.version = new VersionDelimiter(versionStr);
         if (this.version.isSameOrNewerVersion(1, 6, 16)) {
            this.c_IStandardDamageDistributionBuilder = Class.forName("ichttt.mods.firstaid.api.distribution.IStandardDamageDistributionBuilder");
            this.m_IStandardDamageDistributionBuilder_addDistributionLayer = ReflectUtil.findMethod(this.c_IStandardDamageDistributionBuilder, "addDistributionLayer");
            this.m_IStandardDamageDistributionBuilder_disableNeighbourRestDistribution = ReflectUtil.findMethod(this.c_IStandardDamageDistributionBuilder, "disableNeighbourRestDistribution");
            this.m_IStandardDamageDistributionBuilder_ignoreOrder = ReflectUtil.findMethod(this.c_IStandardDamageDistributionBuilder, "ignoreOrder");
            this.c_DamageDistributionBuilderFactory = Class.forName("ichttt.mods.firstaid.api.distribution.DamageDistributionBuilderFactory");
            this.m_DamageDistributionBuilderFactory_getInstance = ReflectUtil.findMethod(this.c_DamageDistributionBuilderFactory, "getInstance");
            this.m_DamageDistributionBuilderFactory_newStandardBuilder = ReflectUtil.findMethod(this.c_DamageDistributionBuilderFactory, "newStandardBuilder");
            this.c_EnumPlayerPart = Class.forName("ichttt.mods.firstaid.api.enums.EnumPlayerPart");
            this.o_EnumPlayerPart_BODY = ReflectUtil.findField(this.c_EnumPlayerPart, "BODY").get((Object)null);
            this.o_EnumPlayerPart_HEAD = ReflectUtil.findField(this.c_EnumPlayerPart, "HEAD").get((Object)null);
            this.c_BaseDamageDistributionBuilder = Class.forName("ichttt.mods.firstaid.common.apiimpl.distribution.BaseDamageDistributionBuilder");
            this.m_BaseDamageDistributionBuilder_build = ReflectUtil.findMethod(this.c_BaseDamageDistributionBuilder, "build");
            this.c_IDamageDistribution = Class.forName("ichttt.mods.firstaid.api.IDamageDistribution");
            this.c_FirstAidRegistryImpl = Class.forName("ichttt.mods.firstaid.common.apiimpl.FirstAidRegistryImpl");
            this.f_FirstAidRegistryImpl_INSTANCE = ReflectUtil.findField(this.c_FirstAidRegistryImpl, "INSTANCE");
            this.m_FirstAidRegistryImpl_registerDistribution_Array = this.c_FirstAidRegistryImpl.getDeclaredMethod("registerDistribution", DamageSource[].class, this.c_IDamageDistribution);
            this.m_FirstAidRegistryImpl_registerDistribution_Array.setAccessible(true);
            this.registerDamageSources();
         } else {
            SimpleDifficulty.logger.info("FirstAid version is lower than 1.6.16, compatibility was skipped");
         }
      } catch (Exception var2) {
         SimpleDifficulty.logger.error("FirstAidCompat reflection failed! First Aid compatibility is now disabled!", var2);
      }

   }

   private void registerDamageSources() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      Object factory = this.m_DamageDistributionBuilderFactory_getInstance.invoke((Object)null);
      if (factory == null) {
         SimpleDifficulty.logger.error("FirstAid DamageDistributionBuilderFactory not found!");
      } else {
         Object builder = this.newStandardBuilder(factory);
         this.addDistributionLayer(builder, EntityEquipmentSlot.CHEST, this.o_EnumPlayerPart_BODY);
         this.addDistributionLayer(builder, EntityEquipmentSlot.HEAD, this.o_EnumPlayerPart_HEAD);
         this.ignoreOrder(builder);
         this.disableNeighbourRestDistribution(builder);
         this.registerStatic(builder, SDDamageSources.DEHYDRATION);
         SimpleDifficulty.logger.info("FirstAid Dehydration Registered");
      }
   }

   private <O> O[] array(O... objs) {
      return objs;
   }

   private Object addDistributionLayer(Object builder, EntityEquipmentSlot ees, Object... enumPlayerParts) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return this.m_IStandardDamageDistributionBuilder_addDistributionLayer.invoke(builder, ees, ReflectUtil.createCastedArray(this.c_EnumPlayerPart, enumPlayerParts));
   }

   private Object disableNeighbourRestDistribution(Object builder) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return this.m_IStandardDamageDistributionBuilder_disableNeighbourRestDistribution.invoke(builder);
   }

   private Object ignoreOrder(Object builder) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return this.m_IStandardDamageDistributionBuilder_ignoreOrder.invoke(builder);
   }

   private void registerStatic(Object builder, DamageSource... sources) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      if (this.c_BaseDamageDistributionBuilder.isInstance(builder)) {
         Object instance = this.f_FirstAidRegistryImpl_INSTANCE.get((Object)null);
         Object built = this.m_BaseDamageDistributionBuilder_build.invoke(builder);
         this.m_FirstAidRegistryImpl_registerDistribution_Array.invoke(instance, sources, built);
      } else {
         throw new RuntimeException("Builder is not supported");
      }
   }

   private Object newStandardBuilder(Object factory) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return this.m_DamageDistributionBuilderFactory_newStandardBuilder.invoke(factory);
   }
}

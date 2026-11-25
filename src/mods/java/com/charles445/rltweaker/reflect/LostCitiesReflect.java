package com.charles445.rltweaker.reflect;

import com.charles445.rltweaker.util.ReflectUtil;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.world.World;

public class LostCitiesReflect {
   public final Class c_WorldTypeTools = Class.forName("mcjty.lostcities.dimensions.world.WorldTypeTools");
   public final Method m_WorldTypeTools_getLostCityChunkGenerator;
   public final Method m_WorldTypeTools_isLostCities;
   public final Class c_LostCityChunkGenerator;
   public final Field f_LostCityChunkGenerator_cachedPrimers;
   public final Field f_LostCityChunkGenerator_cachedHeightmaps;

   public LostCitiesReflect() throws Exception {
      this.m_WorldTypeTools_getLostCityChunkGenerator = ReflectUtil.findMethod(this.c_WorldTypeTools, "getLostCityChunkGenerator");
      this.m_WorldTypeTools_isLostCities = ReflectUtil.findMethod(this.c_WorldTypeTools, "isLostCities");
      this.c_LostCityChunkGenerator = Class.forName("mcjty.lostcities.dimensions.world.LostCityChunkGenerator");
      this.f_LostCityChunkGenerator_cachedPrimers = ReflectUtil.findField(this.c_LostCityChunkGenerator, "cachedPrimers");
      this.f_LostCityChunkGenerator_cachedHeightmaps = ReflectUtil.findField(this.c_LostCityChunkGenerator, "cachedHeightmaps");
   }

   @Nullable
   public Object getLostCityChunkGenerator(World world) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return this.m_WorldTypeTools_getLostCityChunkGenerator.invoke((Object)null, world);
   }

   public boolean isLostCities(World world) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return ((Boolean)this.m_WorldTypeTools_isLostCities.invoke((Object)null, world)).booleanValue();
   }

   public Map getCachedPrimers(Object o_LostCityChunkGenerator) throws IllegalArgumentException, IllegalAccessException {
      return (Map)this.f_LostCityChunkGenerator_cachedPrimers.get(o_LostCityChunkGenerator);
   }

   public Map getCachedHeightmaps(Object o_LostCityChunkGenerator) throws IllegalArgumentException, IllegalAccessException {
      return (Map)this.f_LostCityChunkGenerator_cachedHeightmaps.get(o_LostCityChunkGenerator);
   }
}

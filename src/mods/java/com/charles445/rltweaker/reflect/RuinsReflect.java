package com.charles445.rltweaker.reflect;

import com.charles445.rltweaker.util.CompatUtil;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ReflectUtil;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class RuinsReflect {
   public final Object ruinsInstance;
   public final Class c_ChunkLoggerData = Class.forName("atomicstryker.ruins.common.ChunkLoggerData");
   public final Field f_ChunkLoggerData_xCoords;
   public final Field f_ChunkLoggerData_zCoords;
   public final Class c_RuinsMod;
   public final Field f_RuinsMod_generatorMap;

   public RuinsReflect() throws Exception {
      this.f_ChunkLoggerData_xCoords = ReflectUtil.findField(this.c_ChunkLoggerData, "xCoords");
      this.f_ChunkLoggerData_zCoords = ReflectUtil.findField(this.c_ChunkLoggerData, "zCoords");
      this.c_RuinsMod = Class.forName("atomicstryker.ruins.common.RuinsMod");
      this.f_RuinsMod_generatorMap = ReflectUtil.findField(this.c_RuinsMod, "generatorMap");
      this.ruinsInstance = CompatUtil.getModInstance("ruins");
      if (this.ruinsInstance == null) {
         throw new CriticalException("Couldn't find Ruins Mod Instance!");
      }
   }

   public boolean wipeChunkLoggerData(Object chunkLoggerData, int cleanupThreshold) throws IllegalArgumentException, IllegalAccessException {
      ArrayList<Integer> xCoords = this.getXCoords(chunkLoggerData);
      ArrayList<Integer> zCoords = this.getZCoords(chunkLoggerData);
      if (xCoords.size() > cleanupThreshold) {
         xCoords.clear();
         zCoords.clear();
         return true;
      } else {
         return false;
      }
   }

   public ArrayList<Integer> getXCoords(Object chunkLoggerData) throws IllegalArgumentException, IllegalAccessException {
      return (ArrayList)this.f_ChunkLoggerData_xCoords.get(chunkLoggerData);
   }

   public ArrayList<Integer> getZCoords(Object chunkLoggerData) throws IllegalArgumentException, IllegalAccessException {
      return (ArrayList)this.f_ChunkLoggerData_zCoords.get(chunkLoggerData);
   }

   public ConcurrentHashMap<Integer, Object> getGeneratorMap() throws IllegalArgumentException, IllegalAccessException {
      return (ConcurrentHashMap)this.f_RuinsMod_generatorMap.get(this.ruinsInstance);
   }
}

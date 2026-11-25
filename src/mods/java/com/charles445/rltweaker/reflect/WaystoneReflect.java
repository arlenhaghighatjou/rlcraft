package com.charles445.rltweaker.reflect;

import com.charles445.rltweaker.util.ReflectUtil;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class WaystoneReflect {
   public final Class c_GenerateWaystoneNameEvent = Class.forName("net.blay09.mods.waystones.util.GenerateWaystoneNameEvent");
   public final Method m_getWaystoneName;
   public final Method m_setWaystoneName;
   public final Method m_getPos;
   public final Method m_getDimension;
   public final Class c_NameGenerator;
   public final Method m_NameGenerator_get;
   public final Method m_NameGenerator_randomName;
   public final Field f_NameGenerator_usedNames;
   public final Class c_RomanNumber;
   public final Method m_toRoman;

   public WaystoneReflect() throws Exception {
      this.m_getWaystoneName = ReflectUtil.findMethod(this.c_GenerateWaystoneNameEvent, "getWaystoneName");
      this.m_setWaystoneName = ReflectUtil.findMethod(this.c_GenerateWaystoneNameEvent, "setWaystoneName");
      this.m_getPos = ReflectUtil.findMethod(this.c_GenerateWaystoneNameEvent, "getPos");
      this.m_getDimension = ReflectUtil.findMethod(this.c_GenerateWaystoneNameEvent, "getDimension");
      this.c_NameGenerator = Class.forName("net.blay09.mods.waystones.worldgen.NameGenerator");
      this.m_NameGenerator_get = ReflectUtil.findMethod(this.c_NameGenerator, "get");
      this.m_NameGenerator_randomName = ReflectUtil.findMethod(this.c_NameGenerator, "randomName");
      this.f_NameGenerator_usedNames = ReflectUtil.findField(this.c_NameGenerator, "usedNames");
      this.c_RomanNumber = Class.forName("net.blay09.mods.waystones.worldgen.RomanNumber");
      this.m_toRoman = ReflectUtil.findMethod(this.c_RomanNumber, "toRoman");
   }

   public String toRoman(int value) throws Exception {
      return (String)this.m_toRoman.invoke((Object)null, value);
   }

   public String getWaystoneName(Object event) throws Exception {
      return (String)this.m_getWaystoneName.invoke(event);
   }

   public void setWaystoneName(Object event, String s) throws Exception {
      this.m_setWaystoneName.invoke(event, s);
   }

   public BlockPos getPos(Object event) throws Exception {
      return (BlockPos)this.m_getPos.invoke(event);
   }

   public int getDimension(Object event) throws Exception {
      return ((Integer)this.m_getDimension.invoke(event)).intValue();
   }

   public String getRandomName(int dimension) throws Exception {
      World world = DimensionManager.getWorld(dimension);
      Object generator = this.m_NameGenerator_get.invoke((Object)null, world);
      return (String)this.m_NameGenerator_randomName.invoke(generator, world.rand);
   }

   public Set<String> getUsedNames(int dimension) throws Exception {
      World world = DimensionManager.getWorld(dimension);
      Object generator = this.m_NameGenerator_get.invoke((Object)null, world);
      return (Set)this.f_NameGenerator_usedNames.get(generator);
   }
}

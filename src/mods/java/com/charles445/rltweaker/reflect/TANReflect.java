package com.charles445.rltweaker.reflect;

import com.charles445.rltweaker.util.ReflectUtil;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class TANReflect {
   public final Class c_ThirstHelper = Class.forName("toughasnails.api.thirst.ThirstHelper");
   public final Method m_ThirstHelper_getThirstData;
   public final Class c_IThirst;
   public final Method m_IThirst_getThirst;
   public final Method m_IThirst_getHydration;
   public final Method m_IThirst_getExhaustion;
   public final Method m_IThirst_setExhaustion;
   public final Class c_IPlayerStat;
   public final Method m_IPlayerStat_createUpdateMessage;
   public final Class c_PacketHandler;
   public final Field f_PacketHandler_instance;
   public final Class c_ThirstHandler;
   public final Method m_ThirstHandler_addExhaustion;

   public TANReflect() throws Exception {
      this.m_ThirstHelper_getThirstData = ReflectUtil.findMethod(this.c_ThirstHelper, "getThirstData");
      this.c_IThirst = Class.forName("toughasnails.api.stat.capability.IThirst");
      this.m_IThirst_getThirst = ReflectUtil.findMethod(this.c_IThirst, "getThirst");
      this.m_IThirst_getHydration = ReflectUtil.findMethod(this.c_IThirst, "getHydration");
      this.m_IThirst_getExhaustion = ReflectUtil.findMethod(this.c_IThirst, "getExhaustion");
      this.m_IThirst_setExhaustion = ReflectUtil.findMethod(this.c_IThirst, "setExhaustion");
      this.c_IPlayerStat = Class.forName("toughasnails.api.stat.IPlayerStat");
      this.m_IPlayerStat_createUpdateMessage = ReflectUtil.findMethod(this.c_IPlayerStat, "createUpdateMessage");
      this.c_PacketHandler = Class.forName("toughasnails.handler.PacketHandler");
      this.f_PacketHandler_instance = ReflectUtil.findField(this.c_PacketHandler, "instance");
      this.c_ThirstHandler = Class.forName("toughasnails.thirst.ThirstHandler");
      this.m_ThirstHandler_addExhaustion = ReflectUtil.findMethod(this.c_ThirstHandler, "addExhaustion");
   }

   public Object getThirstData(EntityPlayer player) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return this.m_ThirstHelper_getThirstData.invoke((Object)null, player);
   }

   public int readThirstFromData(Object data) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return ((Integer)this.m_IThirst_getThirst.invoke(data)).intValue();
   }

   public float readHydrationFromData(Object data) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return ((Float)this.m_IThirst_getHydration.invoke(data)).floatValue();
   }

   public float readExhaustionFromData(Object data) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return ((Float)this.m_IThirst_getExhaustion.invoke(data)).floatValue();
   }

   public void setExhaustionInData(Object data, float exhaustion) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      this.m_IThirst_setExhaustion.invoke(data, exhaustion);
   }

   public SimpleNetworkWrapper getPacketHandlerInstance() throws IllegalArgumentException, IllegalAccessException {
      return (SimpleNetworkWrapper)this.f_PacketHandler_instance.get((Object)null);
   }

   public IMessage createUpdateMessageWithStat(Object stat) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return (IMessage)this.m_IPlayerStat_createUpdateMessage.invoke(stat);
   }

   public boolean isThirstHandlerInstance(Object obj) {
      return this.c_ThirstHandler.isInstance(this.c_ThirstHandler);
   }

   public void addExhaustion(Object thirstHandler, float exhaustion) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      this.m_ThirstHandler_addExhaustion.invoke(thirstHandler, exhaustion);
   }
}

package com.charles445.simpledifficulty.util;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.SDCompatibility;
import java.lang.reflect.Method;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CompatUtil {
   private static CompatUtil instance;
   private Class EventBus;
   private Method m_register;

   public CompatUtil() {
   }

   public static boolean canUseMod(String modid) {
      return Loader.isModLoaded(modid) && !SDCompatibility.disabledCompletely.contains(modid);
   }

   public static boolean subscribeEventManually(Class<?> clazz, Object thiz, Method thiz_toCall) {
      try {
         if (instance == null) {
            instance = new CompatUtil();
            instance.EventBus = Class.forName("net.minecraftforge.fml.common.eventhandler.EventBus");
            instance.m_register = instance.EventBus.getDeclaredMethod("register", Class.class, Object.class, Method.class, ModContainer.class);
            instance.m_register.setAccessible(true);
         }

         if (!thiz_toCall.isAnnotationPresent(SubscribeEvent.class)) {
            SimpleDifficulty.logger.error("Method needs a SubscribeEvent annotation, go add one.");
            return false;
         } else {
            instance.m_register.invoke(MinecraftForge.EVENT_BUS, clazz, thiz, thiz_toCall, Loader.instance().getMinecraftModContainer());
            return true;
         }
      } catch (Exception var4) {
         SimpleDifficulty.logger.error("subscribeEventManually error.", var4);
         return false;
      }
   }
}

package com.charles445.rltweaker.util;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.ASMEventHandler;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import net.minecraftforge.fml.common.eventhandler.ListenerList;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CompatUtil {
   private static CompatUtil instance;
   private final Class c_EventBus = Class.forName("net.minecraftforge.fml.common.eventhandler.EventBus");
   private final Method m_register;
   private final Field f_listeners;
   private final Field f_busID;
   private final Class c_GameRegistry;
   private final Method m_GameRegistry_registerWorldGenerator;
   private final Field f_GameRegistry_worldGenerators;
   private final Field f_GameRegistry_worldGeneratorIndex;

   public CompatUtil() throws Exception {
      this.m_register = this.c_EventBus.getDeclaredMethod("register", Class.class, Object.class, Method.class, ModContainer.class);
      this.m_register.setAccessible(true);
      this.f_listeners = this.c_EventBus.getDeclaredField("listeners");
      this.f_listeners.setAccessible(true);
      this.f_busID = this.c_EventBus.getDeclaredField("busID");
      this.f_busID.setAccessible(true);
      this.c_GameRegistry = Class.forName("net.minecraftforge.fml.common.registry.GameRegistry");
      this.m_GameRegistry_registerWorldGenerator = ReflectUtil.findMethod(this.c_GameRegistry, "registerWorldGenerator");
      this.f_GameRegistry_worldGenerators = ReflectUtil.findField(this.c_GameRegistry, "worldGenerators");
      this.f_GameRegistry_worldGeneratorIndex = ReflectUtil.findField(this.c_GameRegistry, "worldGeneratorIndex");
   }

   public static void wrapSpecificHandler(String name, Consumer<IEventListener> constructor, String clazzName, String methodName) throws Exception {
      Object handler = findAndRemoveHandlerFromEventBus(clazzName, methodName);
      if (handler instanceof IEventListener) {
         RLTweaker.logger.info("Registering " + name + " to the event bus");
         constructor.accept((IEventListener)handler);
      }

   }

   public static void subscribeEventManually(Class<?> clazz, Object thiz, Method thiz_toCall) throws Exception {
      if (instance == null) {
         instance = new CompatUtil();
      }

      if (!thiz_toCall.isAnnotationPresent(SubscribeEvent.class)) {
         throw new RuntimeException("Method needs a SubscribeEvent annotation.");
      } else {
         instance.m_register.invoke(MinecraftForge.EVENT_BUS, clazz, thiz, thiz_toCall, Loader.instance().getMinecraftModContainer());
         RLTweaker.logger.info("Registered " + thiz.getClass().getName() + " " + thiz_toCall.getName() + " to the event bus");
      }
   }

   @Nullable
   public static Object getModInstance(String modid) {
      for(ModContainer modContainer : Loader.instance().getModList()) {
         if (modContainer.getModId().equals(modid)) {
            return modContainer.getMod();
         }
      }

      RLTweaker.logger.warn("Asked to find mod instance " + modid + ", but could not find it!");
      return null;
   }

   @Nullable
   public static Object findAndRemoveHandlerFromEventBus(Class clazz) throws Exception {
      return findAndRemoveHandlerFromEventBus(clazz.getName());
   }

   @Nullable
   public static Object findAndRemoveHandlerFromEventBus(String name) throws Exception {
      return findAndRemoveHandlerFromEventBus(name, (String)null);
   }

   @Nullable
   public static Object findAndRemoveHandlerFromEventBus(String name, @Nullable String specific) throws Exception {
      boolean criticalCrash = false;

      try {
         if (instance == null) {
            instance = new CompatUtil();
         }

         boolean found_listener = false;
         ConcurrentHashMap<Object, ArrayList<IEventListener>> listeners = (ConcurrentHashMap)instance.f_listeners.get(MinecraftForge.EVENT_BUS);
         RLTweaker.logger.debug("Listener Size: " + listeners.size());
         Object handler = null;

         for(Entry<Object, ArrayList<IEventListener>> listener_entry : listeners.entrySet()) {
            handler = listener_entry.getKey();
            String handlerName = handler.getClass().getName();
            if (handlerName.equals("java.lang.Class")) {
               handlerName = ((Class)handler).getName();
            }

            if (handlerName.equals(name)) {
               if (ModConfig.server.minecraft.debug) {
                  ArrayList<IEventListener> eventListeners = (ArrayList)listener_entry.getValue();
                  if (eventListeners == null) {
                     RLTweaker.logger.debug("eventListeners: null");
                  } else {
                     for(IEventListener eventListener : eventListeners) {
                        RLTweaker.logger.debug(eventListener.toString());
                     }
                  }
               }

               found_listener = true;
               if (specific == null) {
                  MinecraftForge.EVENT_BUS.unregister(handler);
                  RLTweaker.logger.info("Found and removed " + name + " from the event bus");
                  RLTweaker.logger.debug("Listener Size Post Unregister: " + listeners.size());
                  return handler;
               }

               ArrayList<IEventListener> eventListeners = (ArrayList)listener_entry.getValue();
               RLTweaker.logger.debug("EventListener Size Pre Unregister: " + eventListeners.size());
               Iterator<IEventListener> elIterator = eventListeners.iterator();

               while(elIterator.hasNext()) {
                  IEventListener eventListener = (IEventListener)elIterator.next();
                  if (eventListener.toString().contains(specific)) {
                     criticalCrash = true;
                     elIterator.remove();
                     RLTweaker.logger.debug("EventListener Size Post Unregister: " + eventListeners.size());
                     int busID = instance.f_busID.getInt(MinecraftForge.EVENT_BUS);
                     ListenerList.unregisterAll(busID, eventListener);
                     RLTweaker.logger.info("Found and removed " + name + " IEventListener " + specific + " from the event bus");
                     return eventListener;
                  }
               }

               RLTweaker.logger.debug("Instance Missing Specific EventListener, Continuing...");
            }
         }

         return null;
      } catch (Exception var13) {
         if (criticalCrash) {
            throw new CriticalException(var13);
         } else {
            throw var13;
         }
      }
   }

   @Nullable
   public static <W extends WorldGeneratorWrapper> W tryWrapWorldGenerator(W wrapperIn, Class<?> targetGeneratorClazz) throws Exception {
      boolean criticalCrash = false;

      try {
         if (instance == null) {
            instance = new CompatUtil();
         }

         Set<IWorldGenerator> worldGenerators = (Set)instance.f_GameRegistry_worldGenerators.get((Object)null);
         Map<IWorldGenerator, Integer> worldGeneratorIndex = (Map)instance.f_GameRegistry_worldGeneratorIndex.get((Object)null);
         if (worldGenerators == null) {
            RLTweaker.logger.error("worldGenerators was null in tryWrapWorldGenerator");
            ErrorUtil.logSilent("tryWrapWorldGenerator worldGenerators null");
            return (W)null;
         } else if (worldGeneratorIndex == null) {
            RLTweaker.logger.error("worldGeneratorIndex was null in tryWrapWorldGenerator");
            ErrorUtil.logSilent("tryWrapWorldGenerator worldGeneratorIndex null");
            return (W)null;
         } else {
            IWorldGenerator foundGenerator = null;

            for(IWorldGenerator generator : worldGenerators) {
               if (generator.getClass().equals(targetGeneratorClazz)) {
                  foundGenerator = generator;
                  break;
               }
            }

            if (foundGenerator == null) {
               RLTweaker.logger.error("Could not find world generator of class: " + targetGeneratorClazz.getName());
               ErrorUtil.logSilent("tryWrapWorldGenerator worldGenerators null");
               return (W)null;
            } else {
               Integer weight = (Integer)worldGeneratorIndex.get(foundGenerator);
               if (weight == null) {
                  RLTweaker.logger.error("Found world generator of class but weight was null: " + targetGeneratorClazz.getName());
                  ErrorUtil.logSilent("tryWrapWorldGenerator weight null");
                  return (W)null;
               } else {
                  criticalCrash = true;
                  if (!worldGenerators.remove(foundGenerator)) {
                     RLTweaker.logger.error("Failed to remove in worldGenerators with class: " + targetGeneratorClazz.getName());
                     return (W)null;
                  } else if (worldGeneratorIndex.remove(foundGenerator) == null) {
                     RLTweaker.logger.error("Failed to remove in worldGeneratorIndex with class: " + targetGeneratorClazz.getName());
                     return (W)null;
                  } else {
                     wrapperIn.setWrappedGenerator(foundGenerator);
                     instance.m_GameRegistry_registerWorldGenerator.invoke((Object)null, wrapperIn, weight.intValue());
                     RLTweaker.logger.info("Wrapped IWorldGenerator of class " + targetGeneratorClazz.getName());
                     return wrapperIn;
                  }
               }
            }
         }
      } catch (Exception var8) {
         if (criticalCrash) {
            throw new CriticalException(var8);
         } else {
            throw var8;
         }
      }
   }

   @Nullable
   public static Object getSubscriberInstance(IEventListener eventListener) {
      try {
         if (eventListener instanceof ASMEventHandler) {
            IEventListener customHandler = (IEventListener)ReflectUtil.findField(ASMEventHandler.class, "handler").get(eventListener);
            return ReflectUtil.findField(customHandler.getClass(), "instance").get(customHandler);
         } else {
            return null;
         }
      } catch (Exception var2) {
         var2.printStackTrace();
         return null;
      }
   }
}

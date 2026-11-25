package net.theawesomegem.fishingmadebetter.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import net.minecraftforge.fml.common.eventhandler.ListenerList;
import net.theawesomegem.fishingmadebetter.proxy.CommonProxy;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public class HandlerUtil {
   private static HandlerUtil instance;
   private final Class c_EventBus = Class.forName("net.minecraftforge.fml.common.eventhandler.EventBus");
   private final Field f_listeners;
   private final Field f_busID;

   public HandlerUtil() throws Exception {
      this.f_listeners = this.c_EventBus.getDeclaredField("listeners");
      this.f_listeners.setAccessible(true);
      this.f_busID = this.c_EventBus.getDeclaredField("busID");
      this.f_busID.setAccessible(true);
   }

   @Nullable
   public static Object findAndRemoveHandlerFromEventBus(String name, @Nullable String specific) throws Exception {
      boolean criticalCrash = false;

      try {
         if (instance == null) {
            instance = new HandlerUtil();
         }

         boolean found_listener = false;
         ConcurrentHashMap<Object, ArrayList<IEventListener>> listeners = (ConcurrentHashMap)instance.f_listeners.get(MinecraftForge.EVENT_BUS);
         Object handler = null;

         label52:
         for(Entry<Object, ArrayList<IEventListener>> listener_entry : listeners.entrySet()) {
            handler = listener_entry.getKey();
            String handlerName = handler.getClass().getName();
            if (handlerName.equals("java.lang.Class")) {
               handlerName = ((Class)handler).getName();
            }

            if (handlerName.equals(name)) {
               found_listener = true;
               if (specific == null) {
                  MinecraftForge.EVENT_BUS.unregister(handler);
                  CommonProxy.Logger.log(Level.INFO, "Found and removed " + name + " from the event bus");
                  return handler;
               }

               ArrayList<IEventListener> eventListeners = (ArrayList)listener_entry.getValue();
               Iterator<IEventListener> elIterator = eventListeners.iterator();

               IEventListener eventListener;
               while(true) {
                  if (!elIterator.hasNext()) {
                     continue label52;
                  }

                  eventListener = (IEventListener)elIterator.next();
                  if (eventListener.toString().contains(specific)) {
                     break;
                  }
               }

               criticalCrash = true;
               elIterator.remove();
               int busID = instance.f_busID.getInt(MinecraftForge.EVENT_BUS);
               ListenerList.unregisterAll(busID, eventListener);
               CommonProxy.Logger.log(Level.INFO, "Found and removed " + name + " IEventListener " + specific + " from the event bus");
               return eventListener;
            }
         }

         return null;
      } catch (Exception var13) {
         if (criticalCrash) {
            throw new RuntimeException(var13);
         } else {
            throw var13;
         }
      }
   }
}

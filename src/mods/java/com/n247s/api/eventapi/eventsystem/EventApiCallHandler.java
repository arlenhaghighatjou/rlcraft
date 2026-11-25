package com.n247s.api.eventapi.eventsystem;

import com.n247s.api.eventapi.EventApi;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public class EventApiCallHandler extends CallHandler {
   private static final Logger log = EventApi.logger;

   public EventApiCallHandler(Class<? extends EventType> eventType) {
      super(eventType);
   }

   protected boolean CallInstances(EventType eventType) {
      boolean isCanceled = false;

      try {
         isCanceled = this.CallInstancesInOrder(eventType);
      } catch (Exception var4) {
         log.catching(var4);
      }

      return isCanceled;
   }

   protected boolean CallInstancesWithBlackList(EventType eventTypeInstance, List blackList) {
      try {
         for(int i = 0; i < 5; ++i) {
            HashMap linkedHashMap = (HashMap)this.instanceMap.get(CustomEventSubscribe.Priority.getPriorityInOrder(i));
            if (linkedHashMap != null) {
               for(Object originalEntry : linkedHashMap.keySet()) {
                  if (blackList.contains(originalEntry)) {
                     blackList.remove(originalEntry);
                  } else {
                     this.callEventMethod((Method)linkedHashMap.get(originalEntry), originalEntry, eventTypeInstance);
                  }
               }
            }
         }

         if (blackList.size() > 0) {
            log.error("Not all Objects from the blackList can be resolved!, report this to the modAuthor!");
         }
      } catch (Exception var7) {
         log.catching(var7);
      }

      return eventTypeInstance.isCanceled();
   }

   protected boolean CallInstancesInOrder(EventType eventTypeInstance) {
      try {
         for(int i = 0; i < 5; ++i) {
            LinkedHashMap linkedHashMap = (LinkedHashMap)this.instanceMap.get(CustomEventSubscribe.Priority.getPriorityInOrder(i));
            if (linkedHashMap != null) {
               for(Object originalEntry : linkedHashMap.keySet()) {
                  this.callEventMethod((Method)linkedHashMap.get(originalEntry), originalEntry, eventTypeInstance);
               }
            }
         }
      } catch (Exception var6) {
         if (!(var6 instanceof ConcurrentModificationException)) {
            log.catching(var6);
         }
      }

      return eventTypeInstance.isCanceled();
   }

   protected boolean CallInstancesInOrder(EventType eventTypeInstance, List orderList) {
      List calledInstanceList = new ArrayList();
      if (orderList != null) {
         try {
            boolean containsMethod = false;

            for(int i = 0; i < orderList.size(); ++i) {
               Object originalEntry = orderList.get(i);
               if (this.entryCheckList.contains(originalEntry)) {
                  Class clazz;
                  if (!(originalEntry instanceof Class)) {
                     clazz = originalEntry.getClass();
                  } else {
                     clazz = (Class)originalEntry;
                  }

                  Method[] methodArray = clazz.getMethods();

                  for(int j = 0; j < methodArray.length; ++j) {
                     Method currentMethod = methodArray[j];
                     if (currentMethod.isAnnotationPresent(CustomEventSubscribe.class)) {
                        if (currentMethod.getParameterTypes().length > 1) {
                           log.catching(new IllegalArgumentException("An CustomEventSubScribed Method Can't have more than one Parameter!"));
                        }

                        if (originalEntry instanceof Class && !Modifier.isStatic(currentMethod.getModifiers())) {
                           log.catching(new IllegalArgumentException("An CustomEventSubScribed Method Can't be non-static if you register an Class Object!"));
                        }

                        if (this.eventType.isAssignableFrom(currentMethod.getParameterTypes()[0])) {
                           this.callEventMethod(currentMethod, originalEntry, eventTypeInstance);
                           containsMethod = true;
                        } else {
                           log.catching(new IllegalArgumentException("The Parameter of a CustomEventSubscribed method isn't an EventType!"));
                        }
                     }
                  }

                  if (containsMethod) {
                     containsMethod = false;
                  } else {
                     log.catching(new IllegalArgumentException("Class " + clazz.getName() + " doesn't contain an eventMethod!"));
                  }
               }
            }

            calledInstanceList.addAll(orderList);
         } catch (Exception var11) {
            log.catching(var11);
         }
      } else {
         this.CallInstancesInOrder(eventTypeInstance);
      }

      return eventTypeInstance.isCanceled();
   }

   protected boolean CallInstancesInOrderWithBlackList(EventType eventTypeInstance, List orderList, List blackList) {
      List calledInstanceList = new ArrayList();

      for(int i = 0; i < blackList.size(); ++i) {
         Object current = blackList.get(i);
         if (orderList.contains(current)) {
            orderList.remove(current);
         }
      }

      if (orderList != null) {
         try {
            boolean containsMethod = false;

            for(int i = 0; i < orderList.size(); ++i) {
               Object originalEntry = orderList.get(i);
               if (this.entryCheckList.contains(originalEntry)) {
                  Class clazz;
                  if (!(originalEntry instanceof Class)) {
                     clazz = originalEntry.getClass();
                  } else {
                     clazz = (Class)originalEntry;
                  }

                  Method[] methodArray = clazz.getMethods();

                  for(int j = 0; j < methodArray.length; ++j) {
                     Method currentMethod = methodArray[j];
                     if (currentMethod.isAnnotationPresent(CustomEventSubscribe.class)) {
                        if (currentMethod.getParameterTypes().length > 1) {
                           log.catching(new IllegalArgumentException("An CustomEventSubScribed Method Can't have more than one Parameter!"));
                        }

                        if (originalEntry instanceof Class && !Modifier.isStatic(currentMethod.getModifiers())) {
                           log.catching(new IllegalArgumentException("An CustomEventSubScribed Method Can't be non-static if you register an Class Object!"));
                        }

                        if (this.eventType.isAssignableFrom(currentMethod.getParameterTypes()[0])) {
                           this.callEventMethod(currentMethod, originalEntry, eventTypeInstance);
                           containsMethod = true;
                        } else {
                           log.catching(new IllegalArgumentException("The Parameter of a CustomEventSubscribed method isn't an EventType!"));
                        }
                     }
                  }

                  if (containsMethod) {
                     containsMethod = false;
                  } else {
                     log.catching(new IllegalArgumentException("Class " + clazz.getName() + " doesn't contain an eventMethod!"));
                  }
               }
            }

            calledInstanceList.addAll(orderList);
         } catch (Exception var12) {
            log.catching(var12);
         }
      } else {
         this.CallInstancesWithBlackList(eventTypeInstance, blackList);
      }

      if (blackList.size() > 0) {
         calledInstanceList.addAll(blackList);
      }

      return eventTypeInstance.isCanceled();
   }

   protected boolean CallInstancesWithWhiteList(EventType eventTypeInstance, List whiteList) {
      try {
         boolean containsMethod = false;

         for(int i = 0; i < whiteList.size(); ++i) {
            Object originalEntry = whiteList.get(i);
            if (this.entryCheckList.contains(originalEntry)) {
               Class clazz;
               if (!(originalEntry instanceof Class)) {
                  clazz = originalEntry.getClass();
               } else {
                  clazz = (Class)originalEntry;
               }

               Method[] methodArray = clazz.getMethods();

               for(int j = 0; j < methodArray.length; ++j) {
                  Method currentMethod = methodArray[j];
                  if (currentMethod.isAnnotationPresent(CustomEventSubscribe.class)) {
                     if (currentMethod.getParameterTypes().length > 1) {
                        log.catching(new IllegalArgumentException("An CustomEventSubScribed Method Can't have more than one Parameter!"));
                     }

                     if (originalEntry instanceof Class && !Modifier.isStatic(currentMethod.getModifiers())) {
                        log.catching(new IllegalArgumentException("An CustomEventSubScribed Method Can't be non-static if you register an Class Object!"));
                     }

                     if (this.eventType.isAssignableFrom(currentMethod.getParameterTypes()[0])) {
                        this.callEventMethod(currentMethod, originalEntry, eventTypeInstance);
                        containsMethod = true;
                     } else {
                        log.catching(new IllegalArgumentException("The Parameter of a CustomEventSubscribed method isn't an EventType!"));
                     }
                  }
               }

               if (containsMethod) {
                  containsMethod = false;
               } else {
                  log.catching(new IllegalArgumentException("Class " + clazz.getName() + " doesn't contain an eventMethod!"));
               }
            }
         }
      } catch (Exception var10) {
         log.catching(var10);
      }

      return eventTypeInstance.isCanceled();
   }

   protected void callEventMethod(Method method, Object instance, Object... methodArguments) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      method.setAccessible(true);
      method.invoke(instance, methodArguments);
   }
}

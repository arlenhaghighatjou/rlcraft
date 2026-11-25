package com.charles445.aireducer.routine;

import com.charles445.aireducer.AIReducer;
import com.charles445.aireducer.ai.WrappedTask;
import com.charles445.aireducer.reflect.ReflectorMinecraft;
import com.charles445.aireducer.util.ErrorUtil;
import com.google.common.base.Function;
import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.pathfinding.PathNavigate;

public abstract class Routine {
   private Map<Class, Constructor> wrappedConstructorMap = new ConcurrentHashMap();

   public Routine() {
   }

   public abstract boolean canRun();

   public void runRoutine(EntityLiving entity, String domain, String path) {
      if (this.canRun()) {
         this.run(entity, domain, path);
      }

   }

   protected abstract void run(EntityLiving var1, String var2, String var3);

   protected void applyTickRate(EntityAITasks tasks, int tickRate) {
      if (ReflectorMinecraft.reflector != null) {
         try {
            ReflectorMinecraft.reflector.f_EntityAITasks_tickRate.setInt(tasks, tickRate);
         } catch (Exception var4) {
            ;
         }

      }
   }

   protected void setNavigator(EntityLiving entity, PathNavigate navigator) {
      if (ReflectorMinecraft.reflector != null) {
         try {
            ReflectorMinecraft.reflector.f_EntityLiving_navigator.set(entity, navigator);
         } catch (Exception var4) {
            ;
         }

      }
   }

   protected boolean removeAllTasksOfClass(EntityLiving entity, Class classToRemove) {
      boolean found = false;
      Iterator<EntityAITaskEntry> iterator = entity.tasks.taskEntries.iterator();

      while(iterator.hasNext()) {
         EntityAITaskEntry task = (EntityAITaskEntry)iterator.next();
         if (task.action.getClass() == classToRemove) {
            ErrorUtil.debugDebug("Removing " + classToRemove.getName() + " from " + entity.getClass().getName());
            found = true;
            iterator.remove();
         }
      }

      return found;
   }

   protected int tryAndReplaceAllTasks(EntityLiving entity, EntityAITasks tasks, @Nullable Class toMatch, Function<EntityAITaskEntry, EntityAIBase> action) {
      int count = 0;
      LinkedList<EntityAITaskEntry> entries = new LinkedList();

      for(EntityAITaskEntry entry : tasks.taskEntries) {
         if (toMatch != null && toMatch != entry.action.getClass()) {
            entries.add(entry);
         } else {
            EntityAIBase newAI = (EntityAIBase)action.apply(entry);
            if (newAI != null) {
               tasks.getClass();
               entries.add(new EntityAITaskEntry(tasks, entry.priority, (EntityAIBase)action.apply(entry)));
               ++count;
            } else {
               ErrorUtil.debugError("tryAndReplaceAllTasks ERROR " + entity.getClass().getName() + " " + (toMatch == null ? "any" : toMatch.getClass().getName()));
               entries.add(entry);
            }
         }
      }

      if (count > 0) {
         ErrorUtil.debugDebug("Replacing " + count + " tasks of " + (toMatch == null ? "any" : toMatch.getName()));
         tasks.taskEntries.clear();
         tasks.taskEntries.addAll(entries);
      }

      return count;
   }

   protected void wrapTask(EntityLiving entity, EntityAITasks tasks, @Nullable Class clazz, Class<? extends WrappedTask> wrapperClazz) {
      this.tryAndReplaceAllTasks(entity, tasks, clazz, (oldTaskEntry) -> {
         try {
            Constructor construct = (Constructor)this.wrappedConstructorMap.get(wrapperClazz);
            if (construct == null) {
               AIReducer.logger.info("Caching Wrapped Task Constructor: " + wrapperClazz.getName());
               construct = wrapperClazz.getDeclaredConstructor(EntityLiving.class, EntityAIBase.class);
               this.wrappedConstructorMap.put(wrapperClazz, construct);
            }

            WrappedTask wrapper = (WrappedTask)construct.newInstance(entity, oldTaskEntry.action);
            ErrorUtil.debugDebug("Wrapped task with priority " + oldTaskEntry.priority + ": " + oldTaskEntry.action.getClass().getName() + " " + wrapper.getClass().getName());
            return (void)wrapper;
         } catch (Exception var6) {
            AIReducer.logger.error("Failed to reflect to requested WrappedTask constructor!", var6);
            return (void)oldTaskEntry.action;
         }
      });
   }

   protected void debugPrintTasks(EntityAITasks tasks) {
      for(EntityAITaskEntry entry : tasks.taskEntries) {
         ErrorUtil.debugDebug("[" + entry.priority + ", " + entry.action.getClass().getName() + "]");
      }

   }
}

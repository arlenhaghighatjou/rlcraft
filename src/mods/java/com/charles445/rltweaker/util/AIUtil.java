package com.charles445.rltweaker.util;

import com.google.common.base.Function;
import java.util.LinkedList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;

public class AIUtil {
   public AIUtil() {
   }

   public static int tryAndReplaceAllTasks(EntityLiving entity, EntityAITasks tasks, Class toMatch, Function<EntityAITaskEntry, EntityAIBase> action) {
      int count = 0;
      LinkedList<EntityAITaskEntry> entries = new LinkedList();

      for(EntityAITaskEntry entry : tasks.taskEntries) {
         if (toMatch == entry.action.getClass()) {
            EntityAIBase newAI = (EntityAIBase)action.apply(entry);
            if (newAI != null) {
               tasks.getClass();
               entries.add(new EntityAITaskEntry(tasks, entry.priority, (EntityAIBase)action.apply(entry)));
               ++count;
            } else {
               ErrorUtil.logSilent("tryAndReplaceAllTasks " + entity.getClass().getName() + " " + toMatch.getClass().getName());
               entries.add(entry);
            }
         } else {
            entries.add(entry);
         }
      }

      if (count > 0) {
         tasks.taskEntries.clear();
         tasks.taskEntries.addAll(entries);
      }

      return count;
   }
}

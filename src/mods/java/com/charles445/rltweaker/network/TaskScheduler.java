package com.charles445.rltweaker.network;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TaskScheduler {
   public static List<TaskScheduler.ScheduledTask> clientTasks = new ArrayList();
   private static long taskTimer = 0L;

   public TaskScheduler() {
   }

   public static void addClientTask(IMessage message, long delay) {
      clientTasks.add(new TaskScheduler.ScheduledTask(message, taskTimer + delay));
   }

   @SideOnly(Side.CLIENT)
   private static void executeClientTask(IMessage message) {
      if (message instanceof MessageUpdateEntityMovement) {
         MessageUpdateEntityMovement.Handler.fromMessage((MessageUpdateEntityMovement)message);
      }

   }

   @SideOnly(Side.CLIENT)
   public static void processClientTick(ClientTickEvent event) {
      if (event.phase != Phase.END) {
         if (!clientTasks.isEmpty()) {
            Iterator<TaskScheduler.ScheduledTask> it = clientTasks.iterator();

            while(it.hasNext()) {
               TaskScheduler.ScheduledTask task = (TaskScheduler.ScheduledTask)it.next();
               if (task.time <= taskTimer) {
                  executeClientTask(task.message);
                  it.remove();
               }
            }
         }

         ++taskTimer;
      }
   }

   public static class ScheduledTask {
      public IMessage message;
      public long time;

      public ScheduledTask(IMessage message, long time) {
         this.message = message;
         this.time = time;
      }
   }
}

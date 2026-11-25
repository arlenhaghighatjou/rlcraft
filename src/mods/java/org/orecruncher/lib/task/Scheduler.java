package org.orecruncher.lib.task;

import com.google.common.util.concurrent.ListenableFuture;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import org.orecruncher.LibBase;

public final class Scheduler {
   private Scheduler() {
   }

   public static ListenableFuture<Object> schedule(@Nonnull Side side, @Nonnull Runnable task) {
      IThreadListener tl = (IThreadListener)(side == Side.SERVER ? FMLCommonHandler.instance().getMinecraftServerInstance() : Minecraft.getMinecraft());
      return tl == null ? null : tl.addScheduledTask(task);
   }

   public static void scheduleDeferred(@Nonnull Side side, @Nonnull Runnable task) {
      try {
         Thread t = new Thread(() -> {
            schedule(side, task);
         });
         t.start();
         t.join();
      } catch (Throwable var3) {
         LibBase.log().error("Error executing deferred task", var3);
      }

   }
}

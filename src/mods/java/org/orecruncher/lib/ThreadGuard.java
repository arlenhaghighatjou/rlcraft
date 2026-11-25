package org.orecruncher.lib;

import javax.annotation.Nonnull;
import net.minecraftforge.fml.relauncher.Side;
import org.orecruncher.lib.logging.ModLog;

public final class ThreadGuard {
   public static final String CLIENT_THREAD = "Client thread";
   public static final String SERVER_THREAD = "Server thread";
   private final String threadName;
   private final String context;
   private final ModLog log;
   private ThreadGuard.Action action;

   public ThreadGuard(@Nonnull ModLog log, @Nonnull Side side, @Nonnull String context) {
      this(log, side == Side.CLIENT ? "Client thread" : "Server thread", context);
   }

   public ThreadGuard(@Nonnull ModLog log, @Nonnull String threadName, @Nonnull String context) {
      this.action = ThreadGuard.Action.LOG;
      this.log = log;
      this.threadName = threadName;
      this.context = context;
   }

   public ThreadGuard setAction(@Nonnull ThreadGuard.Action action) {
      this.action = action;
      return this;
   }

   public void check(@Nonnull String locus) {
      if (this.action != ThreadGuard.Action.NONE) {
         String name = Thread.currentThread().getName();
         if (!name.equals(this.threadName)) {
            String txt = String.format("[%s::%s] illegal access by thread [%s]!", this.context, locus, name);
            if (this.action != ThreadGuard.Action.LOG) {
               throw new RuntimeException(txt);
            }

            this.log.error(txt, new Throwable());
         }

      }
   }

   public static enum Action {
      NONE,
      LOG,
      EXCEPTION;

      private Action() {
      }
   }
}

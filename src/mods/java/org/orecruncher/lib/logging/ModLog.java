package org.orecruncher.lib.logging;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public class ModLog {
   private static final Map<String, ModLog> loggers = Maps.newHashMap();
   private final Logger logger;
   private boolean DEBUGGING;
   private int traceMask;
   public static final ModLog NULL_LOGGER = new ModLog((Logger)null) {
      public ModLog setDebug(boolean flag) {
         return this;
      }

      public ModLog setTraceMask(int mask) {
         return this;
      }

      public boolean isDebugging() {
         return false;
      }

      public void info(@Nonnull String msg, @Nullable Object... parms) {
      }

      public void warn(@Nonnull String msg, @Nullable Object... parms) {
      }

      public void debug(@Nonnull String msg, @Nullable Object... parms) {
      }

      public void debug(int mask, @Nonnull String msg, @Nullable Object... parms) {
      }

      public void error(@Nonnull String msg, @Nonnull Throwable e) {
      }

      public void catching(@Nonnull Throwable t) {
      }
   };

   private ModLog(@Nonnull Logger logger) {
      this.logger = logger;
      this.DEBUGGING = false;
      this.traceMask = 0;
   }

   @Nonnull
   private String[] formatLines(@Nonnull String format, @Nullable Object... parms) {
      return StringUtils.split(String.format(format, parms), '\n');
   }

   public static ModLog getLogger(@Nonnull String id) {
      ModLog result = (ModLog)loggers.get(id);
      return result != null ? result : NULL_LOGGER;
   }

   public static ModLog setLogger(@Nonnull String id, @Nonnull Logger log) {
      ModLog result = log == null ? NULL_LOGGER : new ModLog(log);
      loggers.put(id, result);
      return result;
   }

   public ModLog setDebug(boolean flag) {
      this.DEBUGGING = flag;
      return this;
   }

   public ModLog setTraceMask(int mask) {
      this.traceMask = mask;
      return this;
   }

   public boolean testTrace(int mask) {
      return (this.traceMask & mask) != 0;
   }

   public boolean isDebugging() {
      return this.DEBUGGING;
   }

   public void info(@Nonnull String msg, @Nullable Object... parms) {
      for(String s : this.formatLines(msg, parms)) {
         this.logger.info(s);
      }

   }

   public void warn(@Nonnull String msg, @Nullable Object... parms) {
      for(String s : this.formatLines(msg, parms)) {
         this.logger.warn(s);
      }

   }

   public void debug(@Nonnull String msg, @Nullable Object... parms) {
      if (this.DEBUGGING) {
         for(String s : this.formatLines(msg, parms)) {
            this.logger.info(s);
         }
      }

   }

   public void debug(int mask, @Nonnull String msg, @Nullable Object... parms) {
      if (this.testTrace(mask)) {
         this.debug(msg, parms);
      }

   }

   public void error(@Nonnull String msg, @Nonnull Throwable e) {
      for(String s : this.formatLines(msg)) {
         this.logger.error(s);
      }

      e.printStackTrace();
   }

   public void catching(@Nonnull Throwable t) {
      this.logger.catching(t);
      t.printStackTrace();
   }
}

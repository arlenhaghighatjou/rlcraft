package dev.satyrn.wolfarmor.util;

import dev.satyrn.wolfarmor.api.util.ILogHelper;
import javax.annotation.Nonnull;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import org.apache.logging.log4j.LoggingException;

public class LogHelper implements ILogHelper {
   private Logger logger;

   public LogHelper() {
   }

   LogHelper(@Nonnull Logger logger) {
      this.initializeLogger(logger);
   }

   public void initializeLogger(Logger logger) {
      this.logger = logger;
   }

   public void log(@Nonnull Level level, @Nonnull String message) {
      if (this.logger == null) {
         throw new LoggingException("Logger has not been initialized");
      } else {
         this.logger.log(level, message);
      }
   }

   public void log(@Nonnull Level level, @Nonnull Throwable ex) {
      this.log(level, ex, level);
   }

   public void log(@Nonnull Level level, @Nonnull Throwable ex, @Nonnull Level stackTraceLevel) {
      this.log(level, ex.getMessage());

      for(StackTraceElement stackTraceElement : ex.getStackTrace()) {
         this.log(stackTraceLevel, stackTraceElement);
      }

   }

   public void log(@Nonnull Level level, @Nonnull Object obj) {
      this.log(level, obj.toString());
   }

   public void info(@Nonnull String message) {
      this.log(Level.INFO, message);
   }

   public void warning(@Nonnull String message) {
      this.log(Level.WARN, message);
   }

   public void warning(@Nonnull Exception ex) {
      this.log(Level.WARN, ex, Level.DEBUG);
   }

   public void error(@Nonnull String message) {
      this.log(Level.ERROR, message);
   }

   public void error(@Nonnull Exception ex) {
      this.log(Level.ERROR, ex, Level.DEBUG);
   }

   public void fatal(@Nonnull String message) {
      this.log(Level.FATAL, message);
   }

   public void fatal(@Nonnull Throwable ex) {
      this.log(Level.FATAL, ex, Level.DEBUG);
   }

   public void debug(@Nonnull String message) {
      this.log(Level.DEBUG, message);
   }

   public void debug(@Nonnull Object obj) {
      this.log(Level.DEBUG, obj);
   }

   public void trace(@Nonnull String message) {
      this.log(Level.TRACE, message);
   }

   public void trace(@Nonnull Object obj) {
      this.log(Level.TRACE, obj);
   }
}

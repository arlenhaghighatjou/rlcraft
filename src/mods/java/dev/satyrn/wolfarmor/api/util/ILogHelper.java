package dev.satyrn.wolfarmor.api.util;

import javax.annotation.Nonnull;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

public interface ILogHelper {
   void initializeLogger(Logger var1);

   void log(@Nonnull Level var1, @Nonnull String var2);

   void log(@Nonnull Level var1, @Nonnull Throwable var2);

   void log(@Nonnull Level var1, @Nonnull Throwable var2, @Nonnull Level var3);

   void log(@Nonnull Level var1, @Nonnull Object var2);

   void info(@Nonnull String var1);

   void warning(@Nonnull String var1);

   void warning(@Nonnull Exception var1);

   void error(@Nonnull String var1);

   void error(@Nonnull Exception var1);

   void fatal(@Nonnull String var1);

   void fatal(@Nonnull Throwable var1);

   void debug(@Nonnull String var1);

   void debug(@Nonnull Object var1);

   void trace(@Nonnull String var1);

   void trace(@Nonnull Object var1);
}

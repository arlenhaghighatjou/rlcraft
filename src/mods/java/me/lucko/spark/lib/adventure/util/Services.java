package me.lucko.spark.lib.adventure.util;

import java.util.Iterator;
import java.util.Optional;
import java.util.ServiceLoader;
import org.jetbrains.annotations.NotNull;

public final class Services {
   private static final boolean SERVICE_LOAD_FAILURES_ARE_FATAL = Boolean.parseBoolean(System.getProperty(String.join(".", "net", "kyori", "adventure", "serviceLoadFailuresAreFatal"), String.valueOf(true)));

   private Services() {
   }

   @NotNull
   public static <P> Optional<P> service(@NotNull Class<P> type) {
      ServiceLoader<P> loader = Services0.<P>loader(type);
      Iterator<P> it = loader.iterator();

      while(true) {
         if (it.hasNext()) {
            P instance;
            try {
               instance = (P)it.next();
            } catch (Throwable var5) {
               if (!SERVICE_LOAD_FAILURES_ARE_FATAL) {
                  continue;
               }

               throw new IllegalStateException("Encountered an exception loading service " + type, var5);
            }

            if (it.hasNext()) {
               throw new IllegalStateException("Expected to find one service " + type + ", found multiple");
            }

            return Optional.of(instance);
         }

         return Optional.empty();
      }
   }
}

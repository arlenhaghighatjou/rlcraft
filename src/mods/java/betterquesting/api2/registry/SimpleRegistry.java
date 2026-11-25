package betterquesting.api2.registry;

import betterquesting.core.BetterQuesting;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.Callable;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;

public class SimpleRegistry<T> {
   private final HashMap<ResourceLocation, Callable<T>> factories = new HashMap();

   public SimpleRegistry() {
   }

   public void register(@Nonnull ResourceLocation idname, @Nonnull Callable<T> factory) {
      if (this.factories.containsKey(idname)) {
         throw new IllegalArgumentException("Cannot register duplicate factory or registry name");
      } else {
         this.factories.put(idname, factory);
      }
   }

   @Nullable
   public T createNew(@Nonnull ResourceLocation idName) {
      Callable<T> fact = (Callable)this.factories.get(idName);

      try {
         return (T)(fact == null ? null : fact.call());
      } catch (Exception var4) {
         BetterQuesting.logger.error("Registry failed to instantiate new object with ID: " + idName.toString(), var4);
         return (T)null;
      }
   }

   public Set<ResourceLocation> getAll() {
      return Collections.unmodifiableSet(this.factories.keySet());
   }
}

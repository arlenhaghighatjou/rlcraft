package betterquesting.api2.registry;

import betterquesting.core.BetterQuesting;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;

public class FunctionRegistry<T, E> {
   private final HashMap<ResourceLocation, Function<E, T>> factories = new HashMap();
   private final HashMap<ResourceLocation, E> def_args = new HashMap();

   public FunctionRegistry() {
   }

   public void register(@Nonnull ResourceLocation idname, @Nonnull Function<E, T> factory, E template) {
      if (this.factories.containsKey(idname)) {
         throw new IllegalArgumentException("Cannot register duplicate factory or registry name");
      } else {
         this.factories.put(idname, factory);
         this.def_args.put(idname, template);
      }
   }

   @Nullable
   public T createNew(@Nonnull ResourceLocation idName) {
      E arg = (E)this.def_args.get(idName);
      if (arg != null) {
         return (T)this.createNew(idName, arg);
      } else {
         BetterQuesting.logger.error("Registry failed to instantiate new object with ID: " + idName.toString());
         return (T)null;
      }
   }

   @Nullable
   public T createNew(@Nonnull ResourceLocation idName, @Nonnull E info) {
      Function<E, T> fact = (Function)this.factories.get(idName);

      try {
         return (T)(fact == null ? null : fact.apply(info));
      } catch (Exception var5) {
         BetterQuesting.logger.error("Registry failed to instantiate new object with ID: " + idName.toString(), var5);
         return (T)null;
      }
   }

   @Nullable
   public E getTemplate(@Nonnull ResourceLocation idname) {
      return (E)this.def_args.get(idname);
   }

   @Nonnull
   public Set<ResourceLocation> getAll() {
      return Collections.unmodifiableSet(this.factories.keySet());
   }
}

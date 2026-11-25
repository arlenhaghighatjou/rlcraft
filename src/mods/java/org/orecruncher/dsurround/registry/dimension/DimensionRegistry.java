package org.orecruncher.dsurround.registry.dimension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.world.World;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.registry.Registry;
import org.orecruncher.dsurround.registry.config.DimensionConfig;
import org.orecruncher.dsurround.registry.config.ModConfiguration;

public final class DimensionRegistry extends Registry {
   private final List<DimensionConfig> cache = new ArrayList();

   public DimensionRegistry() {
      super("Dimension Registry");
   }

   protected void preInit() {
      this.cache.clear();
   }

   protected void init(@Nonnull ModConfiguration cfg) {
      cfg.dimensions.forEach(this::register);
   }

   protected void complete() {
      if (ModOptions.logging.enableDebugLogging) {
         ModBase.log().info("*** DIMENSION REGISTRY (cache) ***", new Object[0]);
         this.cache.stream().map(Object::toString).forEach((x$0) -> {
            rec$.info(x$0, new Object[0]);
         });
      }

   }

   public void loading(@Nonnull World world) {
      this.getData(world);
   }

   @Nonnull
   private DimensionConfig getData(@Nonnull DimensionConfig entry) {
      Optional<DimensionConfig> result = this.cache.stream().filter((e) -> {
         return e.equals(entry);
      }).findFirst();
      if (result.isPresent()) {
         return (DimensionConfig)result.get();
      } else {
         this.cache.add(entry);
         return entry;
      }
   }

   public void register(@Nonnull DimensionConfig entry) {
      if (entry.dimensionId != null || entry.name != null) {
         DimensionConfig data = this.getData(entry);
         if (data == entry) {
            return;
         }

         if (data.dimensionId == null) {
            data.dimensionId = entry.dimensionId;
         }

         if (data.name == null) {
            data.name = entry.name;
         }

         if (entry.hasAurora != null) {
            data.hasAurora = entry.hasAurora;
         }

         if (entry.hasHaze != null) {
            data.hasHaze = entry.hasHaze;
         }

         if (entry.hasWeather != null) {
            data.hasWeather = entry.hasWeather;
         }

         if (entry.cloudHeight != null) {
            data.cloudHeight = entry.cloudHeight;
         }

         if (entry.seaLevel != null) {
            data.seaLevel = entry.seaLevel;
         }

         if (entry.skyHeight != null) {
            data.skyHeight = entry.skyHeight;
         }
      }

   }

   @Nullable
   public DimensionConfig getData(@Nonnull World world) {
      synchronized(this) {
         for(DimensionConfig e : this.cache) {
            if (e.dimensionId != null && e.dimensionId.intValue() == world.provider.getDimension() || e.name != null && e.name.equals(world.provider.getDimensionType().getName())) {
               return e;
            }
         }

         return null;
      }
   }
}

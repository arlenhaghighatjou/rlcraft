package com.teamacronymcoders.base.util;

import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RegistryUtils {
   public RegistryUtils() {
   }

   public static <T extends IForgeRegistryEntry<T>> T getEntry(IForgeRegistry<T> registry, ResourceLocation resource) {
      Optional<T> value = Optional.ofNullable(registry.getValue(resource));
      return (T)(value.orElseThrow(() -> {
         return (T)(new IllegalArgumentException("Cannot get Entry for name: " + resource));
      }));
   }
}

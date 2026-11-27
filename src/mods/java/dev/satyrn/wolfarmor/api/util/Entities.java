package dev.satyrn.wolfarmor.api.util;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ObjectHolderRegistry;
import net.minecraftforge.fml.common.registry.EntityEntry;

public abstract class Entities {
   /** @deprecated */
   @Deprecated
   public static EntityEntry WOLF_ARMORED = null;

   public Entities() {
   }

   public static void registerHolders() {
      ObjectHolderRegistry.INSTANCE.register(EntityEntry.class, new ResourceLocation("wolfarmor:wolf_armored"), v -> WOLF_ARMORED = v);
   }
}

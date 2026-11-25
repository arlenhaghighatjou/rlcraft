package dev.satyrn.wolfarmor.api.util;

import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder("wolfarmor")
public abstract class Entities {
   /** @deprecated */
   @ObjectHolder("wolf_armored")
   @Deprecated
   public static final EntityEntry WOLF_ARMORED = null;

   public Entities() {
   }
}

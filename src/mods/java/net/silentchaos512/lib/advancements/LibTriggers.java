package net.silentchaos512.lib.advancements;

import net.minecraft.advancements.CriteriaTriggers;

public class LibTriggers {
   public static final UseItemTrigger USE_ITEM = (UseItemTrigger)CriteriaTriggers.register(new UseItemTrigger());
   public static final GenericIntTrigger GENERIC_INT = (GenericIntTrigger)CriteriaTriggers.register(new GenericIntTrigger());

   public LibTriggers() {
   }

   public static void init() {
   }
}

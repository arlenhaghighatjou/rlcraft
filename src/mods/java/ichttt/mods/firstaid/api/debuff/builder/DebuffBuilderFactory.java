package ichttt.mods.firstaid.api.debuff.builder;

import javax.annotation.Nonnull;

public abstract class DebuffBuilderFactory {
   private static DebuffBuilderFactory instance;

   public DebuffBuilderFactory() {
   }

   public static void setInstance(DebuffBuilderFactory factory) {
      instance = factory;
   }

   public static DebuffBuilderFactory getInstance() {
      return instance;
   }

   @Nonnull
   public abstract IDebuffBuilder newOnHitDebuffBuilder(@Nonnull String var1);

   @Nonnull
   public abstract IDebuffBuilder newConstantDebuffBuilder(@Nonnull String var1);
}

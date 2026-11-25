package com.ferreusveritas.dynamictrees.api.substances;

import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ISubstanceEffect {
   /** @deprecated */
   @Deprecated
   default boolean apply(World world, BlockPos rootPos) {
      return this.apply(world, rootPos, BlockPos.ORIGIN).success();
   }

   ISubstanceEffect.Result apply(World var1, BlockPos var2, BlockPos var3);

   boolean update(World var1, BlockPos var2, int var3, int var4);

   String getName();

   boolean isLingering();

   public static class Result {
      @Nullable
      private final String errorKey;
      private final Object[] args;

      public Result(@Nullable String errorKey, Object... args) {
         this.errorKey = errorKey;
         this.args = args;
      }

      @Nullable
      public String getErrorKey() {
         return this.errorKey;
      }

      public Object[] getArgs() {
         return this.args;
      }

      public boolean success() {
         return this.errorKey == null;
      }

      public static ISubstanceEffect.Result successful() {
         return new ISubstanceEffect.Result((String)null, new Object[0]);
      }

      public static ISubstanceEffect.Result failure(String errorKey, Object... args) {
         return new ISubstanceEffect.Result(errorKey, args);
      }

      public static ISubstanceEffect.Result failure() {
         return new ISubstanceEffect.Result("", new Object[0]);
      }
   }
}

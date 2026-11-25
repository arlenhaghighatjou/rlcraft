package org.orecruncher.lib;

import javax.annotation.Nonnull;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

public abstract class SideLocal<T> {
   private final Object[] sideData = new Object[Side.values().length];

   public SideLocal() {
   }

   protected abstract T initialValue(@Nonnull Side var1);

   public final T get() {
      return (T)this.get(FMLCommonHandler.instance().getEffectiveSide());
   }

   public final T get(@Nonnull Side side) {
      int idx = side.ordinal();
      Object result = this.sideData[idx];
      if (result == null) {
         result = this.sideData[idx] = this.initialValue(side);
      }

      return (T)result;
   }

   public final void clear() {
      this.clear(FMLCommonHandler.instance().getEffectiveSide());
   }

   public final void clear(@Nonnull Side side) {
      this.sideData[side.ordinal()] = null;
   }

   public final boolean hasValue() {
      return this.hasValue(FMLCommonHandler.instance().getEffectiveSide());
   }

   public final boolean hasValue(@Nonnull Side side) {
      return this.sideData[side.ordinal()] != null;
   }
}

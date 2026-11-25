package com.endertech.common;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class CachedValue<V> {
   protected V cachedValue;
   protected CommonTime.Stamp lastUpdate;
   protected final CommonTime.Interval updateInterval;
   protected final Supplier<V> valueSupplier;

   public static <V> CachedValue<V> of(Supplier<V> valueSupplier, CommonTime.Interval updateInterval) {
      return new CachedValue<V>(updateInterval, valueSupplier);
   }

   /** @deprecated */
   @Deprecated
   public CachedValue(V initialValue, CommonTime.Interval updateInterval) {
      this(updateInterval, () -> {
         return (void)initialValue;
      });
   }

   public CachedValue(CommonTime.Interval updateInterval, Supplier<V> valueSupplier) {
      this.cachedValue = null;
      this.lastUpdate = CommonTime.Stamp.now();
      this.updateInterval = updateInterval;
      this.valueSupplier = valueSupplier;
   }

   protected boolean requiresUpdate() {
      return this.cachedValue == null || CommonTime.Interval.passedFrom(this.lastUpdate).moreThan(this.updateInterval);
   }

   protected V updateAndReturn(V value) {
      this.cachedValue = value;
      this.lastUpdate = CommonTime.Stamp.now();
      return this.cachedValue;
   }

   public V get() {
      return (V)(this.requiresUpdate() ? this.updateAndReturn(this.valueSupplier.get()) : this.cachedValue);
   }

   public <P1, P2> V get(BiFunction<P1, P2, V> updatedValue, P1 param1, P2 param2) {
      return (V)(this.requiresUpdate() ? this.updateAndReturn(updatedValue.apply(param1, param2)) : this.cachedValue);
   }

   public <P> V get(Function<P, V> updatedValue, P param) {
      return (V)(this.requiresUpdate() ? this.updateAndReturn(updatedValue.apply(param)) : this.cachedValue);
   }
}

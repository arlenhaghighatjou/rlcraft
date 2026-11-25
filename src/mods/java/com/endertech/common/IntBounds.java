package com.endertech.common;

import java.util.Iterator;
import java.util.Objects;

public class IntBounds implements IBounds<Integer>, Iterable<Integer> {
   public static final IntBounds ZERO = new IntBounds(Integer.valueOf(0), Integer.valueOf(0));
   public static final IntBounds INTEGER = new IntBounds(Integer.MIN_VALUE, Integer.MAX_VALUE);
   public static final IntBounds INTEGER_POSITIVE = new IntBounds(Integer.valueOf(0), Integer.MAX_VALUE);
   public final Integer min;
   public final Integer max;

   public IntBounds(Integer bound1, Integer bound2) {
      this.min = Math.min(bound1.intValue(), bound2.intValue());
      this.max = Math.max(bound1.intValue(), bound2.intValue());
   }

   public IntBounds(Integer bound) {
      this(bound, bound);
   }

   public static IntBounds from(Integer bound1, Integer bound2) {
      return new IntBounds(bound1, bound2);
   }

   public static IntBounds from(Integer bound) {
      return from(bound, bound);
   }

   public static IntBounds from(IBounds<? extends Number> bounds) {
      return from(Integer.valueOf(bounds.getMin().intValue()), Integer.valueOf(bounds.getMax().intValue()));
   }

   public final Integer getMin() {
      return this.min;
   }

   public final Integer getMax() {
      return this.max;
   }

   public boolean encloses(Integer value) {
      return value.intValue() >= this.getMin().intValue() && value.intValue() <= this.getMax().intValue();
   }

   public Integer enclose(Integer value) {
      return CommonMath.Range.enclose(value.intValue(), this.getMin().intValue(), this.getMax().intValue());
   }

   public Integer randomBetween() {
      return CommonMath.Random.between(this.getMin().intValue(), this.getMax().intValue());
   }

   public Integer approxUp(float factor) {
      return CommonMath.Approx.strictUp(factor, this);
   }

   public Integer approxDown(float factor) {
      return CommonMath.Approx.strictDown(factor, this);
   }

   public float approxFactor(Integer value) {
      return CommonMath.notZero((double)this.length().intValue()) ? (float)(value.intValue() - this.getMin().intValue()) / (float)this.length().intValue() : 0.0F;
   }

   public Integer length() {
      return this.getMax().intValue() - this.getMin().intValue();
   }

   public Integer center() {
      return this.getMin().intValue() + this.length().intValue() / 2;
   }

   public IntBounds reduce(Integer value) {
      return from(Integer.valueOf(this.getMin().intValue() + value.intValue()), Integer.valueOf(this.getMax().intValue() - value.intValue()));
   }

   public IntBounds extend(Integer value) {
      return from(Integer.valueOf(this.getMin().intValue() - value.intValue()), Integer.valueOf(this.getMax().intValue() + value.intValue()));
   }

   public IntBounds offset(Integer value) {
      return from(Integer.valueOf(this.getMin().intValue() + value.intValue()), Integer.valueOf(this.getMax().intValue() + value.intValue()));
   }

   public IntBounds fit(IBounds<Integer> bounds) {
      return from(bounds.enclose(this.getMin()), bounds.enclose(this.getMax()));
   }

   public IntBounds unite(IBounds<Integer> bounds) {
      return from(Integer.valueOf(Math.min(this.getMin().intValue(), ((Integer)bounds.getMin()).intValue())), Integer.valueOf(Math.max(this.getMax().intValue(), ((Integer)bounds.getMax()).intValue())));
   }

   public String toString() {
      return IntBounds.class.getSimpleName() + Args.group(Args.get("min", this.getMin()), Args.get("max", this.getMax()));
   }

   public boolean randomFits(Integer value) {
      if (value.intValue() >= this.getMax().intValue()) {
         return true;
      } else if (value.intValue() <= this.getMin().intValue()) {
         return false;
      } else {
         Integer random = this.randomBetween();
         return random.intValue() <= value.intValue();
      }
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof IntBounds)) {
         return false;
      } else {
         IntBounds bounds = (IntBounds)obj;
         return this.getMin().equals(bounds.getMin()) && this.getMax().equals(bounds.getMax());
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.getMin(), this.getMax()});
   }

   public boolean corners(Integer value) {
      return this.getMin().equals(value) || this.getMax().equals(value);
   }

   public Iterator<Integer> iterator() {
      return new IntBounds.Iter();
   }

   private class Iter implements Iterator<Integer> {
      Integer value;

      private Iter() {
         this.value = IntBounds.this.getMin().intValue() - 1;
      }

      public boolean hasNext() {
         return IntBounds.this.encloses(Integer.valueOf(this.value.intValue() + 1));
      }

      public Integer next() {
         Integer var1 = this.value;
         Integer var2 = this.value = this.value.intValue() + 1;
         return this.value;
      }
   }
}

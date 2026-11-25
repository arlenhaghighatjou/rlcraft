package com.endertech.common;

import java.util.Objects;

public class FloatBounds implements IBounds<Float> {
   public static final FloatBounds ZERO = new FloatBounds(0.0F, 0.0F);
   public static final FloatBounds FLOAT = new FloatBounds(Float.MIN_VALUE, Float.MAX_VALUE);
   public static final FloatBounds FLOAT_POSITIVE = new FloatBounds(0.0F, Float.MAX_VALUE);
   public final Float min;
   public final Float max;

   public FloatBounds(Float bound1, Float bound2) {
      this.min = Math.min(bound1.floatValue(), bound2.floatValue());
      this.max = Math.max(bound1.floatValue(), bound2.floatValue());
   }

   public static FloatBounds from(Float bound1, Float bound2) {
      return new FloatBounds(bound1, bound2);
   }

   public static FloatBounds from(Float bound) {
      return from(bound, bound);
   }

   public static FloatBounds from(IBounds<? extends Number> bounds) {
      return from(Float.valueOf(bounds.getMin().floatValue()), Float.valueOf(bounds.getMax().floatValue()));
   }

   public final Float getMin() {
      return this.min;
   }

   public final Float getMax() {
      return this.max;
   }

   public boolean encloses(Float value) {
      return value.floatValue() >= this.getMin().floatValue() && value.floatValue() <= this.getMax().floatValue();
   }

   public Float enclose(Float value) {
      return CommonMath.Range.enclose(value.floatValue(), this.getMin().floatValue(), this.getMax().floatValue());
   }

   public Float randomBetween() {
      return CommonMath.Random.between(this.getMin().floatValue(), this.getMax().floatValue());
   }

   public Float approxUp(float factor) {
      return CommonMath.Approx.strictUp(factor, this);
   }

   public Float approxDown(float factor) {
      return CommonMath.Approx.strictDown(factor, this);
   }

   public float approxFactor(Float value) {
      return CommonMath.notZero((double)this.length().floatValue()) ? (value.floatValue() - this.getMin().floatValue()) / this.length().floatValue() : 0.0F;
   }

   public Float center() {
      return this.getMin().floatValue() + this.length().floatValue() * 0.5F;
   }

   public Float length() {
      return this.getMax().floatValue() - this.getMin().floatValue();
   }

   public FloatBounds scale(float scale) {
      return from(Float.valueOf(this.getMin().floatValue() * scale), Float.valueOf(this.getMax().floatValue() * scale));
   }

   public FloatBounds extend(Float value) {
      return from(Float.valueOf(this.getMin().floatValue() - value.floatValue()), Float.valueOf(this.getMax().floatValue() + value.floatValue()));
   }

   public FloatBounds reduce(Float value) {
      return from(Float.valueOf(this.getMin().floatValue() + value.floatValue()), Float.valueOf(this.getMax().floatValue() - value.floatValue()));
   }

   public FloatBounds offset(Float value) {
      return from(Float.valueOf(this.getMin().floatValue() + value.floatValue()), Float.valueOf(this.getMax().floatValue() + value.floatValue()));
   }

   public FloatBounds fit(IBounds<Float> bounds) {
      return from(bounds.enclose(this.getMin()), bounds.enclose(this.getMax()));
   }

   public String toString() {
      return FloatBounds.class.getSimpleName() + Args.group(Args.get("min", this.getMin()), Args.get("max", this.getMax()));
   }

   public boolean randomFits(Float value) {
      if (value.floatValue() >= this.getMax().floatValue()) {
         return true;
      } else if (value.floatValue() <= this.getMin().floatValue()) {
         return false;
      } else {
         Float random = this.randomBetween();
         return random.floatValue() <= value.floatValue();
      }
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof FloatBounds)) {
         return false;
      } else {
         FloatBounds bounds = (FloatBounds)obj;
         return this.getMin().equals(bounds.getMin()) && this.getMax().equals(bounds.getMax());
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.getMin(), this.getMax()});
   }

   public boolean corners(Float value) {
      return this.getMin().equals(value) || this.getMax().equals(value);
   }

   public FloatBounds unite(IBounds<Float> bounds) {
      return from(Float.valueOf(Math.min(this.getMin().floatValue(), ((Float)bounds.getMin()).floatValue())), Float.valueOf(Math.max(this.getMax().floatValue(), ((Float)bounds.getMax()).floatValue())));
   }
}

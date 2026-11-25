package com.endertech.common;

public interface IBounds<T extends Number> {
   T getMin();

   T getMax();

   boolean encloses(T var1);

   T enclose(T var1);

   IBounds<T> fit(IBounds<T> var1);

   IBounds<T> unite(IBounds<T> var1);

   T randomBetween();

   T approxUp(float var1);

   T approxDown(float var1);

   default CommonMath.Approx<T> approxTo(IBounds<T> bounds) {
      return new CommonMath.Approx<T>(this, bounds);
   }

   T center();

   T length();

   float approxFactor(T var1);

   IBounds<T> extend(T var1);

   IBounds<T> reduce(T var1);

   IBounds<T> offset(T var1);

   boolean randomFits(T var1);

   boolean corners(T var1);
}

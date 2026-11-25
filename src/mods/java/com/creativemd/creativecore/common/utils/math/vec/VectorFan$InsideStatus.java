package com.creativemd.creativecore.common.utils.math.vec;

enum VectorFan$InsideStatus {
   INSIDE,
   OUTSIDE_MIN_ONE,
   OUTSIDE_MAX_ONE,
   OUTSIDE_MIN_TWO,
   OUTSIDE_MAX_TWO,
   OUTSIDE_MIN_ONE_MIN_TWO,
   OUTSIDE_MIN_ONE_MAX_TWO,
   OUTSIDE_MAX_ONE_MIN_TWO,
   OUTSIDE_MAX_ONE_MAX_TWO;

   private VectorFan$InsideStatus() {
   }

   public abstract boolean isInside();

   public abstract boolean isOutsideOne();

   public abstract boolean outsideDirectionOne();

   public abstract boolean isOutsideTwo();

   public abstract boolean outsideDirectionTwo();

   public static VectorFan$InsideStatus get(float one, float two, float minOne, float minTwo, float maxOne, float maxTwo) {
      if (one >= minOne) {
         if (one <= maxOne) {
            if (two >= minTwo) {
               return two <= maxTwo ? INSIDE : OUTSIDE_MAX_TWO;
            } else {
               return OUTSIDE_MIN_TWO;
            }
         } else if (two >= minTwo) {
            return two <= maxTwo ? OUTSIDE_MAX_ONE : OUTSIDE_MAX_ONE_MAX_TWO;
         } else {
            return OUTSIDE_MAX_ONE_MIN_TWO;
         }
      } else if (two >= minTwo) {
         return two <= maxTwo ? OUTSIDE_MIN_ONE : OUTSIDE_MIN_ONE_MAX_TWO;
      } else {
         return OUTSIDE_MIN_ONE_MIN_TWO;
      }
   }
}

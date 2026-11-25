package me.lucko.spark.lib.adventure.examination;

import java.util.stream.Stream;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface Examiner<R> {
   @NonNull
   default R examine(@NonNull Examinable examinable) {
      return (R)this.examine(examinable.examinableName(), examinable.examinableProperties());
   }

   @NonNull
   R examine(@NonNull String var1, @NonNull Stream<? extends ExaminableProperty> var2);

   @NonNull
   R examine(@Nullable Object var1);

   @NonNull
   R examine(boolean var1);

   @NonNull
   R examine(@Nullable boolean[] var1);

   @NonNull
   R examine(byte var1);

   @NonNull
   R examine(@Nullable byte[] var1);

   @NonNull
   R examine(char var1);

   @NonNull
   R examine(@Nullable char[] var1);

   @NonNull
   R examine(double var1);

   @NonNull
   R examine(@Nullable double[] var1);

   @NonNull
   R examine(float var1);

   @NonNull
   R examine(@Nullable float[] var1);

   @NonNull
   R examine(int var1);

   @NonNull
   R examine(@Nullable int[] var1);

   @NonNull
   R examine(long var1);

   @NonNull
   R examine(@Nullable long[] var1);

   @NonNull
   R examine(short var1);

   @NonNull
   R examine(@Nullable short[] var1);

   @NonNull
   R examine(@Nullable String var1);
}

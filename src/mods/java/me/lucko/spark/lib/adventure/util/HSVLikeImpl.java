package me.lucko.spark.lib.adventure.util;

import java.util.Objects;
import me.lucko.spark.lib.adventure.examination.string.StringExaminer;
import org.jetbrains.annotations.Nullable;

final class HSVLikeImpl implements HSVLike {
   private final float h;
   private final float s;
   private final float v;

   HSVLikeImpl(float h, float s, float v) {
      this.h = h;
      this.s = s;
      this.v = v;
   }

   public float h() {
      return this.h;
   }

   public float s() {
      return this.s;
   }

   public float v() {
      return this.v;
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof HSVLikeImpl)) {
         return false;
      } else {
         HSVLikeImpl that = (HSVLikeImpl)other;
         return ShadyPines.equals(that.h, this.h) && ShadyPines.equals(that.s, this.s) && ShadyPines.equals(that.v, this.v);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.h, this.s, this.v});
   }

   public String toString() {
      return (String)this.examine(StringExaminer.simpleEscaping());
   }
}

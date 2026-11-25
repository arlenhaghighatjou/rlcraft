package com.alcatrazescapee.notreepunching.util.types;

import javax.annotation.Nullable;

public enum Pottery {
   WORKED,
   LARGE_VESSEL,
   SMALL_VESSEL,
   BUCKET,
   FLOWER_POT;

   private static Pottery[] values = values();

   private Pottery() {
   }

   @Nullable
   public Pottery next() {
      return this == FLOWER_POT ? null : values[this.ordinal() + 1];
   }
}

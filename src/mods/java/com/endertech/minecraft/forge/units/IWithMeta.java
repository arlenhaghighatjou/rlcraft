package com.endertech.minecraft.forge.units;

public interface IWithMeta<T> {
   T withMeta(int var1);

   default T withMetaAll() {
      return (T)this.withMeta(32767);
   }
}

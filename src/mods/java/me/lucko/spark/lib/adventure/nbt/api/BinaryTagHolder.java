package me.lucko.spark.lib.adventure.nbt.api;

import me.lucko.spark.lib.adventure.util.Codec;
import org.jetbrains.annotations.NotNull;

public interface BinaryTagHolder {
   @NotNull
   static <T, EX extends Exception> BinaryTagHolder encode(@NotNull T nbt, @NotNull Codec<? super T, String, ?, EX> codec) throws EX {
      return new BinaryTagHolderImpl(codec.encode(nbt));
   }

   @NotNull
   static BinaryTagHolder of(@NotNull String string) {
      return new BinaryTagHolderImpl(string);
   }

   @NotNull
   String string();

   @NotNull
   <T, DX extends Exception> T get(@NotNull Codec<T, String, DX, ?> var1) throws DX;
}

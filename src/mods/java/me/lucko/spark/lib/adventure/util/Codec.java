package me.lucko.spark.lib.adventure.util;

import org.jetbrains.annotations.NotNull;

public interface Codec<D, E, DX extends Throwable, EX extends Throwable> {
   @NotNull
   static <D, E, DX extends Throwable, EX extends Throwable> Codec<D, E, DX, EX> of(@NotNull final Codec.Decoder<D, E, DX> decoder, @NotNull final Codec.Encoder<D, E, EX> encoder) {
      return new Codec<D, E, DX, EX>() {
         @NotNull
         public D decode(@NotNull E encoded) throws DX {
            return decoder.decode(encoded);
         }

         @NotNull
         public E encode(@NotNull D decoded) throws EX {
            return encoder.encode(decoded);
         }
      };
   }

   @NotNull
   D decode(@NotNull E var1) throws DX;

   @NotNull
   E encode(@NotNull D var1) throws EX;

   public interface Decoder<D, E, X extends Throwable> {
      @NotNull
      D decode(@NotNull E var1) throws X;
   }

   public interface Encoder<D, E, X extends Throwable> {
      @NotNull
      E encode(@NotNull D var1) throws X;
   }
}

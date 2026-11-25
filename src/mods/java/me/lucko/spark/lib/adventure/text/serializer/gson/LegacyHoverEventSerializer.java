package me.lucko.spark.lib.adventure.text.serializer.gson;

import java.io.IOException;
import me.lucko.spark.lib.adventure.text.Component;
import me.lucko.spark.lib.adventure.text.event.HoverEvent;
import me.lucko.spark.lib.adventure.util.Codec;
import org.jetbrains.annotations.NotNull;

public interface LegacyHoverEventSerializer {
   @NotNull
   HoverEvent.ShowItem deserializeShowItem(@NotNull Component var1) throws IOException;

   @NotNull
   HoverEvent.ShowEntity deserializeShowEntity(@NotNull Component var1, Codec.Decoder<Component, String, ? extends RuntimeException> var2) throws IOException;

   @NotNull
   Component serializeShowItem(@NotNull HoverEvent.ShowItem var1) throws IOException;

   @NotNull
   Component serializeShowEntity(@NotNull HoverEvent.ShowEntity var1, Codec.Encoder<Component, String, ? extends RuntimeException> var2) throws IOException;
}

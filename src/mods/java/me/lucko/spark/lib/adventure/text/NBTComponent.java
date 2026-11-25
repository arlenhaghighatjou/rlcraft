package me.lucko.spark.lib.adventure.text;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface NBTComponent<C extends NBTComponent<C, B>, B extends NBTComponentBuilder<C, B>> extends BuildableComponent<C, B> {
   @NotNull
   String nbtPath();

   @Contract(
      pure = true
   )
   @NotNull
   C nbtPath(@NotNull String var1);

   boolean interpret();

   @Contract(
      pure = true
   )
   @NotNull
   C interpret(boolean var1);

   @Nullable
   Component separator();

   @NotNull
   C separator(@Nullable ComponentLike var1);
}

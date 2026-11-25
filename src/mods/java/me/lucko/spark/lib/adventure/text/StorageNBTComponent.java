package me.lucko.spark.lib.adventure.text;

import me.lucko.spark.lib.adventure.key.Key;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface StorageNBTComponent extends NBTComponent<StorageNBTComponent, StorageNBTComponent.Builder>, ScopedComponent<StorageNBTComponent> {
   @NotNull
   Key storage();

   @Contract(
      pure = true
   )
   @NotNull
   StorageNBTComponent storage(@NotNull Key var1);

   public interface Builder extends NBTComponentBuilder<StorageNBTComponent, StorageNBTComponent.Builder> {
      @Contract("_ -> this")
      @NotNull
      StorageNBTComponent.Builder storage(@NotNull Key var1);
   }
}

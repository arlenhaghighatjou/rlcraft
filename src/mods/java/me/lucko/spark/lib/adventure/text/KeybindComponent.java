package me.lucko.spark.lib.adventure.text;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface KeybindComponent extends BuildableComponent<KeybindComponent, KeybindComponent.Builder>, ScopedComponent<KeybindComponent> {
   @NotNull
   String keybind();

   @Contract(
      pure = true
   )
   @NotNull
   KeybindComponent keybind(@NotNull String var1);

   public interface Builder extends ComponentBuilder<KeybindComponent, KeybindComponent.Builder> {
      @Contract("_ -> this")
      @NotNull
      KeybindComponent.Builder keybind(@NotNull String var1);
   }
}

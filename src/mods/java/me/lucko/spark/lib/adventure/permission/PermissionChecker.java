package me.lucko.spark.lib.adventure.permission;

import java.util.function.Predicate;
import me.lucko.spark.lib.adventure.key.Key;
import me.lucko.spark.lib.adventure.pointer.Pointer;
import me.lucko.spark.lib.adventure.util.TriState;
import org.jetbrains.annotations.NotNull;

public interface PermissionChecker extends Predicate<String> {
   Pointer<PermissionChecker> POINTER = Pointer.<PermissionChecker>pointer(PermissionChecker.class, Key.key("adventure", "permission"));

   @NotNull
   static PermissionChecker always(TriState state) {
      if (state == TriState.TRUE) {
         return PermissionCheckers.TRUE;
      } else {
         return state == TriState.FALSE ? PermissionCheckers.FALSE : PermissionCheckers.NOT_SET;
      }
   }

   @NotNull
   TriState value(String var1);

   default boolean test(String permission) {
      return this.value(permission) == TriState.TRUE;
   }
}

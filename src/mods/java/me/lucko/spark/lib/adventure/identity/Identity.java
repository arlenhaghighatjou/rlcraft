package me.lucko.spark.lib.adventure.identity;

import java.util.UUID;
import java.util.stream.Stream;
import me.lucko.spark.lib.adventure.examination.Examinable;
import me.lucko.spark.lib.adventure.examination.ExaminableProperty;
import me.lucko.spark.lib.adventure.key.Key;
import me.lucko.spark.lib.adventure.pointer.Pointer;
import me.lucko.spark.lib.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface Identity extends Examinable {
   Pointer<String> NAME = Pointer.<String>pointer(String.class, Key.key("adventure", "name"));
   Pointer<UUID> UUID = Pointer.<UUID>pointer(UUID.class, Key.key("adventure", "uuid"));
   Pointer<Component> DISPLAY_NAME = Pointer.<Component>pointer(Component.class, Key.key("adventure", "display_name"));

   @NotNull
   static Identity nil() {
      return Identities.NIL;
   }

   @NotNull
   static Identity identity(@NotNull UUID uuid) {
      return (Identity)(uuid.equals(Identities.NIL.uuid()) ? Identities.NIL : new IdentityImpl(uuid));
   }

   @NotNull
   UUID uuid();

   @NotNull
   default Stream<? extends ExaminableProperty> examinableProperties() {
      return Stream.of(ExaminableProperty.of("uuid", (Object)this.uuid()));
   }
}

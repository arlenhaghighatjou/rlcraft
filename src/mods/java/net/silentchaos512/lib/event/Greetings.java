package net.silentchaos512.lib.event;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public final class Greetings {
   private static final List<Function<EntityPlayer, Optional<ITextComponent>>> messages = new ArrayList();

   private Greetings() {
   }

   /** @deprecated */
   @Deprecated
   public static void addMessage(Supplier<ITextComponent> message) {
      messages.add((player) -> {
         return Optional.ofNullable(message.get());
      });
   }

   public static void addMessage(Function<EntityPlayer, ITextComponent> message) {
      messages.add((player) -> {
         return Optional.ofNullable(message.apply(player));
      });
   }

   public static void register(EventBus bus) {
      bus.register(PlayerLoggedInEvent.class, Greetings::onPlayerLoggedIn);
   }

   public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
      if (event.player != null) {
         messages.forEach((msg) -> {
            Optional<ITextComponent> var10000 = msg.apply(event.player);
            EntityPlayer var10001 = event.player;
            var10000.ifPresent(var10001::sendMessage);
         });
      }
   }
}

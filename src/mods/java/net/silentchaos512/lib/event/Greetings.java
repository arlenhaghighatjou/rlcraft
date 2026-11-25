package net.silentchaos512.lib.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

@EventBusSubscriber(
   modid = "silentlib"
)
public final class Greetings {
   private static final List<Function<EntityPlayer, Optional<ITextComponent>>> messages = new ArrayList();

   private Greetings() {
   }

   /** @deprecated */
   @Deprecated
   public static void addMessage(Supplier<ITextComponent> message) {
      messages.add((player) -> {
         return (void)Optional.ofNullable(message.get());
      });
   }

   public static void addMessage(Function<EntityPlayer, ITextComponent> message) {
      messages.add((player) -> {
         return (void)Optional.ofNullable(message.apply(player));
      });
   }

   @SubscribeEvent
   public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
      if (event.player != null) {
         messages.forEach((msg) -> {
            Optional var10000 = (Optional)msg.apply(event.player);
            EntityPlayer var10001 = event.player;
            event.player.getClass();
            var10000.ifPresent(var10001::sendMessage);
         });
      }
   }
}

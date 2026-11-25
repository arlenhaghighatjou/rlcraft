package dk.zlepper.itlt.eventhandlers;

import dk.zlepper.itlt.Itlt;
import dk.zlepper.itlt.KeyBindings;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public class KeyPressedInChatGuiEventHandler {
   private ChatGuiEventHandler handler;

   public KeyPressedInChatGuiEventHandler(ChatGuiEventHandler handler) {
      Itlt.logger.info("Creating keyPressedHandler");
      this.handler = handler;
   }

   @SubscribeEvent
   public void onKeyInput(KeyInputEvent event) {
      Itlt.logger.info("Key pressed");
      if (KeyBindings.isEnterPressed()) {
         Itlt.logger.info("It was the enter key");
         this.handler.clearInputField();
      }

   }
}

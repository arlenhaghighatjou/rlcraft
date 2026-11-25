package com.bloodnbonesgaming.lib.events;

import com.bloodnbonesgaming.lib.client.chat.ChatListenerCopy;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.util.text.ChatType;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientEventHandler {
   private boolean registeredChatListener = false;

   public ClientEventHandler() {
   }

   @SubscribeEvent
   public void onGuiOpen(GuiOpenEvent event) {
      if (event.getGui() instanceof GuiMainMenu && !this.registeredChatListener) {
         ((List)Minecraft.getMinecraft().ingameGUI.chatListeners.get(ChatType.SYSTEM)).add(new ChatListenerCopy());
         this.registeredChatListener = true;
      }

   }
}

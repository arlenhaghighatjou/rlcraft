package com.bloodnbonesgaming.lib.events;

import com.bloodnbonesgaming.lib.client.chat.ChatListenerCopy;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.util.text.ChatType;
import net.minecraftforge.client.event.GuiOpenEvent;

public class ClientEventHandler {
   private boolean registeredChatListener = false;

   public ClientEventHandler() {
   }

   public void onGuiOpen(GuiOpenEvent event) {
      if (Minecraft.getMinecraft().ingameGUI != null && !this.registeredChatListener) {
         Minecraft.getMinecraft().ingameGUI.addChatListener(ChatType.SYSTEM, new ChatListenerCopy());
         this.registeredChatListener = true;
      }

   }
}

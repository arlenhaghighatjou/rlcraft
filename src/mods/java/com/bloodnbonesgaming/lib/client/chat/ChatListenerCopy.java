package com.bloodnbonesgaming.lib.client.chat;

import com.bloodnbonesgaming.lib.util.ClipboardHelper;
import net.minecraft.client.gui.chat.IChatListener;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;

public class ChatListenerCopy implements IChatListener {
   public ChatListenerCopy() {
   }

   public void say(ChatType type, ITextComponent component) {
      if (component.getStyle() != null && component.getStyle().getClickEvent() != null && component.getStyle().getClickEvent().getValue() != null && component.getStyle().getClickEvent().getValue().startsWith("/clipboard")) {
         ClipboardHelper.copyToClipboard(component.getUnformattedText());
      }

   }
}

package org.orecruncher.dsurround.capabilities.speech;

import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
class SpeechBubbleData {
   private static final int MIN_TEXT_WIDTH = 60;
   private static final int MAX_TEXT_WIDTH = 180;
   private final int expires;
   private String incomingText;
   private List<String> messages;

   public SpeechBubbleData(@Nonnull String message, int expiry) {
      this.incomingText = message.replaceAll("(\\xA7.)", "");
      this.expires = expiry;
   }

   @Nonnull
   public List<String> getText() {
      if (this.messages == null) {
         FontRenderer font = Minecraft.getMinecraft().getRenderManager().getFontRenderer();
         if (font == null) {
            return ImmutableList.of();
         }

         this.messages = font.listFormattedStringToWidth(this.incomingText, 180);
         this.incomingText = null;
      }

      return this.messages;
   }

   public boolean isExpired(int currentTick) {
      return currentTick > this.expires;
   }
}

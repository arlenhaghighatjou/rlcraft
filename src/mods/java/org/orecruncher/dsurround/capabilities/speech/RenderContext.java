package org.orecruncher.dsurround.capabilities.speech;

import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.lib.collections.ObjectArray;

@SideOnly(Side.CLIENT)
public final class RenderContext {
   private static final int MIN_TEXT_WIDTH = 60;
   private static final double BUBBLE_MARGIN = 4.0D;
   public final int textWidth;
   public final int numberOfMessages;
   public final double top;
   public final double bottom;
   public final double left;
   public final double right;

   RenderContext(@Nonnull ObjectArray<String> messages) {
      int theWidth = 60;

      for(String s : messages) {
         theWidth = Math.max(theWidth, Minecraft.getMinecraft().fontRenderer.getStringWidth(s));
      }

      this.textWidth = theWidth;
      this.numberOfMessages = messages.size();
      this.top = (double)(-this.numberOfMessages * 9) - 4.0D;
      this.bottom = 4.0D;
      this.left = -((double)this.textWidth / 2.0D + 4.0D);
      this.right = (double)this.textWidth / 2.0D + 4.0D;
   }
}

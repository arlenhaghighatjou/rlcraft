package goblinbob.mobends.core.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

public class UIScissorHelper {
   public static final UIScissorHelper INSTANCE = new UIScissorHelper();
   private int x = 0;
   private int y = 0;
   private int width = 0;
   private int height = 0;

   public UIScissorHelper() {
   }

   public void setUIBounds(int uiX, int uiY, int uiWidth, int uiHeight) {
      ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
      int scaledWidth = scaledResolution.getScaledWidth();
      int scaledHeight = scaledResolution.getScaledHeight();
      this.x = uiX * Minecraft.getMinecraft().displayWidth / scaledWidth;
      this.y = (scaledHeight - uiY - uiHeight) * Minecraft.getMinecraft().displayHeight / scaledHeight;
      this.width = uiWidth * Minecraft.getMinecraft().displayWidth / scaledWidth;
      this.height = uiHeight * Minecraft.getMinecraft().displayHeight / scaledHeight;
   }

   public void enable() {
      GL11.glEnable(3089);
      GL11.glScissor(this.x, this.y, this.width, this.height);
   }

   public void disable() {
      GL11.glDisable(3089);
   }
}

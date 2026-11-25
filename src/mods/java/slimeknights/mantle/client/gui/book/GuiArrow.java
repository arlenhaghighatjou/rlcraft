package slimeknights.mantle.client.gui.book;

import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiArrow extends GuiButton {
   private static final int X = 412;
   public static final int WIDTH = 18;
   public static final int HEIGHT = 10;
   public GuiArrow.ArrowType arrowType;
   public int color;
   public int hoverColor;

   public GuiArrow(int buttonId, int x, int y, GuiArrow.ArrowType arrowType, int color, int hoverColor) {
      super(buttonId, x, y, arrowType.w, arrowType.h, "");
      this.arrowType = arrowType;
      this.color = color;
      this.hoverColor = hoverColor;
   }

   public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY, float partialTicks) {
      if (this.visible) {
         mc.getTextureManager().bindTexture(Textures.TEX_BOOK);
         this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
         int color = this.hovered ? this.hoverColor : this.color;
         float r = (float)(color >> 16 & 255) / 255.0F;
         float g = (float)(color >> 8 & 255) / 255.0F;
         float b = (float)(color & 255) / 255.0F;
         GlStateManager.color(r, g, b);
         Gui.drawScaledCustomSizeModalRect(this.x, this.y, (float)this.arrowType.x, (float)this.arrowType.y, this.width, this.height, this.width, this.height, 512.0F, 512.0F);
         this.mouseDragged(mc, mouseX, mouseY);
      }

   }

   public static enum ArrowType {
      NEXT(412, 0),
      PREV(412, 10),
      RIGHT(412, 20),
      LEFT(412, 30),
      BACK_UP(412, 40, 18, 18),
      UP(412, 58, 10, 18),
      DOWN(422, 58, 10, 18),
      REFRESH(412, 76, 18, 18);

      public final int x;
      public final int y;
      public final int w;
      public final int h;

      private ArrowType(int x, int y) {
         this(x, y, 18, 10);
      }

      private ArrowType(int x, int y, int w, int h) {
         this.x = x;
         this.y = y;
         this.w = w;
         this.h = h;
      }
   }
}

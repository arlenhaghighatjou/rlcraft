package ivorius.reccomplex.gui;

import ivorius.reccomplex.RecurrentComplex;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiValidityStateIndicator extends Gui {
   protected static final ResourceLocation textureSprite = new ResourceLocation("reccomplex", RecurrentComplex.filePathTextures + "gui_states.png");
   protected int width = 10;
   protected int height = 10;
   public int xPosition;
   public int yPosition;
   protected GuiValidityStateIndicator.State state;
   private boolean isVisible = true;

   public GuiValidityStateIndicator(int xPosition, int yPosition, GuiValidityStateIndicator.State state) {
      this.xPosition = xPosition;
      this.yPosition = yPosition;
      this.state = state;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public int getxPosition() {
      return this.xPosition;
   }

   public int getyPosition() {
      return this.yPosition;
   }

   public void draw() {
      if (this.isVisible) {
         GlStateManager.color(1.0F, 1.0F, 1.0F);
         Minecraft.getMinecraft().getTextureManager().bindTexture(textureSprite);
         this.drawTexturedModalRect(this.xPosition, this.yPosition, this.state.getUvX(), this.state.getUvY(), this.width, this.height);
      }

   }

   public GuiValidityStateIndicator.State getState() {
      return this.state;
   }

   public void setState(GuiValidityStateIndicator.State state) {
      this.state = state;
   }

   public boolean isVisible() {
      return this.isVisible;
   }

   public void setVisible(boolean isVisible) {
      this.isVisible = isVisible;
   }

   public static enum State {
      VALID(0, 0),
      SEMI_VALID(10, 0),
      INVALID(20, 0),
      UNKNWON(30, 0);

      private int uvX;
      private int uvY;

      private State(int uvX, int uvY) {
         this.uvX = uvX;
         this.uvY = uvY;
      }

      public int getUvX() {
         return this.uvX;
      }

      public int getUvY() {
         return this.uvY;
      }
   }
}

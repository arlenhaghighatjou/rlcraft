package goblinbob.mobends.core.client.gui.elements;

import goblinbob.mobends.core.util.Draw;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiToggleButton {
   protected static final ResourceLocation BUTTON_TEXTURES = new ResourceLocation("textures/gui/widgets.png");
   private static final int FLIPPER_WIDTH = 30;
   private static final int HEIGHT = 20;
   protected int x = 0;
   protected int y = 0;
   protected boolean hovered = false;
   protected boolean enabled = true;
   protected boolean toggleState = false;
   protected final String title;
   protected final int labelWidth;

   public GuiToggleButton(String title, int minLabelWidth) {
      this.title = title;
      int titleWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(title) + 20;
      this.labelWidth = titleWidth > minLabelWidth ? titleWidth : minLabelWidth;
   }

   public void initGui(int x, int y) {
      this.x = x;
      this.y = y;
   }

   public void update(int mouseX, int mouseY) {
      this.hovered = mouseX >= this.x && mouseX <= this.x + this.labelWidth + 30 && mouseY >= this.y && mouseY <= this.y + 20;
   }

   public void draw() {
      Minecraft mc = Minecraft.getMinecraft();
      FontRenderer fontRenderer = mc.fontRenderer;
      mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glEnable(3042);
      OpenGlHelper.glBlendFunc(770, 771, 1, 0);
      GL11.glBlendFunc(770, 771);
      int k = this.hovered ? 1 : 0;
      GL11.glPushMatrix();
      Draw.texturedModalRect(this.x, this.y, 0, 66 + k * 20, (this.labelWidth + 30) / 2, 20);
      Draw.texturedModalRect(this.x + this.labelWidth / 2, this.y, 200 - (this.labelWidth + 30) / 2, 66 + k * 20, (this.labelWidth + 30) / 2, 20);
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      if (this.toggleState) {
         GL11.glColor3f(0.3F, 1.0F, 0.5F);
      } else {
         GL11.glColor3f(1.0F, 0.3F, 0.3F);
      }

      Draw.texturedModalRect(this.x + this.labelWidth, this.y, 0, 66 + k * 20, 15, 20);
      Draw.texturedModalRect(this.x + this.labelWidth + 15, this.y, 185, 66 + k * 20, 15, 20);
      GL11.glPopMatrix();
      int l = 14737632;
      if (!this.enabled) {
         l = 10526880;
      } else if (this.hovered) {
         l = 16777120;
      }

      String stateText = this.toggleState ? "ON" : "OFF";
      int textWidth = fontRenderer.getStringWidth(stateText);
      fontRenderer.drawString(stateText, this.x + this.labelWidth - textWidth / 2 + 15, this.y + 6, l);
      fontRenderer.drawString(this.title, this.x + 10, this.y + 6, l);
      GL11.glBlendFunc(770, 771);
   }

   public void setToggleState(boolean state) {
      this.toggleState = state;
   }

   public boolean mouseClicked(int mouseX, int mouseY, int button) {
      if (this.hovered && button == 0) {
         this.toggleState = !this.toggleState;
         return true;
      } else {
         return false;
      }
   }

   public boolean getToggleState() {
      return this.toggleState;
   }
}

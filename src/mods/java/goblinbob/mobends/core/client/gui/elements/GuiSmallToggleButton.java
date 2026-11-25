package goblinbob.mobends.core.client.gui.elements;

import goblinbob.mobends.core.util.Draw;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.OpenGlHelper;
import net.minecraft.util.ResourceLocation;

public class GuiSmallToggleButton {
   protected static final ResourceLocation BUTTON_TEXTURES = new ResourceLocation("textures/gui/widgets.png");
   private static final int WIDTH = 30;
   private static final int HEIGHT = 20;
   protected int x = 0;
   protected int y = 0;
   protected boolean hovered = false;
   protected boolean enabled = true;
   protected boolean toggleState = false;

   public GuiSmallToggleButton() {
   }

   public void initGui(int x, int y) {
      this.x = x;
      this.y = y;
   }

   public void update(int mouseX, int mouseY) {
      this.hovered = mouseX >= this.x && mouseX <= this.x + 30 && mouseY >= this.y && mouseY <= this.y + 20;
   }

   public void draw() {
      Minecraft mc = Minecraft.getMinecraft();
      FontRenderer fontRenderer = mc.fontRenderer;
      mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.enableBlend();
      OpenGlHelper.glBlendFunc(770, 771, 1, 0);
      GlStateManager.blendFunc(770, 771);
      int k = this.hovered ? 1 : 0;
      GlStateManager.pushMatrix();
      if (this.toggleState) {
         GlStateManager.color(0.3F, 1.0F, 0.7F);
      } else {
         GlStateManager.color(1.0F, 0.3F, 0.4F);
      }

      Draw.texturedModalRect(this.x, this.y, 0, 66 + k * 20, 15, 20);
      Draw.texturedModalRect(this.x + 15, this.y, 185, 66 + k * 20, 15, 20);
      GlStateManager.popMatrix();
      int l = 14737632;
      if (!this.enabled) {
         l = 10526880;
      } else if (this.hovered) {
         l = 16777120;
      }

      String stateText = this.toggleState ? "ON" : "OFF";
      int textWidth = fontRenderer.getStringWidth(stateText);
      fontRenderer.drawString(stateText, this.x - textWidth / 2 + 15, this.y + 6, l);
      GlStateManager.blendFunc(770, 771);
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

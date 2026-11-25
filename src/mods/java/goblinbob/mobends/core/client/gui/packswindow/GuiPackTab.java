package goblinbob.mobends.core.client.gui.packswindow;

import goblinbob.mobends.core.util.Draw;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;

public class GuiPackTab {
   public static final int WIDTH = 18;
   public static final int HEIGHT = 15;
   public final String titleKey;
   private final int textureIndex;
   private int x;
   private int y;
   private boolean hovered;
   private boolean selected;
   private float selectedTransitionTween;

   public GuiPackTab(String titleKey, int textureIndex) {
      this.titleKey = titleKey;
      this.textureIndex = textureIndex;
      this.hovered = false;
      this.selected = false;
      this.selectedTransitionTween = 0.0F;
   }

   public void initGui(int x, int y) {
      this.x = x;
      this.y = y;
   }

   public void update(int mouseX, int mouseY) {
      this.hovered = mouseX >= this.x && mouseX <= this.x + 18 && mouseY >= this.y - 15 && mouseY <= this.y;
      if (this.selected) {
         this.selectedTransitionTween = Math.min(this.selectedTransitionTween + 0.2F, 1.0F);
      } else {
         this.selectedTransitionTween = Math.max(this.selectedTransitionTween - 0.2F, 0.0F);
      }

   }

   public void draw(int mouseX, int mouseY) {
      this.update(mouseX, mouseY);
      Minecraft.getMinecraft().getTextureManager().bindTexture(GuiPacksWindow.BACKGROUND_TEXTURE);
      int yOffset = this.y - 15 + (this.selected ? -1 : 0);
      int SELECTED_TEXTURE_Y = 147;
      int HOVERED_TEXTURE_Y = 132;
      int NEUTRAL_TEXTURE_Y = 117;
      int textureY = this.hovered ? 132 : 117;
      Draw.texturedModalRect(this.x, yOffset, this.textureIndex * 18, textureY, 18, 15);
      if (this.selectedTransitionTween > 0.0F) {
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
         GlStateManager.color(1.0F, 1.0F, 1.0F, this.selectedTransitionTween);
         Draw.texturedModalRect(this.x, yOffset, this.textureIndex * 18, 147, 18, 15);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.disableBlend();
      }

   }

   public boolean mouseClicked(int mouseX, int mouseY, int button) {
      this.update(mouseX, mouseY);
      return this.hovered;
   }

   public void setSelected(boolean selected) {
      this.selected = selected;
   }
}

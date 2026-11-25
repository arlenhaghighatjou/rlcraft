package betterquesting.api2.client.gui.panels.content;

import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.client.gui.resources.colors.IGuiColor;
import betterquesting.api2.client.gui.resources.textures.IGuiTexture;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;

public class PanelGeneric implements IGuiPanel {
   private final IGuiRect transform;
   private boolean enabled;
   private IGuiTexture texture;
   private IGuiColor color;
   private List<String> tooltip;

   public PanelGeneric(IGuiRect rect, IGuiTexture texture) {
      this(rect, texture, (IGuiColor)null);
   }

   public PanelGeneric(IGuiRect rect, IGuiTexture texture, IGuiColor color) {
      this.enabled = true;
      this.tooltip = null;
      this.transform = rect;
      this.texture = texture;
      this.color = color;
   }

   public void setTooltip(List<String> tooltip) {
      this.tooltip = tooltip;
   }

   public void setTexture(IGuiTexture texture, IGuiColor color) {
      this.texture = texture;
      this.color = color;
   }

   public IGuiRect getTransform() {
      return this.transform;
   }

   public void initPanel() {
   }

   public void setEnabled(boolean state) {
      this.enabled = state;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void drawPanel(int mx, int my, float partialTick) {
      if (this.texture != null) {
         GlStateManager.pushMatrix();
         if (this.color != null) {
            this.texture.drawTexture(this.transform.getX(), this.transform.getY(), this.transform.getWidth(), this.transform.getHeight(), 0.0F, partialTick, this.color);
         } else {
            this.texture.drawTexture(this.transform.getX(), this.transform.getY(), this.transform.getWidth(), this.transform.getHeight(), 0.0F, partialTick);
         }

         GlStateManager.popMatrix();
      }

   }

   public boolean onMouseClick(int mx, int my, int button) {
      return false;
   }

   public boolean onMouseRelease(int mx, int my, int button) {
      return false;
   }

   public boolean onMouseScroll(int mx, int my, int scroll) {
      return false;
   }

   public boolean onKeyTyped(char c, int keycode) {
      return false;
   }

   public List<String> getTooltip(int mx, int my) {
      return this.transform.contains(mx, my) ? this.tooltip : null;
   }
}

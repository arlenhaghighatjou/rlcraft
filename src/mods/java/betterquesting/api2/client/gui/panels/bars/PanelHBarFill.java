package betterquesting.api2.client.gui.panels.bars;

import betterquesting.api.utils.RenderUtils;
import betterquesting.api2.client.gui.controls.IValueIO;
import betterquesting.api2.client.gui.misc.GuiRectangle;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.resources.colors.GuiColorStatic;
import betterquesting.api2.client.gui.resources.colors.IGuiColor;
import betterquesting.api2.client.gui.resources.textures.IGuiTexture;
import betterquesting.api2.client.gui.themes.presets.PresetTexture;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.util.math.MathHelper;

public class PanelHBarFill implements IBarFill {
   private final IGuiRect transform;
   private boolean enabled = true;
   private IGuiTexture texBack = PresetTexture.METER_H_0.getTexture();
   private IGuiTexture texFill = PresetTexture.METER_H_1.getTexture();
   private IValueIO<Float> fillDriver;
   private boolean flipBar = false;
   private IGuiColor color = new GuiColorStatic(-1);

   public PanelHBarFill(IGuiRect rect) {
      this.transform = rect;
   }

   public PanelHBarFill setFillDriver(IValueIO<Float> driver) {
      this.fillDriver = driver;
      return this;
   }

   public PanelHBarFill setFlipped(boolean flipped) {
      this.flipBar = flipped;
      return this;
   }

   public PanelHBarFill setFillColor(IGuiColor color) {
      this.color = color;
      return this;
   }

   public PanelHBarFill setBarTexture(IGuiTexture back, IGuiTexture front) {
      this.texBack = back;
      this.texFill = front;
      return this;
   }

   public void initPanel() {
   }

   public void setEnabled(boolean state) {
      this.enabled = state;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public IGuiRect getTransform() {
      return this.transform;
   }

   public void drawPanel(int mx, int my, float partialTick) {
      IGuiRect bounds = this.getTransform();
      GlStateManager.pushMatrix();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      if (this.texBack != null) {
         this.texBack.drawTexture(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), 0.0F, partialTick);
      }

      float f = MathHelper.clamp(((Float)this.fillDriver.readValue()).floatValue(), 0.0F, 1.0F);
      if (this.flipBar) {
         RenderUtils.startScissor(new GuiRectangle(bounds.getX() + (int)((float)bounds.getWidth() - (float)bounds.getWidth() * f), bounds.getY(), (int)((float)bounds.getWidth() * f), bounds.getHeight(), 0));
      } else {
         RenderUtils.startScissor(new GuiRectangle(bounds.getX(), bounds.getY(), (int)((float)bounds.getWidth() * f), bounds.getHeight(), 0));
      }

      if (this.texFill != null) {
         this.texFill.drawTexture(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), 0.0F, partialTick, this.color);
      }

      RenderUtils.endScissor();
      GlStateManager.popMatrix();
   }

   public boolean onMouseClick(int mx, int my, int click) {
      return false;
   }

   public boolean onMouseRelease(int mx, int my, int click) {
      return false;
   }

   public boolean onMouseScroll(int mx, int my, int scroll) {
      return false;
   }

   public boolean onKeyTyped(char c, int keycode) {
      return false;
   }

   public List<String> getTooltip(int mx, int my) {
      return null;
   }
}

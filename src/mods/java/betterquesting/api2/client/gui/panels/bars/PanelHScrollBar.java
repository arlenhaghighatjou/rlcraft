package betterquesting.api2.client.gui.panels.bars;

import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.resources.textures.IGuiTexture;
import betterquesting.api2.client.gui.themes.presets.PresetTexture;
import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Mouse;

public class PanelHScrollBar implements IScrollBar {
   private final IGuiRect transform;
   private boolean enabled = true;
   private boolean active = true;
   private IGuiTexture texBack;
   private IGuiTexture[] texHandleState = new IGuiTexture[3];
   private float scroll = 0.0F;
   private float speed = 0.1F;
   private int hSize = 16;
   private int inset = 0;
   private boolean isDragging = false;

   public PanelHScrollBar(IGuiRect rect) {
      this.transform = rect;
      this.setBarTexture(PresetTexture.SCROLL_H_BG.getTexture(), PresetTexture.SCROLL_H_0.getTexture(), PresetTexture.SCROLL_H_1.getTexture(), PresetTexture.SCROLL_H_2.getTexture());
   }

   public PanelHScrollBar setHandleSize(int size, int inset) {
      this.hSize = size;
      this.inset = inset;
      return this;
   }

   public PanelHScrollBar setBarTexture(IGuiTexture back, IGuiTexture handleDisabled, IGuiTexture handleIdle, IGuiTexture handleHover) {
      this.texBack = back;
      this.texHandleState[0] = handleDisabled;
      this.texHandleState[1] = handleIdle;
      this.texHandleState[2] = handleHover;
      return this;
   }

   public PanelHScrollBar setScrollSpeed(float f) {
      this.speed = f;
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

   public void setActive(boolean state) {
      this.active = state;
   }

   public boolean isActive() {
      return this.active;
   }

   public IGuiRect getTransform() {
      return this.transform;
   }

   public void drawPanel(int mx, int my, float partialTick) {
      IGuiRect bounds = this.getTransform();
      if (!this.active || !this.isDragging || !Mouse.isButtonDown(0) && !Mouse.isButtonDown(2)) {
         if (this.isDragging) {
            this.isDragging = false;
         }
      } else {
         float cx = (float)(mx - (bounds.getX() + this.hSize / 2)) / (float)(bounds.getWidth() - this.hSize);
         this.writeValue(Float.valueOf(cx));
      }

      GlStateManager.pushMatrix();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      if (this.texBack != null) {
         this.texBack.drawTexture(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), 0.0F, partialTick);
      }

      int sx = MathHelper.floor((float)(bounds.getWidth() - this.hSize - this.inset * 2) * this.scroll);
      int state = !this.active ? 0 : (!this.isDragging && !bounds.contains(mx, my) ? 1 : 2);
      IGuiTexture tex = this.texHandleState[state];
      if (tex != null) {
         tex.drawTexture(bounds.getX() + sx + this.inset, bounds.getY() + this.inset, this.hSize, bounds.getHeight() - this.inset * 2, 0.0F, partialTick);
      }

      GlStateManager.popMatrix();
   }

   public boolean onMouseClick(int mx, int my, int click) {
      IGuiRect bounds = this.getTransform();
      if (this.active && bounds.contains(mx, my)) {
         if (click != 0 && click != 2) {
            return false;
         } else {
            this.isDragging = true;
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean onMouseRelease(int mx, int my, int click) {
      return false;
   }

   public boolean onMouseScroll(int mx, int my, int sdx) {
      IGuiRect bounds = this.getTransform();
      if (this.active && sdx != 0 && bounds.contains(mx, my)) {
         float dx = (float)sdx * this.speed;
         if ((dx >= 0.0F || this.scroll > 0.0F) && (dx <= 0.0F || this.scroll < 1.0F)) {
            this.writeValue(Float.valueOf(dx + this.scroll));
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean onKeyTyped(char c, int keycode) {
      return false;
   }

   public List<String> getTooltip(int mx, int my) {
      return null;
   }

   public Float readValue() {
      return this.scroll;
   }

   public void writeValue(Float value) {
      this.scroll = MathHelper.clamp(value.floatValue(), 0.0F, 1.0F);
   }

   public Float readValueRaw() {
      return this.readValue();
   }

   public void writeValueRaw(Float value) {
      this.scroll = value.floatValue();
   }
}

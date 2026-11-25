package betterquesting.api2.client.gui.panels.content;

import betterquesting.api.utils.RenderUtils;
import betterquesting.api2.client.gui.controls.IValueIO;
import betterquesting.api2.client.gui.controls.io.ValueFuncIO;
import betterquesting.api2.client.gui.misc.GuiRectangle;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import java.util.List;
import java.util.concurrent.Callable;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class PanelEntityPreview implements IGuiPanel {
   private final IGuiRect transform;
   private boolean enabled = true;
   public Entity entity;
   private final IValueIO<Float> basePitch;
   private final IValueIO<Float> baseYaw;
   private IValueIO<Float> pitchDriver;
   private IValueIO<Float> yawDriver;
   private float zDepth = 100.0F;

   public PanelEntityPreview(IGuiRect rect, Entity entity) {
      this.transform = rect;
      this.entity = entity;
      this.basePitch = new ValueFuncIO<Float>(() -> {
         return 15.0F;
      });
      this.pitchDriver = this.basePitch;
      this.baseYaw = new ValueFuncIO<Float>(() -> {
         return -30.0F;
      });
      this.yawDriver = this.baseYaw;
   }

   public PanelEntityPreview setRotationFixed(float pitch, float yaw) {
      this.pitchDriver = this.basePitch;
      this.yawDriver = this.baseYaw;
      this.basePitch.writeValue(Float.valueOf(pitch));
      this.baseYaw.writeValue(Float.valueOf(yaw));
      return this;
   }

   public PanelEntityPreview setRotationDriven(IValueIO<Float> pitch, IValueIO<Float> yaw) {
      this.pitchDriver = pitch == null ? this.basePitch : pitch;
      this.yawDriver = yaw == null ? this.baseYaw : yaw;
      return this;
   }

   public PanelEntityPreview setDepth(float z) {
      this.zDepth = z;
      return this;
   }

   public void setEntity(Entity entity) {
      this.entity = entity;
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
      if (this.entity != null) {
         IGuiRect bounds = this.getTransform();
         GlStateManager.pushMatrix();
         RenderUtils.startScissor(new GuiRectangle(bounds));
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         int sizeX = bounds.getWidth();
         int sizeY = bounds.getHeight();
         float scale = Math.min((float)sizeY / 2.0F / this.entity.height, (float)sizeX / 2.0F / this.entity.width);
         RenderUtils.RenderEntity(bounds.getX() + sizeX / 2, bounds.getY() + sizeY / 2 + MathHelper.ceil(this.entity.height * scale / 2.0F), (int)scale, ((Float)this.yawDriver.readValue()).floatValue(), ((Float)this.pitchDriver.readValue()).floatValue(), this.entity);
         RenderUtils.endScissor();
         GlStateManager.popMatrix();
      }
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

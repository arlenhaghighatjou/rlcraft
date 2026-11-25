package bqtweaker.client.gui.panels;

import betterquesting.api.utils.RenderUtils;
import betterquesting.api2.client.gui.controls.IValueIO;
import betterquesting.api2.client.gui.controls.io.ValueFuncIO;
import betterquesting.api2.client.gui.misc.GuiRectangle;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.utils.EntityPlayerPreview;
import bqtweaker.client.util.RenderUtilsOverride;
import net.lax1dude.eaglercraft.v1_8.mojang.authlib.GameProfile;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import java.util.concurrent.Callable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class PanelPlayerPortraitOverride implements IGuiPanel {
   private final IGuiRect transform;
   private boolean enabled;
   private final AbstractClientPlayer player;
   private final IValueIO<Float> basePitch;
   private final IValueIO<Float> baseYaw;
   private IValueIO<Float> pitchDriver;
   private IValueIO<Float> yawDriver;
   private float zDepth;

   public PanelPlayerPortraitOverride(IGuiRect rect, EaglercraftUUID playerID, String username) {
      this(rect, new EntityPlayerPreview(Minecraft.getMinecraft().world, new GameProfile(playerID, username)));
   }

   public PanelPlayerPortraitOverride(IGuiRect rect, AbstractClientPlayer player) {
      this.enabled = true;
      this.zDepth = 100.0F;
      this.transform = rect;
      this.player = new EntityPlayerPreview(player.world, player.getGameProfile());
      this.player.limbSwing = 0.0F;
      this.player.limbSwingAmount = 0.0F;
      this.player.rotationYawHead = 0.0F;
      ResourceLocation resource = this.player.getLocationSkin();
      if (Minecraft.getMinecraft().getTextureManager().getTexture(resource) == null) {
         AbstractClientPlayer.getDownloadImageSkin(resource, player.getGameProfile().getName());
      }

      this.basePitch = new ValueFuncIO(() -> {
         return 15.0F;
      });
      this.pitchDriver = this.basePitch;
      this.baseYaw = new ValueFuncIO(() -> {
         return -30.0F;
      });
      this.yawDriver = this.baseYaw;
   }

   public PanelPlayerPortraitOverride setRotationFixed(float pitch, float yaw) {
      this.pitchDriver = this.basePitch;
      this.yawDriver = this.baseYaw;
      this.basePitch.writeValue(Float.valueOf(pitch));
      this.baseYaw.writeValue(Float.valueOf(yaw));
      return this;
   }

   public PanelPlayerPortraitOverride setRotationDriven(IValueIO<Float> pitch, IValueIO<Float> yaw) {
      this.pitchDriver = pitch == null ? this.basePitch : pitch;
      this.yawDriver = yaw == null ? this.baseYaw : yaw;
      return this;
   }

   public PanelPlayerPortraitOverride setDepth(float z) {
      this.zDepth = z;
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
      RenderUtils.startScissor(new GuiRectangle(bounds));
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      int scale = Math.min(bounds.getWidth(), bounds.getHeight());
      RenderUtilsOverride.RenderEntity((float)(bounds.getX() + bounds.getWidth() / 2), (float)(bounds.getY() + bounds.getHeight() / 2 + (int)((float)scale * 1.5F)), this.zDepth, scale, ((Float)this.yawDriver.readValue()).floatValue(), ((Float)this.pitchDriver.readValue()).floatValue(), this.player);
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

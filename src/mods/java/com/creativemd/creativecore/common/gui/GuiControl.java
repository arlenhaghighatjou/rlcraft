package com.creativemd.creativecore.common.gui;

import com.creativemd.creativecore.common.gui.client.style.DisplayStyle;
import com.creativemd.creativecore.common.gui.client.style.Style;
import com.creativemd.creativecore.common.gui.container.GuiParent;
import com.creativemd.creativecore.common.gui.container.SubGui;
import com.creativemd.creativecore.common.gui.event.gui.GuiToolTipEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.vecmath.Vector2d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;

@SideOnly(Side.CLIENT)
public abstract class GuiControl extends CoreControl {
   public static final ResourceLocation guiUtilsImage = new ResourceLocation("creativecore", "textures/gui/utils.png");
   public static Minecraft mc = Minecraft.getMinecraft();
   public static FontRenderer font;
   public static Style defaultStyle = Style.liteStyle;
   public int posX;
   public int posY;
   public int width;
   public int height;
   public float rotation;
   public boolean visible;
   protected Style style = null;
   protected int borderWidth = 1;
   protected int marginWidth = 2;
   protected ArrayList<String> customTooltip = null;
   private static final Rect screenRect = new Rect(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);

   public GuiControl(String name, int x, int y, int width, int height) {
      super(name);
      this.posX = x;
      this.posY = y;
      this.width = width + this.getContentOffset() * 2;
      this.height = height + this.getContentOffset() * 2;
      this.rotation = 0.0F;
      this.visible = true;
   }

   public GuiControl setEnabled(boolean enabled) {
      return (GuiControl)super.setEnabled(enabled);
   }

   public GuiControl setDimension(int width, int height) {
      this.width = width + this.getContentOffset() * 2;
      this.height = height + this.getContentOffset() * 2;
      return this;
   }

   public GuiControl setRotation(float rotation) {
      this.rotation = rotation;
      return this;
   }

   public GuiControl setStyle(Style style) {
      this.style = style;
      return this;
   }

   public GuiControl setLangTooltip(String key) {
      String translate = translateOrNull(key);
      if (translate != null) {
         this.customTooltip = new ArrayList(Arrays.asList(translate.split("\\n")));
      }

      return this;
   }

   public GuiControl setCustomTooltip(String... lines) {
      this.customTooltip = new ArrayList(Arrays.asList(lines));
      return this;
   }

   public GuiControl setVisible(boolean value) {
      this.visible = value;
      return this;
   }

   public boolean isInteractable() {
      return super.isInteractable() && this.visible;
   }

   public Vec3d getMousePos() {
      return this.getParent().getMousePos();
   }

   public Vec3d rotateMouseVec(Vec3d mouse) {
      if (this.rotation == 0.0F) {
         return mouse;
      } else {
         Vec3d centerOffset = this.getCenterOffset();
         return getRotationAround((double)(-this.rotation), mouse, new Vec3d((double)this.posX + centerOffset.x, (double)this.posY + centerOffset.y, 0.0D));
      }
   }

   public boolean isMouseOver() {
      Vec3d mouse = this.getParent().getMousePos();
      Vec3d pos = this.rotateMouseVec(mouse);
      return this.isInteractable() && this.visible && this.isMouseOver((int)pos.x, (int)pos.y);
   }

   public boolean isMouseOver(int posX, int posY) {
      return posX >= this.posX && posX < this.posX + this.width && posY >= this.posY && posY < this.posY + this.height;
   }

   public SubGui getGui() {
      if (this.parent != null) {
         return ((GuiControl)this.parent).getGui();
      } else {
         return this instanceof SubGui ? (SubGui)this : null;
      }
   }

   public GuiParent getParent() {
      return (GuiParent)this.parent;
   }

   public void openClientLayer(SubGui gui) {
      this.getGui().openClientLayer(gui);
   }

   public Style getDefaultStyle() {
      return defaultStyle;
   }

   public boolean hasStyle() {
      if (this.parent != null && this.getParent().hasStyle()) {
         return true;
      } else {
         return this.style != null;
      }
   }

   public Style getStyle() {
      if (this.style != null) {
         return this.style;
      } else {
         return this.parent != null && this.hasStyle() ? this.getParent().getStyle() : this.getDefaultStyle();
      }
   }

   public DisplayStyle getBorderDisplay(DisplayStyle display) {
      return display;
   }

   public boolean hasBorder() {
      return true;
   }

   public boolean hasBackground() {
      return true;
   }

   public boolean hasMouseOverEffect() {
      return true;
   }

   public boolean canOverlap() {
      return false;
   }

   public GuiToolTipEvent getToolTipEvent() {
      List<String> toolTip = this.getTooltip();
      if (this.customTooltip != null) {
         if (toolTip == null) {
            toolTip = new ArrayList(this.customTooltip);
         } else {
            toolTip.addAll(this.customTooltip);
         }
      }

      return toolTip != null && toolTip.size() > 0 ? new GuiToolTipEvent(toolTip, this) : null;
   }

   public List<String> getTooltip() {
      return null;
   }

   public Vec3d getCenterOffset() {
      return new Vec3d((double)this.width / 2.0D, (double)this.height / 2.0D, 0.0D);
   }

   public int getContentOffset() {
      return this.borderWidth + this.marginWidth;
   }

   public boolean isVisibleInsideRect(int x, int y, int width, int height, float scale) {
      return (float)(this.posX + this.width) * scale >= (float)x * scale && (float)this.posX * scale <= (float)x * scale + (float)width && (float)(this.posY + this.height) * scale >= (float)y * scale && (float)this.posY * scale <= (float)y * scale + (float)height;
   }

   public int getGuiScale() {
      ScaledResolution resolution = new ScaledResolution(mc);
      return resolution.getScaleFactor();
   }

   public int getPixelOffsetX() {
      int x = this.posX + this.getContentOffset();
      if (this.hasParent()) {
         if (this.parent instanceof GuiParent) {
            x = (int)((double)x + ((GuiParent)this.parent).getOffsetX());
         }

         x += ((GuiControl)this.parent).getPixelOffsetX();
      }

      return x;
   }

   public int getPixelOffsetY() {
      int y = this.posY + this.getContentOffset();
      if (this.hasParent()) {
         if (this.parent instanceof GuiParent) {
            y = (int)((double)y + ((GuiParent)this.parent).getOffsetY());
         }

         y += ((GuiControl)this.parent).getPixelOffsetY();
      }

      return y;
   }

   protected abstract void renderContent(GuiRenderHelper var1, Style var2, int var3, int var4);

   protected void renderContent(GuiRenderHelper helper, Style style, int width, int height, Rect relativeMaximumRect) {
      this.renderContent(helper, style, width, height);
   }

   protected void renderBackground(GuiRenderHelper helper, Style style) {
      if (this.hasBorder()) {
         style.getBorder(this).renderStyle(helper, this.width, this.height);
      }

      GlStateManager.translate((float)this.borderWidth, (float)this.borderWidth, 0.0F);
      if (this.hasBackground()) {
         style.getBackground(this).renderStyle(helper, this.width - this.borderWidth * 2, this.height - this.borderWidth * 2);
      }

   }

   protected void renderForeground(GuiRenderHelper helper, Style style) {
      if (!this.enabled) {
         style.getDisableEffect(this).renderStyle(helper, this.width, this.height);
      }

   }

   protected Rect getRect() {
      return new Rect(0, 0, this.width - this.getContentOffset() * 2, this.height - this.getContentOffset() * 2);
   }

   protected void prepareContentStencil(GuiRenderHelper helper, Rect maximumRect) {
      int var10000 = this.borderWidth + this.marginWidth;
      GL11.glClear(1024);
      GL11.glStencilOp(7681, 7681, 7681);
      GL11.glStencilFunc(514, 1, 1);
      GlStateManager.colorMask(false, false, false, false);
      maximumRect.renderRect(helper, new Color(0, 0, 0, 255));
      GlStateManager.colorMask(true, true, true, true);
   }

   public void renderControl(GuiRenderHelper helper, float scale, Rect relativeMaximumRect) {
      if (this.visible) {
         Style style = this.getStyle();
         GlStateManager.pushMatrix();
         if (scale != 0.0F) {
            GlStateManager.scale(scale, scale, 1.0F);
         }

         GlStateManager.translate((float)this.posX, (float)this.posY, 0.0F);
         if (this.rotation != 0.0F) {
            Vec3d centerOffset = this.getCenterOffset();
            GlStateManager.translate(centerOffset.x, centerOffset.y, 0.0D);
            GlStateManager.rotate(this.rotation, 0.0F, 0.0F, 1.0F);
            GlStateManager.translate(-centerOffset.x, -centerOffset.y, 0.0D);
         }

         this.renderBackground(helper, style);
         int spaceUsed = this.getContentOffset();
         GlStateManager.translate((float)this.marginWidth, (float)this.marginWidth, 0.0F);
         if (!this.canOverlap()) {
            GL11.glEnable(2960);
            GL11.glStencilOp(7680, 7680, 7680);
            GL11.glStencilFunc(514, 1, 1);
         } else {
            GL11.glDisable(2960);
         }

         this.renderContent(helper, style, this.width - spaceUsed * 2, this.height - spaceUsed * 2, relativeMaximumRect);
         GL11.glDisable(2960);
         GlStateManager.translate((float)(-spaceUsed), (float)(-spaceUsed), 0.0F);
         this.renderForeground(helper, style);
         GlStateManager.popMatrix();
      }
   }

   public void onLoseFocus() {
   }

   public boolean mouseScrolled(int x, int y, int scrolled) {
      return false;
   }

   public boolean mousePressed(int x, int y, int button) {
      return false;
   }

   public void mouseMove(int x, int y, int button) {
   }

   public void mouseReleased(int x, int y, int button) {
   }

   public void mouseDragged(int x, int y, int button, long time) {
   }

   public boolean onKeyPressed(char character, int key) {
      return false;
   }

   public static Rect getScreenRect() {
      return screenRect;
   }

   public static void playSound(ISound sound) {
      mc.getSoundHandler().playSound(sound);
   }

   public static void playSound(SoundEvent event) {
      mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(event, 1.0F));
   }

   public static void playSound(SoundEvent event, float volume, float pitch) {
      mc.getSoundHandler().playSound(PositionedSoundRecord.getRecord(event, pitch, volume));
   }

   public static Vec3d getRotationAround(double angle, Vec3d pos, Vec3d center) {
      Vector2d result = new Vector2d(pos.x, pos.y);
      Vector2d newCenter = new Vector2d(center.x, center.y);
      result.sub(newCenter);
      Vector2d temp = new Vector2d(result);
      result.x = Math.cos(Math.toRadians(angle)) * temp.x - Math.sin(Math.toRadians(angle)) * temp.y;
      result.y = Math.sin(Math.toRadians(angle)) * temp.x + Math.cos(Math.toRadians(angle)) * temp.y;
      result.add(newCenter);
      return new Vec3d(result.x, result.y, 0.0D);
   }

   static {
      font = mc.fontRenderer;
   }
}

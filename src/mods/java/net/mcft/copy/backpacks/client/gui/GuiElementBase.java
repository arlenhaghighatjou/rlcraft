package net.mcft.copy.backpacks.client.gui;

import net.lax1dude.eaglercraft.v1_8.opengl.RealOpenGLEnums;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class GuiElementBase {
   public static final String ELLIPSIS = "...";
   public static final int ELLIPSIS_WIDTH = getStringWidth("...");
   public static final int LINE_HEIGHT;
   public static final int COLOR_CONTROL = -2039584;
   public static final int COLOR_CONTROL_HIGHLIGHT = -96;
   public static final int COLOR_CONTROL_DISABLED = -6250336;
   private GuiContext _context;
   private GuiContainer _parent;
   private boolean _visible = true;
   private boolean _enabled = true;
   private int _width;
   private int _height;
   private Alignment _horizontalAlign = new Alignment.Min(0);
   private Alignment _verticalAlign = new Alignment.Min(0);
   private List<String> _tooltip = null;
   private Supplier<List<String>> _tooltipFunc = null;
   private int _tooltipDelay = 400;
   private int _dragStartX;
   private int _dragStartY;
   private int _dragPrevX;
   private int _dragPrevY;

   public GuiElementBase() {
   }

   void setContext(GuiContext value) {
      this._context = value;
   }

   void setParent(GuiContainer value) {
      this._parent = value;
   }

   public final GuiContext getContext() {
      return this._context;
   }

   public final GuiContainer getParent() {
      return this._parent;
   }

   public boolean isVisible() {
      return this._visible;
   }

   public void setVisible(boolean value) {
      this._visible = value;
   }

   public boolean isEnabled() {
      return this._enabled && (this.getParent() == null || this.getParent().isEnabled());
   }

   public void setEnabled(boolean value) {
      this._enabled = value;
   }

   public int getSize(Direction direction) {
      return direction == Direction.HORIZONTAL ? this._width : this._height;
   }

   public final int getWidth() {
      return this.getSize(Direction.HORIZONTAL);
   }

   public final int getHeight() {
      return this.getSize(Direction.VERTICAL);
   }

   public void setSize(Direction direction, int value) {
      if (value != this.getSize(direction)) {
         if (direction == Direction.HORIZONTAL) {
            this._width = value;
         } else {
            this._height = value;
         }

         this.onSizeChanged(direction);
         if (this._parent != null) {
            this._parent.onChildSizeChanged(this, direction);
         }

      }
   }

   public final void setWidth(int value) {
      this.setSize(Direction.HORIZONTAL, value);
   }

   public final void setHeight(int value) {
      this.setSize(Direction.VERTICAL, value);
   }

   public final void setSize(int width, int height) {
      this.setWidth(width);
      this.setHeight(height);
   }

   public Alignment getAlign(Direction direction) {
      return direction == Direction.HORIZONTAL ? this._horizontalAlign : this._verticalAlign;
   }

   public final Alignment getHorizontalAlign() {
      return this.getAlign(Direction.HORIZONTAL);
   }

   public final Alignment getVerticalAlign() {
      return this.getAlign(Direction.VERTICAL);
   }

   public void setAlign(Direction direction, Alignment value) {
      if (value == null) {
         throw new NullPointerException("Argument must be non-null");
      } else {
         if (direction == Direction.HORIZONTAL) {
            this._horizontalAlign = value;
         } else {
            this._verticalAlign = value;
         }

         if (this._parent != null) {
            this._parent.onChildAlignChanged(this, direction);
         }

      }
   }

   public final void setAlignHorizontal(Alignment value) {
      this.setAlign(Direction.HORIZONTAL, value);
   }

   public final void setAlignVertical(Alignment value) {
      this.setAlign(Direction.VERTICAL, value);
   }

   public final void setAlign(Alignment horizontal, Alignment vertical) {
      this.setAlignHorizontal(horizontal);
      this.setAlignVertical(vertical);
   }

   public final void setPosition(int left, int top) {
      this.setAlign(new Alignment.Min(left), new Alignment.Min(top));
   }

   public final void setLeft(int value) {
      this.setAlignHorizontal(new Alignment.Min(value));
   }

   public final void setRight(int value) {
      this.setAlignHorizontal(new Alignment.Max(value));
   }

   public final void setTop(int value) {
      this.setAlignVertical(new Alignment.Min(value));
   }

   public final void setBottom(int value) {
      this.setAlignVertical(new Alignment.Max(value));
   }

   public final void setLeftRight(int value) {
      this.setLeftRight(value, value);
   }

   public final void setLeftRight(int left, int right) {
      this.setAlignHorizontal(new Alignment.Both(left, right));
   }

   public final void setTopBottom(int value) {
      this.setTopBottom(value, value);
   }

   public final void setTopBottom(int top, int bottom) {
      this.setAlignVertical(new Alignment.Both(top, bottom));
   }

   public final void setCenteredHorizontal() {
      this.setAlignHorizontal(new Alignment.Center());
   }

   public final void setCenteredHorizontal(int width) {
      this.setCenteredHorizontal();
      this.setWidth(width);
   }

   public final void setCenteredVertical() {
      this.setAlignVertical(new Alignment.Center());
   }

   public final void setCenteredVertical(int height) {
      this.setCenteredVertical();
      this.setHeight(height);
   }

   public final void setFillHorizontal() {
      this.setLeftRight(0);
   }

   public final void setFillVertical() {
      this.setTopBottom(0);
   }

   public final void setFill() {
      this.setFillHorizontal();
      this.setFillVertical();
   }

   public boolean hasTooltip() {
      return !this.getTooltip().isEmpty();
   }

   public List<String> getTooltip() {
      List<String> tooltip = this._tooltipFunc != null ? (List)this._tooltipFunc.get() : this._tooltip;
      if (tooltip == null) {
         tooltip = Collections.emptyList();
      }

      return tooltip;
   }

   public int getTooltipDelay() {
      return this._tooltipDelay;
   }

   public void setTooltip(List<String> value) {
      this.setTooltip(400, value);
   }

   public void setTooltip(int delay, List<String> value) {
      this._tooltip = value;
      this._tooltipDelay = delay;
   }

   public void setTooltip(Supplier<List<String>> value) {
      this.setTooltip(400, value);
   }

   public void setTooltip(int delay, Supplier<List<String>> value) {
      this._tooltipFunc = value;
      this._tooltipDelay = delay;
   }

   public boolean canFocus() {
      return false;
   }

   public boolean isFocused() {
      return this.getContext().getFocused() == this;
   }

   public void setFocused() {
      this.setFocused(true);
   }

   public void setFocused(boolean value) {
      if (value && !this.canFocus()) {
         throw new UnsupportedOperationException("This element can't be focused");
      } else if (this.isVisible()) {
         this.getContext().setFocused(value ? this : null);
      }
   }

   public boolean canPress() {
      return this.canDrag() || this.canFocus();
   }

   public boolean isPressed() {
      return this.getContext() != null && this.getContext().getPressed() == this;
   }

   public void onPressed(int mouseX, int mouseY) {
   }

   public boolean canDrag() {
      return false;
   }

   public boolean isDragged() {
      return this.canDrag() && this.isPressed();
   }

   public void onDragged(int mouseX, int mouseY, int deltaX, int deltaY, int startX, int startY) {
   }

   public void onSizeChanged(Direction direction) {
   }

   public boolean onMouseDown(int mouseButton, int mouseX, int mouseY) {
      if (this.isVisible() && this.isEnabled()) {
         if (mouseButton == GuiElementBase.MouseButton.LEFT && this.canPress()) {
            GuiContext context = this.getContext();
            context.setPressed(this);
            if (this.canDrag()) {
               this._dragStartX = mouseX;
               this._dragStartY = mouseY;
               this._dragPrevX = mouseX;
               this._dragPrevY = mouseY;
            }

            if (this.canFocus()) {
               this.setFocused();
            }

            this.onPressed(mouseX, mouseY);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void onMouseMove(int mouseX, int mouseY) {
      if (this.isVisible() && this.isDragged()) {
         this.onDragged(mouseX, mouseY, mouseX - this._dragPrevX, mouseY - this._dragPrevY, this._dragStartX, this._dragStartY);
         this._dragPrevX = mouseX;
         this._dragPrevY = mouseY;
      }

   }

   public void onMouseUp(int mouseButton, int mouseX, int mouseY) {
   }

   public boolean onMouseScroll(int scroll, int mouseX, int mouseY) {
      return false;
   }

   public void onKey(int keyCode, char keyChar) {
   }

   public void draw(int mouseX, int mouseY, float partialTicks) {
   }

   public void drawTooltip(int mouseX, int mouseY, int screenWidth, int screenHeight, float partialTicks) {
      if (this.hasTooltip()) {
         GuiUtils.drawHoveringText(this.getTooltip(), mouseX, mouseY, screenWidth, screenHeight, 300, getFontRenderer());
      }
   }

   public static boolean regionContains(int x, int y, int width, int height, int testX, int testY) {
      return testX >= x && testX < x + width && testY >= y && testY < y + height;
   }

   public boolean contains(int x, int y) {
      return regionContains(0, 0, this.getWidth(), this.getHeight(), x, y);
   }

   public boolean isHighlighted(int mouseX, int mouseY) {
      GuiElementBase pressed = this.getContext().getPressed();
      return this.isEnabled() && pressed == this || pressed == null && this.contains(mouseX, mouseY);
   }

   public static Minecraft getMC() {
      return Minecraft.getMinecraft();
   }

   public static void display(GuiScreen screen) {
      getMC().displayGuiScreen(screen);
   }

   public static GuiScreen getCurrentScreen() {
      return getMC().currentScreen;
   }

   public static FontRenderer getFontRenderer() {
      return getMC().fontRenderer;
   }

   public static int getStringWidth(String text) {
      return getFontRenderer().getStringWidth(text);
   }

   public static void bindTexture(ResourceLocation resource) {
      getMC().getTextureManager().bindTexture(resource);
   }

   public static void setRenderColor(float red, float green, float blue) {
      setRenderColor(red, green, blue, 1.0F);
   }

   public static void setRenderColor(float red, float green, float blue, float alpha) {
      GlStateManager.color(red, green, blue, alpha);
   }

   public static void setRenderColorRGB(int color) {
      setRenderColorARGB(color | -16777216);
   }

   public static void setRenderColorARGB(int color) {
      setRenderColor((float)(color >> 16 & 255) / 255.0F, (float)(color >> 8 & 255) / 255.0F, (float)(color & 255) / 255.0F, (float)(color >> 24 & 255) / 255.0F);
   }

   public static void enableBlendAlphaStuffs() {
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(RealOpenGLEnums.GL_SRC_ALPHA, RealOpenGLEnums.GL_ONE_MINUS_SRC_ALPHA, RealOpenGLEnums.GL_ZERO, RealOpenGLEnums.GL_ONE);
      GlStateManager.disableTexture2D();
      GlStateManager.shadeModel(7425);
   }

   public static void disableBlendAlphaStuffs() {
      GlStateManager.shadeModel(7424);
      GlStateManager.enableTexture2D();
      GlStateManager.disableBlend();
   }

   public static void drawRect(int x, int y, int width, int height) {
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer buffer = tessellator.getBuffer();
      buffer.begin(7, DefaultVertexFormats.POSITION);
      buffer.pos((double)x, (double)y, 0.0D).endVertex();
      buffer.pos((double)x, (double)(y + height), 0.0D).endVertex();
      buffer.pos((double)(x + width), (double)(y + height), 0.0D).endVertex();
      buffer.pos((double)(x + width), (double)y, 0.0D).endVertex();
      tessellator.draw();
   }

   public static void drawOutline(int x, int y, int width, int height) {
      drawRect(x, y, width, 1);
      drawRect(x, y + height - 1, width, 1);
      drawRect(x, y + 1, 1, height - 2);
      drawRect(x + width - 1, y + 1, 1, height - 2);
   }

   public static void drawRect(int x, int y, int u, int v, int width, int height, int texSize) {
      drawRect(x, y, u, v, width, height, texSize, texSize);
   }

   public static void drawRect(int x, int y, int u, int v, int width, int height, int texWidth, int texHeight) {
      float u1 = (float)u / (float)texWidth;
      float v1 = (float)v / (float)texHeight;
      float u2 = (float)(u + width) / (float)texWidth;
      float v2 = (float)(v + height) / (float)texHeight;
      drawRect(x, y, width, height, u1, v1, u2, v2);
   }

   public static void drawRect(int x, int y, int width, int height, float u1, float v1, float u2, float v2) {
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer buffer = tessellator.getBuffer();
      buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
      buffer.pos((double)x, (double)y, 0.0D).tex((double)u1, (double)v1).endVertex();
      buffer.pos((double)x, (double)(y + height), 0.0D).tex((double)u1, (double)v2).endVertex();
      buffer.pos((double)(x + width), (double)(y + height), 0.0D).tex((double)u2, (double)v2).endVertex();
      buffer.pos((double)(x + width), (double)y, 0.0D).tex((double)u2, (double)v1).endVertex();
      tessellator.draw();
   }

   public static void drawColoredRectRGB(int x, int y, int width, int height, int color) {
      drawColoredRectARGB(x, y, width, height, color | -16777216);
   }

   public static void drawColoredRectARGB(int x, int y, int width, int height, int color) {
      drawColoredRectARGB(x, y, width, height, color, color, color, color);
   }

   public static void drawColoredRectARGB(int x, int y, int width, int height, int colorTL, int colorTR, int colorBL, int colorBR) {
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer buffer = tessellator.getBuffer();
      buffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
      buffer.pos((double)x, (double)y, 0.0D).color(colorTL >> 16 & 255, colorTL >> 8 & 255, colorTL & 255, colorTL >> 24 & 255).endVertex();
      buffer.pos((double)x, (double)(y + height), 0.0D).color(colorBL >> 16 & 255, colorBL >> 8 & 255, colorBL & 255, colorBL >> 24 & 255).endVertex();
      buffer.pos((double)(x + width), (double)(y + height), 0.0D).color(colorBR >> 16 & 255, colorBR >> 8 & 255, colorBR & 255, colorBR >> 24 & 255).endVertex();
      buffer.pos((double)(x + width), (double)y, 0.0D).color(colorTR >> 16 & 255, colorTR >> 8 & 255, colorTR & 255, colorTR >> 24 & 255).endVertex();
      tessellator.draw();
   }

   public static void drawColoredRectRGB(int x, int y, int width, int height, float u1, float v1, float u2, float v2, int color) {
      drawColoredRectARGB(x, y, width, height, u1, v1, u2, v2, color | -16777216);
   }

   public static void drawColoredRectARGB(int x, int y, int width, int height, float u1, float v1, float u2, float v2, int color) {
      drawColoredRectARGB(x, y, width, height, u1, v1, u2, v2, color, color, color, color);
   }

   public static void drawColoredRectARGB(int x, int y, int width, int height, float u1, float v1, float u2, float v2, int colorTL, int colorTR, int colorBL, int colorBR) {
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer buffer = tessellator.getBuffer();
      buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      buffer.pos((double)x, (double)y, 0.0D).tex((double)u1, (double)v1).color(colorTL >> 16 & 255, colorTL >> 8 & 255, colorTL & 255, colorTL >> 24 & 255).endVertex();
      buffer.pos((double)x, (double)(y + height), 0.0D).tex((double)u1, (double)v2).color(colorBL >> 16 & 255, colorBL >> 8 & 255, colorBL & 255, colorBL >> 24 & 255).endVertex();
      buffer.pos((double)(x + width), (double)(y + height), 0.0D).tex((double)u2, (double)v2).color(colorBR >> 16 & 255, colorBR >> 8 & 255, colorBR & 255, colorBR >> 24 & 255).endVertex();
      buffer.pos((double)(x + width), (double)y, 0.0D).tex((double)u2, (double)v1).color(colorTR >> 16 & 255, colorTR >> 8 & 255, colorTR & 255, colorTR >> 24 & 255).endVertex();
      tessellator.draw();
   }

   static {
      LINE_HEIGHT = getFontRenderer().FONT_HEIGHT;
   }

   public static final class Color {
      public static int WHITE = -1;
      public static int BLACK = -16777216;
      public static int TRANSPARENT = 0;

      private Color() {
      }
   }

   public static final class MouseButton {
      public static int LEFT = 0;
      public static int RIGHT = 1;
      public static int MIDDLE = 2;

      private MouseButton() {
      }
   }

   public static final class TooltipDelay {
      public static final int SHORT = 200;
      public static final int NORMAL = 400;
      public static final int LONG = 800;

      private TooltipDelay() {
      }
   }
}

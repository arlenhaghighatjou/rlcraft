package net.ilexiconn.llibrary.client.gui.element;

import java.awt.geom.RectangularShape;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.client.ClientProxy;
import net.ilexiconn.llibrary.client.gui.element.color.ColorScheme;
import net.minecraft.client.gui.ScaledResolution;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class Element<T extends IElementGUI> {
   public static final ColorScheme DEFAULT = ColorScheme.create(() -> {
      return LLibrary.CONFIG.getPrimaryColor();
   }, () -> {
      return LLibrary.CONFIG.getSecondaryColor();
   });
   protected final T gui;
   private Element<T> parent;
   private List<Element<T>> children = new ArrayList();
   private ColorScheme colorScheme;
   private float posX;
   private float posY;
   private int width;
   private int height;
   private boolean enabled;
   private boolean visible;

   public Element(T gui, float posX, float posY, int width, int height) {
      this.colorScheme = DEFAULT;
      this.enabled = true;
      this.visible = true;
      this.gui = gui;
      this.posX = posX;
      this.posY = posY;
      this.width = width;
      this.height = height;
   }

   public void init() {
   }

   public void update() {
   }

   public void render(float mouseX, float mouseY, float partialTicks) {
   }

   public void dispose() {
   }

   public boolean mouseClicked(float mouseX, float mouseY, int button) {
      return this.isSelected(mouseX, mouseY);
   }

   public boolean mouseDragged(float mouseX, float mouseY, int button, long timeSinceClick) {
      return this.isSelected(mouseX, mouseY);
   }

   public boolean mouseReleased(float mouseX, float mouseY, int button) {
      return this.isSelected(mouseX, mouseY);
   }

   public boolean keyPressed(char character, int key) {
      return false;
   }

   public boolean mouseScrolled(float mouseX, float mouseY, int amount) {
      if (this.isSelected(mouseX, mouseY)) {
         for(Element<T> child : this.getChildren()) {
            if (child instanceof ScrollbarElement) {
               ((ScrollbarElement)child).setScrollVelocity(((ScrollbarElement)child).getScrollVelocity() + (float)amount / 120.0F * 0.5F);
               return true;
            }
         }
      }

      return false;
   }

   protected boolean isSelected(float mouseX, float mouseY) {
      return this.gui.isElementOnTop(this) && mouseX >= this.getPosX() && mouseY >= this.getPosY() && mouseX < this.getPosX() + (float)this.getWidth() && mouseY < this.getPosY() + (float)this.getHeight();
   }

   protected boolean isSelected(RectangularShape rectangle, float mouseX, float mouseY) {
      return (double)mouseX >= rectangle.getMinX() && (double)mouseY >= rectangle.getMinY() && (double)mouseX < rectangle.getMaxX() && (double)mouseY < rectangle.getMaxY();
   }

   public Element<T> withParent(Element<T> parent) {
      if (this.parent != null) {
         this.parent.children.remove(this);
      }

      this.parent = parent;
      if (this.parent != null && !this.parent.children.contains(this)) {
         this.parent.children.add(this);
      }

      this.children.clear();
      this.init();
      return this;
   }

   public Element<T> withColorScheme(ColorScheme colorScheme) {
      this.colorScheme = colorScheme;
      return this;
   }

   public Element<T> getParent() {
      return this.parent;
   }

   public List<Element<T>> getChildren() {
      return this.children;
   }

   public ColorScheme getColorScheme() {
      return this.colorScheme;
   }

   public float getPosX() {
      return this.posX + (this.getParent() != null ? this.getParent().getPosX() : 0.0F);
   }

   public Element<T> setPosX(float posX) {
      this.posX = posX;
      return this;
   }

   public float getPosY() {
      return this.posY + (this.getParent() != null ? this.getParent().getPosY() : 0.0F);
   }

   public Element<T> setPosY(float posY) {
      this.posY = posY;
      return this;
   }

   public int getWidth() {
      return this.width;
   }

   public Element<T> setWidth(int width) {
      this.width = width;
      return this;
   }

   public int getHeight() {
      return this.height;
   }

   public Element<T> setHeight(int height) {
      this.height = height;
      return this;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public Element<T> setEnabled(boolean enabled) {
      this.enabled = enabled;
      return this;
   }

   public boolean isVisible() {
      return this.visible;
   }

   public Element<T> setVisible(boolean visible) {
      this.visible = visible;
      return this;
   }

   public void renderChildren(float mouseX, float mouseY, float partialTicks) {
      if (this.isVisible()) {
         this.render(mouseX, mouseY, partialTicks);

         for(Element<T> child : this.children) {
            child.renderChildren(mouseX, mouseY, partialTicks);
         }
      }

   }

   protected void drawRectangle(double x, double y, double width, double height, int color) {
      GlStateManager.disableTexture2D();
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      float a = (float)(color >> 24 & 255) / 255.0F;
      float r = (float)(color >> 16 & 255) / 255.0F;
      float g = (float)(color >> 8 & 255) / 255.0F;
      float b = (float)(color & 255) / 255.0F;
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer WorldRenderer = tessellator.getBuffer();
      WorldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
      WorldRenderer.pos(x, y + height, 0.0D).color(r, g, b, a).endVertex();
      WorldRenderer.pos(x + width, y + height, 0.0D).color(r, g, b, a).endVertex();
      WorldRenderer.pos(x + width, y, 0.0D).color(r, g, b, a).endVertex();
      WorldRenderer.pos(x, y, 0.0D).color(r, g, b, a).endVertex();
      tessellator.draw();
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
      GlStateManager.enableTexture2D();
   }

   protected void drawRectangle(RectangularShape rectangle, int color) {
      this.drawRectangle(rectangle.getMinX(), rectangle.getMinY(), rectangle.getWidth(), rectangle.getHeight(), color);
   }

   protected void drawTexturedRectangle(double x, double y, double width, double height, int color) {
      GlStateManager.enableTexture2D();
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      float a = (float)(color >> 24 & 255) / 255.0F;
      float r = (float)(color >> 16 & 255) / 255.0F;
      float g = (float)(color >> 8 & 255) / 255.0F;
      float b = (float)(color & 255) / 255.0F;
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer WorldRenderer = tessellator.getBuffer();
      WorldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      WorldRenderer.pos(x, y + height, 0.0D).tex(0.0D, 1.0D).color(r, g, b, a).endVertex();
      WorldRenderer.pos(x + width, y + height, 0.0D).tex(1.0D, 1.0D).color(r, g, b, a).endVertex();
      WorldRenderer.pos(x + width, y, 0.0D).tex(1.0D, 0.0D).color(r, g, b, a).endVertex();
      WorldRenderer.pos(x, y, 0.0D).tex(0.0D, 0.0D).color(r, g, b, a).endVertex();
      tessellator.draw();
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
      GlStateManager.disableTexture2D();
   }

   protected void drawTexturedRectangle(RectangularShape rectangle, int color) {
      this.drawTexturedRectangle(rectangle.getMinX(), rectangle.getMinY(), rectangle.getWidth(), rectangle.getHeight(), color);
   }

   protected void drawOutline(double x, double y, double width, double height, int color, double outlineSize) {
      this.drawRectangle(x, y, width - outlineSize, outlineSize, color);
      this.drawRectangle(x + width - outlineSize, y, outlineSize, height - outlineSize, color);
      this.drawRectangle(x, y + height - outlineSize, width, outlineSize, color);
      this.drawRectangle(x, y, outlineSize, height - outlineSize, color);
   }

   protected void drawOutline(RectangularShape rectangle, int color, double outlineSize) {
      this.drawOutline(rectangle.getMinX(), rectangle.getMinY(), rectangle.getWidth(), rectangle.getHeight(), color, outlineSize);
   }

   protected void startScissor() {
      GL11.glEnable(3089);
      float scaleFactor = (float)(new ScaledResolution(ClientProxy.MINECRAFT)).getScaleFactor();
      GL11.glScissor((int)(this.posX * scaleFactor), (int)(((float)this.gui.getHeight() - (this.posY + (float)this.height)) * scaleFactor), (int)((float)this.width * scaleFactor), (int)((float)this.height * scaleFactor));
   }

   protected void endScissor() {
      GL11.glDisable(3089);
   }
}

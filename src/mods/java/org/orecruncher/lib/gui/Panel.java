package org.orecruncher.lib.gui;

import javax.annotation.Nonnull;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec2f;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.lib.Color;

@SideOnly(Side.CLIENT)
public abstract class Panel<T extends Panel<?>> {
   public static final Color BACKGROUND_COLOR = new Color(33, 33, 33);
   public static final Color BORDER_COLOR = Color.MC_DARKGRAY;
   public static final Color TEXT_COLOR = Color.MC_YELLOW;
   private static final int DEFAULT_WIDTH = 10;
   private static final int DEFAULT_HEIGHT = 10;
   protected Color foreground;
   protected Color background;
   protected Color border;
   protected int width;
   protected int height;
   protected int minWidth;
   protected int minHeight;
   protected float alpha;
   protected boolean drawFrame;
   protected boolean drawBackground;
   protected ResourceLocation backgroundTexture;
   protected int textureWidth;
   protected int textureHeight;
   protected Vec2f U;
   protected Vec2f V;

   public Panel() {
      this(10, 10);
   }

   public Panel(int width, int height) {
      this(width, height, TEXT_COLOR, BACKGROUND_COLOR, BORDER_COLOR);
   }

   public Panel(int width, int height, Color fore, Color back, Color border) {
      this.alpha = 1.0F;
      this.drawFrame = true;
      this.drawBackground = true;
      this.backgroundTexture = null;
      this.U = new Vec2f(0.0F, 0.0F);
      this.V = new Vec2f(1.0F, 1.0F);
      this.foreground = fore != null ? fore : TEXT_COLOR;
      this.background = back != null ? back : BACKGROUND_COLOR;
      this.border = border != null ? border : BORDER_COLOR;
      this.minWidth = this.width = width;
      this.minHeight = this.height = height;
   }

   public T setBackgroundTexture(@Nonnull ResourceLocation texture) {
      this.backgroundTexture = texture;
      return (T)this;
   }

   public T setTextureDimensions(int width, int height) {
      this.textureHeight = height;
      this.textureWidth = width;
      return (T)this;
   }

   public T setTextureU(int start, int end) {
      this.U = GuiUtils.calculateSpan(this.textureWidth, start, end);
      return (T)this;
   }

   public T setTextureV(int start, int end) {
      this.V = GuiUtils.calculateSpan(this.textureHeight, start, end);
      return (T)this;
   }

   public T setTextureCoords(@Nonnull Vec2f u, @Nonnull Vec2f v) {
      this.U = u;
      this.V = v;
      return (T)this;
   }

   public T setForegroundColor(@Nonnull Color c) {
      this.foreground = c;
      return (T)this;
   }

   public T setBackgroundColor(@Nonnull Color c) {
      this.background = c;
      return (T)this;
   }

   public T setBorderColor(@Nonnull Color c) {
      this.border = c;
      return (T)this;
   }

   public ResourceLocation getBackgroundTexture() {
      return this.backgroundTexture;
   }

   @Nonnull
   public Color getForegroundColor() {
      return this.foreground;
   }

   @Nonnull
   public Color getBackgroundColor() {
      return this.background;
   }

   @Nonnull
   Color getFrameColor() {
      return this.border;
   }

   public T setAlpha(float a) {
      this.alpha = a;
      return (T)this;
   }

   @Nonnull
   public T setMinimumWidth(int w) {
      this.minWidth = w;
      this.width = Math.max(this.width, w);
      return (T)this;
   }

   public int getMinimumWidth() {
      return this.minWidth;
   }

   public T setWidth(int w) {
      this.width = Math.max(w, this.minWidth);
      return (T)this;
   }

   public int getWidth() {
      return this.width;
   }

   @Nonnull
   public T setMinimumHeight(int h) {
      this.minHeight = h;
      this.height = Math.max(this.height, h);
      return (T)this;
   }

   public int getMinimumHeight() {
      return this.minHeight;
   }

   public T setHeight(int h) {
      this.height = Math.max(h, this.minHeight);
      return (T)this;
   }

   public int getHeight() {
      return this.height;
   }

   public int getCenterX() {
      return (this.width + 1) / 2;
   }

   public int getCenterY() {
      return (this.height + 1) / 2;
   }

   public T drawFrame(boolean flag) {
      this.drawFrame = flag;
      return (T)this;
   }

   public T drawBackground(boolean flag) {
      this.drawBackground = flag;
      return (T)this;
   }

   public void render(int locX, int locY, @Nonnull Panel.Reference ref) {
      if (this.drawFrame || this.drawBackground) {
         int posX;
         int posY;
         switch(ref) {
         case CENTER:
            posX = locX - this.getCenterX();
            posY = locY - this.getCenterY();
            break;
         case TOP_CENTER:
            posX = locX - this.getCenterX();
            posY = locY;
            break;
         case UPPER_LEFT:
         default:
            posX = locX;
            posY = locY;
         }

         if (this.drawBackground) {
            if (this.backgroundTexture != null) {
               GuiUtils.drawTexturedModalRect(this.backgroundTexture, posX, posY, this.width, this.height, this.U, this.V);
            } else {
               int backgroundRGB = this.background.rgbWithAlpha(this.alpha);
               GuiUtils.drawRect(posX + 2, posY + 2, posX + this.width - 1, posY + this.height - 1, backgroundRGB);
            }
         }

         if (this.drawFrame) {
            int frameRGB = this.border.rgbWithAlpha(this.alpha);
            GuiUtils.drawTooltipBox(posX, posY, this.width, this.height, frameRGB, frameRGB, frameRGB);
         }

      }
   }

   public static enum Reference {
      CENTER,
      TOP_CENTER,
      UPPER_LEFT;

      private Reference() {
      }
   }
}

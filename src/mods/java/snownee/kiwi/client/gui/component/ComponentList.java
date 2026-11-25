package snownee.kiwi.client.gui.component;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import snownee.kiwi.client.gui.GuiControl;

public abstract class ComponentList extends Component {
   protected final int screenWidth;
   protected final int screenHeight;
   protected int mouseX;
   protected int mouseY;
   protected int offsetX;
   protected int offsetY;
   private float initialMouseClickY = -2.0F;
   private float scrollFactor;
   protected float scrollDistance;
   public int selectedIndex = -1;
   protected int hoveringIndex = -1;
   private long lastClickTime = 0L;
   private boolean highlightSelected = true;
   private boolean hasHeader;
   private int headerHeight;
   protected boolean captureMouse = true;
   private int cacheContentHeight;
   protected boolean drawBackground = true;
   protected boolean drawScrollbar = true;

   public ComponentList(GuiControl parent, int width, int height, int left, int top, int screenWidth, int screenHeight) {
      super(parent, width, height);
      this.top = top;
      this.left = left;
      this.screenWidth = screenWidth;
      this.screenHeight = screenHeight;
   }

   protected void setHeaderInfo(boolean hasHeader, int headerHeight) {
      this.hasHeader = hasHeader;
      this.headerHeight = headerHeight;
      if (!hasHeader) {
         this.headerHeight = 0;
      }

   }

   protected void setDrawBackground(boolean drawBackground) {
      this.drawBackground = drawBackground;
   }

   protected void setDrawScrollBar(boolean drawScrollbar) {
      this.drawScrollbar = drawScrollbar;
   }

   protected abstract int getSize();

   protected abstract int getSlotHeight(int var1);

   protected abstract void elementClicked(int var1, int var2, int var3, boolean var4);

   protected int getContentHeight() {
      return this.cacheContentHeight;
   }

   protected abstract void drawBackground();

   protected abstract void drawSlot(int var1, int var2, int var3, int var4, Tessellator var5);

   protected void drawHeader(int entryRight, int relativeY, Tessellator tess) {
   }

   protected void clickHeader(int x, int y) {
   }

   private void applyScrollLimits() {
      int listHeight = this.getContentHeight() - this.height;
      if (listHeight < 0) {
         listHeight = 0;
      }

      if (this.scrollDistance < 0.0F) {
         this.scrollDistance = 0.0F;
      }

      if (this.scrollDistance > (float)listHeight) {
         this.scrollDistance = (float)listHeight;
      }

   }

   public void actionPerformed(GuiButton button) {
      if (button.enabled) {
         ;
      }

   }

   public void handleMouseInput(int mouseX, int mouseY) {
      boolean isHovering = mouseX >= this.left && mouseX <= this.left + this.width && mouseY >= this.top && mouseY <= this.top + this.height;
      if (isHovering) {
         int scroll = Mouse.getEventDWheel();
         if (scroll != 0) {
            this.scrollDistance += (float)(-1 * scroll) / 120.0F * (float)this.cacheContentHeight / (float)this.getSize() / 2.0F;
         }

      }
   }

   public void drawScreen(int offsetX, int offsetY, int relMouseX, int relMouseY, float partialTicks) {
      this.offsetX = this.left + offsetX;
      this.offsetY = this.top + offsetY;
      if (this.cacheContentHeight == 0) {
         this.cacheContentHeight();
      }

      this.mouseX = relMouseX;
      this.mouseY = relMouseY;
      this.drawBackground();
      boolean isHovering = this.mouseX >= this.left && this.mouseX <= this.left + this.width && this.mouseY >= this.top && this.mouseY <= this.top + this.height;
      if (!isHovering) {
         this.hoveringIndex = -1;
      }

      int listLength = this.getSize();
      int scrollBarWidth = 6;
      int scrollBarRight = this.left + this.width;
      int scrollBarLeft = this.drawScrollbar ? scrollBarRight - scrollBarWidth : scrollBarRight;
      int entryRight = scrollBarLeft - 1;
      int border = 0;
      boolean clicked = false;
      if (Mouse.isButtonDown(0)) {
         if (this.initialMouseClickY == -1.0F) {
            if (isHovering) {
               int mouseListY = this.mouseY - this.top - this.headerHeight + (int)this.scrollDistance - border;
               if (this.mouseX >= this.left && this.mouseX <= entryRight && mouseListY >= 0) {
                  if (mouseListY >= 0) {
                     if (this.hoveringIndex >= 0 && this.hoveringIndex < listLength) {
                        clicked = true;
                     }
                  } else {
                     this.clickHeader(this.mouseX - this.left, this.mouseY - this.top + (int)this.scrollDistance - border);
                  }
               }

               if (this.mouseX >= scrollBarLeft && this.mouseX <= scrollBarRight) {
                  this.scrollFactor = -1.0F;
                  int scrollHeight = this.getContentHeight() - this.height - border;
                  if (scrollHeight < 1) {
                     scrollHeight = 1;
                  }

                  int var13 = (int)((float)(this.height * this.height) / (float)this.getContentHeight());
                  if (var13 < 32) {
                     var13 = 32;
                  }

                  if (var13 > this.height - border * 2) {
                     var13 = this.height - border * 2;
                  }

                  this.scrollFactor /= (float)(this.height - var13) / (float)scrollHeight;
               } else {
                  this.scrollFactor = 1.0F;
               }

               this.initialMouseClickY = (float)this.mouseY;
            } else {
               this.initialMouseClickY = -2.0F;
            }
         } else if (this.initialMouseClickY >= 0.0F) {
            this.scrollDistance -= ((float)this.mouseY - this.initialMouseClickY) * this.scrollFactor;
            this.initialMouseClickY = (float)this.mouseY;
         }
      } else {
         this.initialMouseClickY = -1.0F;
      }

      this.applyScrollLimits();
      Tessellator tess = Tessellator.getInstance();
      BufferBuilder worldr = tess.getBuffer();
      worldr.setTranslation((double)offsetX, (double)offsetY, 0.0D);
      ScaledResolution res = new ScaledResolution(this.parent.mc);
      double scaleW = (double)this.parent.mc.displayWidth / res.getScaledWidth_double();
      double scaleH = (double)this.parent.mc.displayHeight / res.getScaledHeight_double();
      GL11.glEnable(3089);
      GL11.glScissor((int)((double)this.offsetX * scaleW), (int)((double)this.parent.mc.displayHeight - (double)(offsetY + this.top + this.height) * scaleH), (int)((double)this.width * scaleW), (int)((double)this.height * scaleH));
      if (this.drawBackground) {
         if (this.parent.mc.world != null) {
            this.drawGradientRect(this.left, this.top, this.left + this.width, this.top + this.height, -1059201571, -1059201571);
         } else {
            GlStateManager.disableLighting();
            GlStateManager.disableFog();
            this.parent.mc.renderEngine.bindTexture(Gui.OPTIONS_BACKGROUND);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            float scale = 32.0F;
            worldr.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            worldr.pos((double)this.left, (double)(this.top + this.height), 0.0D).tex((double)((float)this.left / 32.0F), (double)(((float)(this.top + this.height) + this.scrollDistance) / 32.0F)).color(32, 32, 32, 255).endVertex();
            worldr.pos((double)(this.left + this.width), (double)(this.top + this.height), 0.0D).tex((double)((float)(this.left + this.width) / 32.0F), (double)(((float)(this.top + this.height) + this.scrollDistance) / 32.0F)).color(32, 32, 32, 255).endVertex();
            worldr.pos((double)(this.left + this.width), (double)this.top, 0.0D).tex((double)((float)(this.left + this.width) / 32.0F), (double)(((float)this.top + this.scrollDistance) / 32.0F)).color(32, 32, 32, 255).endVertex();
            worldr.pos((double)this.left, (double)this.top, 0.0D).tex((double)((float)this.left / 32.0F), (double)(((float)this.top + this.scrollDistance) / 32.0F)).color(32, 32, 32, 255).endVertex();
            tess.draw();
         }
      }

      int baseY = this.top + border - (int)this.scrollDistance;
      int extraHeight = this.getContentHeight() + border - this.height;
      int contentHeight = 0;
      if (this.hasHeader) {
         this.drawHeader(entryRight, baseY, tess);
         contentHeight += this.headerHeight;
      }

      for(int slotIdx = 0; slotIdx < listLength; ++slotIdx) {
         int slotTop = baseY + contentHeight;
         int sloltHeight = this.getSlotHeight(slotIdx);
         contentHeight += sloltHeight;
         int slotBuffer = sloltHeight - border;
         if (slotTop <= this.top + this.height && slotTop + slotBuffer >= this.top) {
            if (isHovering && (extraHeight <= 0 || this.mouseX < scrollBarLeft) && this.mouseY >= slotTop && this.mouseY < slotTop + sloltHeight) {
               if (clicked) {
                  this.elementClicked(this.hoveringIndex, this.mouseX - this.left, this.mouseY - slotTop, this.hoveringIndex == this.selectedIndex && System.currentTimeMillis() - this.lastClickTime < 250L);
                  this.selectedIndex = this.hoveringIndex;
                  this.lastClickTime = System.currentTimeMillis();
               }

               this.hoveringIndex = slotIdx;
            }

            this.drawSlot(slotIdx, entryRight, slotTop, slotBuffer, tess);
         }
      }

      this.cacheContentHeight = contentHeight;
      GlStateManager.disableDepth();
      worldr.setTranslation((double)offsetX, (double)offsetY, 0.0D);
      if (this.drawScrollbar && extraHeight > 0) {
         int scrollBarHeight = this.height * this.height / this.getContentHeight();
         if (scrollBarHeight < 32) {
            scrollBarHeight = 32;
         }

         if (scrollBarHeight > this.height - border * 2) {
            scrollBarHeight = this.height - border * 2;
         }

         int barTop = (int)this.scrollDistance * (this.height - scrollBarHeight) / extraHeight + this.top;
         if (barTop < this.top) {
            barTop = this.top;
         }

         GlStateManager.disableTexture2D();
         if (this.drawBackground) {
            worldr.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            worldr.pos((double)scrollBarLeft, (double)(this.top + this.height), 0.0D).tex(0.0D, 1.0D).color(221, 221, 221, 255).endVertex();
            worldr.pos((double)scrollBarRight, (double)(this.top + this.height), 0.0D).tex(1.0D, 1.0D).color(221, 221, 221, 255).endVertex();
            worldr.pos((double)scrollBarRight, (double)this.top, 0.0D).tex(1.0D, 0.0D).color(221, 221, 221, 255).endVertex();
            worldr.pos((double)scrollBarLeft, (double)this.top, 0.0D).tex(0.0D, 0.0D).color(221, 221, 221, 255).endVertex();
            tess.draw();
         }

         worldr.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
         worldr.pos((double)scrollBarLeft, (double)(barTop + scrollBarHeight), 0.0D).tex(0.0D, 1.0D).color(128, 128, 128, 255).endVertex();
         worldr.pos((double)scrollBarRight, (double)(barTop + scrollBarHeight), 0.0D).tex(1.0D, 1.0D).color(128, 128, 128, 255).endVertex();
         worldr.pos((double)scrollBarRight, (double)barTop, 0.0D).tex(1.0D, 0.0D).color(128, 128, 128, 255).endVertex();
         worldr.pos((double)scrollBarLeft, (double)barTop, 0.0D).tex(0.0D, 0.0D).color(128, 128, 128, 255).endVertex();
         tess.draw();
         worldr.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
         worldr.pos((double)scrollBarLeft, (double)(barTop + scrollBarHeight - 1), 0.0D).tex(0.0D, 1.0D).color(192, 192, 192, 255).endVertex();
         worldr.pos((double)(scrollBarRight - 1), (double)(barTop + scrollBarHeight - 1), 0.0D).tex(1.0D, 1.0D).color(192, 192, 192, 255).endVertex();
         worldr.pos((double)(scrollBarRight - 1), (double)barTop, 0.0D).tex(1.0D, 0.0D).color(192, 192, 192, 255).endVertex();
         worldr.pos((double)scrollBarLeft, (double)barTop, 0.0D).tex(0.0D, 0.0D).color(192, 192, 192, 255).endVertex();
         tess.draw();
      }

      worldr.setTranslation(0.0D, 0.0D, 0.0D);
      GlStateManager.enableTexture2D();
      GlStateManager.shadeModel(7424);
      GlStateManager.enableAlpha();
      GlStateManager.disableBlend();
      GL11.glDisable(3089);
   }

   protected void drawGradientRect(int left, int top, int right, int bottom, int color1, int color2) {
      GuiUtils.drawGradientRect(0, left, top, right, bottom, color1, color2);
   }

   public void cacheContentHeight() {
      int listLenth = this.getSize();
      int height = 0;

      for(int i = 0; i < listLenth; ++i) {
         height += this.getSlotHeight(i);
      }

      this.cacheContentHeight = height;
   }
}

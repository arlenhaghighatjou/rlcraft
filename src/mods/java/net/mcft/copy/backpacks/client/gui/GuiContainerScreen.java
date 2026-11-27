package net.mcft.copy.backpacks.client.gui;

import java.io.IOException;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.Mouse;

@SideOnly(Side.CLIENT)
public class GuiContainerScreen extends GuiScreen {
   private int _lastMouseX = -1;
   private int _lastMouseY = -1;
   public final GuiContext context = new GuiContext();
   public final GuiContainer container;
   private GuiElementBase _tooltipElement = null;
   private long _tooltipTime = 0L;

   public GuiContainerScreen() {
      this.container = new GuiContainer(this.context);
      this.container.setFill();
   }

   public void setWorldAndResolution(Minecraft mc, int width, int height) {
      this.context.setScreen(this);
      super.setWorldAndResolution(mc, width, height);
      this.container.setSize(width, height);
   }

   public void onGuiClosed() {
      this.context.setScreen((GuiContainerScreen)null);
   }

   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      if (mouseButton == GuiElementBase.MouseButton.LEFT) {
         this.context.setFocused((GuiElementBase)null);
      }

      this.container.onMouseDown(mouseButton, mouseX, mouseY);
   }

   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
      int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
      int scroll = Integer.signum(Mouse.getEventDWheel());
      if (scroll != 0) {
         this.container.onMouseScroll(scroll, mouseX, mouseY);
      }

   }

   public void updateScreen() {
      int mouseX = Mouse.getX() * this.width / this.mc.displayWidth;
      int mouseY = this.height - Mouse.getY() * this.height / this.mc.displayHeight - 1;
      if (mouseX != this._lastMouseX || mouseY != this._lastMouseY) {
         GuiElementBase pressed = this.context.getPressed();
         if (pressed != null) {
            ElementInfo info = (ElementInfo)ElementInfo.getElementHierarchy(pressed).getFirst();
            pressed.onMouseMove(mouseX - info.globalX, mouseY - info.globalY);
         } else if (this.container.contains(mouseX, mouseY)) {
            this.container.onMouseMove(mouseX, mouseY);
         }

         this._lastMouseX = mouseX;
         this._lastMouseY = mouseY;
      }

      GuiElementBase tooltipElement = (GuiElementBase)(new ElementInfo(this.container)).getElementsAt(mouseX, mouseY).map((info) -> {
         return info.element;
      }).filter((element) -> {
         return element.hasTooltip();
      }).reduce((first, second) -> {
         return second;
      }).orElse(null);
      if (tooltipElement != this._tooltipElement) {
         this._tooltipTime = Minecraft.getSystemTime();
      }

      this._tooltipElement = tooltipElement;
   }

   protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
      if (mouseButton == GuiElementBase.MouseButton.LEFT) {
         GuiElementBase pressed = this.context.getPressed();
         if (pressed != null) {
            pressed.onMouseUp(mouseButton, mouseX, mouseY);
            this.context.setPressed((GuiElementBase)null);
         }
      }
   }

   protected void keyTyped(char typedChar, int keyCode) throws IOException {
      super.keyTyped(typedChar, keyCode);
      if (keyCode == 61) {
         if (isShiftKeyDown()) {
            GuiContext.DEBUG = !GuiContext.DEBUG;
         } else if (GuiContext.DEBUG) {
            GuiContext.DEBUG_TEXT = !GuiContext.DEBUG_TEXT;
         }
      }

      GuiElementBase focused = this.context.getFocused();
      if (focused != null) {
         focused.onKey(keyCode, typedChar);
      }

   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawBackground(0);
      this.container.draw(mouseX, mouseY, partialTicks);
      if (this._tooltipElement != null && Minecraft.getSystemTime() >= this._tooltipTime + (long)this._tooltipElement.getTooltipDelay()) {
         this._tooltipElement.drawTooltip(mouseX, mouseY, this.width, this.height, partialTicks);
      }

      if (GuiContext.DEBUG) {
         this.drawDebugInfo(mouseX, mouseY, partialTicks);
      }

   }

   private void drawDebugInfo(int mouseX, int mouseY, float partialTicks) {
      FontRenderer fontRenderer = GuiElementBase.getFontRenderer();
      List<ElementInfo> hierarchy = (List)(new ElementInfo(this.container)).getElementsAt(mouseX, mouseY).collect(Collectors.toList());
      ElementInfo last = (ElementInfo)hierarchy.get(hierarchy.size() - 1);
      List<ElementInfo> children = (List)last.getChildElements().collect(Collectors.toList());
      GuiElementBase.enableBlendAlphaStuffs();
      GuiElementBase.setRenderColorARGB(1627389696);

      for(ElementInfo info : hierarchy) {
         if (info != last) {
            GuiElementBase.drawOutline(info.globalX - 1, info.globalY - 1, info.width + 2, info.height + 2);
         }
      }

      GuiElementBase.setRenderColorARGB(-1610547456);
      GuiElementBase.drawOutline(last.globalX - 1, last.globalY - 1, last.width + 2, last.height + 2);
      GuiElementBase.setRenderColorARGB(805371648);
      GuiElementBase.drawRect(last.globalX, last.globalY, last.width, last.height);
      if (last.element instanceof GuiContainer) {
         GuiContainer container = (GuiContainer)last.element;
         int x = last.globalX;
         int y = last.globalY;
         int w = last.width;
         int h = last.height;
         int padLeft = container.getPaddingLeft();
         int padRight = container.getPaddingRight();
         int padTop = container.getPaddingTop();
         int padBottom = container.getPaddingBottom();
         GuiElementBase.setRenderColorARGB(-2147483393);
         GuiElementBase.drawRect(x, y, padLeft, h);
         GuiElementBase.drawRect(x + w - padRight, y, padRight, h);
         GuiElementBase.drawRect(x + padLeft, y, w, padTop);
         GuiElementBase.drawRect(x + padLeft, y + h - padBottom, w, padBottom);
      }

      if (!children.isEmpty()) {
         GuiElementBase.setRenderColorARGB(-2130771968);

         for(ElementInfo child : children) {
            GuiElementBase.drawOutline(child.globalX - 1, child.globalY - 1, child.width + 2, child.height + 2);
         }
      }

      GuiElementBase.disableBlendAlphaStuffs();
      if (GuiContext.DEBUG_TEXT) {
         int textY = 4;

         for(ElementInfo info : hierarchy) {
            fontRenderer.drawStringWithShadow(info.toString(), 4.0F, (float)textY, 16777215);
            textY += GuiElementBase.LINE_HEIGHT;
         }

         if (!children.isEmpty()) {
            textY = textY + GuiElementBase.LINE_HEIGHT;
            fontRenderer.drawStringWithShadow("Children:", 4.0F, (float)textY, 16777215);
            textY = textY + GuiElementBase.LINE_HEIGHT;

            for(ElementInfo child : children) {
               fontRenderer.drawStringWithShadow(child.toString(), 10.0F, (float)textY, 16777215);
               textY += GuiElementBase.LINE_HEIGHT;
            }
         }

      }
   }
}

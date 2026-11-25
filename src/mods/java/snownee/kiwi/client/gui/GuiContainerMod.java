package snownee.kiwi.client.gui;

import java.io.IOException;
import java.util.List;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import org.lwjgl.input.Mouse;
import snownee.kiwi.client.gui.component.Component;

public abstract class GuiContainerMod extends GuiContainer implements IMessageHandler {
   public GuiControl control;
   protected List<String> tooltip;
   protected FontRenderer tooltipFont;

   public GuiContainerMod(Container container) {
      super(container);
   }

   @OverridingMethodsMustInvokeSuper
   public void initGui() {
      super.initGui();
      this.control = new GuiControl(this.mc, this.width, this.height, this);
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.control.drawScreen(mouseX, mouseY, partialTicks);
      super.drawScreen(mouseX, mouseY, partialTicks);
      if (this.tooltip != null && !this.tooltip.isEmpty()) {
         if (this.tooltipFont == null) {
            this.tooltipFont = this.fontRenderer;
         }

         this.drawHoveringText(this.tooltip, mouseX, mouseY, this.tooltipFont);
      }

      this.tooltip = null;
      this.tooltipFont = null;
   }

   protected void keyTyped(char typedChar, int keyCode) throws IOException {
      super.keyTyped(typedChar, keyCode);
      this.control.keyTyped(typedChar, keyCode);
   }

   public void handleMouseInput() throws IOException {
      int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
      int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
      super.handleMouseInput();
      this.control.handleMouseInput(mouseX, mouseY);
   }

   public void setTooltip(GuiControl control, Component component, List<String> tooltip, FontRenderer fontRenderer) {
      this.tooltip = tooltip;
      this.tooltipFont = fontRenderer;
   }

   public void onGuiClosed() {
      super.onGuiClosed();
      this.control.onDestroy();
   }
}

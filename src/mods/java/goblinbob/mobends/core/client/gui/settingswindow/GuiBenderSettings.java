package goblinbob.mobends.core.client.gui.settingswindow;

import goblinbob.mobends.core.bender.EntityBender;
import goblinbob.mobends.core.client.gui.elements.GuiSmallToggleButton;
import goblinbob.mobends.core.client.gui.elements.IGuiListElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class GuiBenderSettings implements IGuiListElement {
   private final EntityBender<?> bender;
   private final Minecraft mc;
   private final GuiSmallToggleButton toggleButton;
   private int x;
   private int y;
   private int listOrder;

   public GuiBenderSettings(EntityBender<?> bender) {
      this.bender = bender;
      this.mc = Minecraft.getMinecraft();
      this.toggleButton = new GuiSmallToggleButton();
      this.toggleButton.setToggleState(bender.isAnimated());
   }

   public void initGui(int x, int y) {
      this.x = x;
      this.y = y;
      this.toggleButton.initGui(x + 4, y + 4);
   }

   public boolean handleMouseClicked(int mouseX, int mouseY, int mouseButton) {
      if (this.toggleButton.mouseClicked(mouseX, mouseY, mouseButton)) {
         this.bender.setAnimate(this.toggleButton.getToggleState());
         return true;
      } else {
         return false;
      }
   }

   public void update(int mouseX, int mouseY) {
      this.toggleButton.update(mouseX, mouseY);
   }

   public void draw(float partialTicks) {
      GlStateManager.color(1.0F, 1.0F, 1.0F);
      this.mc.fontRenderer.drawStringWithShadow(this.bender.getLocalizedName(), (float)(this.x + 38), (float)(this.y + 10), 16777215);
      this.toggleButton.draw();
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public int getHeight() {
      return 20;
   }

   public int getOrder() {
      return this.listOrder;
   }

   public void setOrder(int order) {
      this.listOrder = order;
   }
}

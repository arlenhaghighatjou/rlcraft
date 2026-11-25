package snownee.kiwi.client.gui.component;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import snownee.kiwi.client.gui.GuiControl;
import snownee.kiwi.client.gui.element.DrawableNineSlice;

public class ComponentPanel extends Component {
   private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/recipe_book.png");
   private final DrawableNineSlice background;
   public final GuiControl control;

   public ComponentPanel(GuiControl parent, int width, int height) {
      super(parent, height, width);
      this.left = (parent.width - width) / 2;
      this.top = (parent.height - height) / 2;
      this.background = new DrawableNineSlice(TEXTURE, 82, 208, 32, 32, 4, 4, 4, 4);
      this.background.setHeight(height);
      this.background.setWidth(width);
      this.control = new GuiControl(parent.mc, width - 8, height - 8, parent);
      this.control.offsetX = this.left + 4;
      this.control.offsetY = this.top + 4;
   }

   public void drawScreen(int offsetX, int offsetY, int relMouseX, int relMouseY, float partialTicks) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.background.draw(this.parent.mc, offsetX + this.left, offsetY + this.top);
      this.control.drawScreen(relMouseX, relMouseY, partialTicks);
   }

   public void handleMouseInput(int mouseX, int mouseY) {
      this.control.handleMouseInput(mouseX, mouseY);
   }

   public void keyTyped(char typedChar, int keyCode) {
      this.control.keyTyped(typedChar, keyCode);
   }

   public void onDestroy() {
      this.control.onDestroy();
      super.onDestroy();
   }
}

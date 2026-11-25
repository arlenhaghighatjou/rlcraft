package goblinbob.mobends.core.client.gui.settingswindow;

import goblinbob.mobends.core.client.gui.elements.GuiList;
import goblinbob.mobends.core.client.gui.packswindow.GuiPacksWindow;
import goblinbob.mobends.core.util.Draw;
import java.util.LinkedList;
import net.minecraft.client.Minecraft;

public class GuiBenderList extends GuiList<GuiBenderSettings> {
   private final LinkedList<GuiBenderSettings> elements = new LinkedList();

   public GuiBenderList(int x, int y, int width, int height) {
      super(x, y, width, height, 5, 3, 3, 15);
   }

   protected void drawBackground(float partialTicks) {
      Minecraft.getMinecraft().getTextureManager().bindTexture(GuiPacksWindow.BACKGROUND_TEXTURE);
      Draw.borderBox(0, 0, this.width, this.height, 4, 36, 117);
   }

   public LinkedList<GuiBenderSettings> getListElements() {
      return this.elements;
   }

   protected int getScrollSpeed() {
      return 20;
   }
}

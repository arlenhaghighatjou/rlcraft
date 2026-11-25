package goblinbob.mobends.core.client.gui.packswindow;

import goblinbob.mobends.core.client.gui.elements.GuiList;
import goblinbob.mobends.core.flux.Observable;
import goblinbob.mobends.core.util.Draw;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;

public class GuiPackList extends GuiList<GuiPackEntry> {
   public static final int WIDTH = 126;
   public static final int HEIGHT = 131;
   private LinkedList<GuiPackEntry> listEntries;
   public final Observable<GuiPackEntry> elementClickedObservable;

   public GuiPackList(LinkedList<GuiPackEntry> listEntries) {
      super(0, 0, 126, 131, 3, 5, 5, 5);
      this.listEntries = listEntries;
      this.elementClickedObservable = new Observable<GuiPackEntry>();
   }

   public LinkedList<GuiPackEntry> getListElements() {
      return this.listEntries;
   }

   public void initGui(int x, int y) {
      super.initGui(x, y);
   }

   protected void drawBackground(float partialTicks) {
      Minecraft.getMinecraft().getTextureManager().bindTexture(GuiPacksWindow.BACKGROUND_TEXTURE);
      Draw.borderBox(0, 0, this.width, this.height, 4, 36, 117);
   }

   protected void drawContent(float partialTicks) {
      for(GuiPackEntry element : this.getListElements()) {
         if (!element.isDragged()) {
            element.draw(partialTicks);
         }
      }

   }

   protected boolean handleMouseClickedElements(int mouseX, int mouseY, int button) {
      Iterator<GuiPackEntry> it = this.getListElements().descendingIterator();

      while(it.hasNext()) {
         GuiPackEntry clickedEntry = (GuiPackEntry)it.next();
         if (clickedEntry.handleMouseClicked(mouseX, mouseY, button)) {
            this.getListElements().forEach((entry) -> {
               entry.setSelected(entry == clickedEntry);
            });
            this.elementClickedObservable.next(clickedEntry);
            return true;
         }
      }

      return false;
   }

   public boolean handleMouseReleased(int mouseX, int mouseY, int button) {
      return super.handleMouseReleased(mouseX, mouseY, button);
   }
}

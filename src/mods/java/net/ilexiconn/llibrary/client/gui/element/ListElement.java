package net.ilexiconn.llibrary.client.gui.element;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.client.ClientProxy;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class ListElement<T extends IElementGUI> extends Element<T> {
   private List<String> entries;
   private Function<ListElement<T>, Boolean> function;
   private ScrollbarElement<T> scrollbar;
   private int entryHeight;
   private int selectedEntry;

   public ListElement(T gui, float posX, float posY, int width, int height, List<String> entries, Function<ListElement<T>, Boolean> function) {
      this(gui, posX, posY, width, height, entries, 13, function);
   }

   public ListElement(T handler, float posX, float posY, int width, int height, List<String> entries, int entryHeight, Function<ListElement<T>, Boolean> function) {
      super(handler, posX, posY, width, height);
      this.selectedEntry = -1;
      this.entries = entries;
      this.function = function;
      this.entryHeight = entryHeight;
   }

   public void init() {
      this.scrollbar = new ScrollbarElement<T>(this, () -> {
         return (float)this.getWidth() - 8.0F;
      }, () -> {
         return 2.0F;
      }, () -> {
         return (float)this.getHeight() - 3.0F;
      }, this.entryHeight, () -> {
         return this.entries.size();
      });
   }

   public void render(float mouseX, float mouseY, float partialTicks) {
      this.drawRectangle((double)this.getPosX(), (double)this.getPosY(), (double)this.getWidth(), (double)this.getHeight(), this.getColorScheme().getSecondaryColor());
      FontRenderer fontRenderer = this.gui.getFontRenderer();
      float y = -this.scrollbar.getScrollOffset();
      GL11.glEnable(3089);
      ScaledResolution scaledResolution = new ScaledResolution(ClientProxy.MINECRAFT);
      int scaleFactor = scaledResolution.getScaleFactor();
      GL11.glScissor((int)(this.getPosX() * (float)scaleFactor), (int)(((float)this.gui.getHeight() - (this.getPosY() + (float)this.getHeight()) + 2.0F) * (float)scaleFactor), this.getWidth() * scaleFactor, (this.getHeight() - 4) * scaleFactor);
      int entryIndex = 0;

      for(String entry : this.entries) {
         float entryX = this.getPosX() + 2.0F;
         float entryY = this.getPosY() + y + 1.0F;
         float entryWidth = (float)(this.getWidth() - 6);
         boolean selected = this.isSelected(this.getPosX(), this.getPosY() + y, entryWidth, (float)this.entryHeight, mouseX, mouseY) && !this.scrollbar.isScrolling();
         boolean clickSelecting = selected && Mouse.isButtonDown(0);
         if (this.selectedEntry == entryIndex) {
            this.drawRectangle((double)entryX, (double)entryY, (double)entryWidth, (double)this.entryHeight, clickSelecting ? LLibrary.CONFIG.getAccentColor() : LLibrary.CONFIG.getDarkAccentColor());
         } else {
            this.drawRectangle((double)entryX, (double)entryY, (double)entryWidth, (double)this.entryHeight, selected ? LLibrary.CONFIG.getAccentColor() : this.getColorScheme().getSecondaryColor());
         }

         fontRenderer.drawString(entry, entryX + 2.0F, entryY - (float)(fontRenderer.FONT_HEIGHT / 2) + (float)(this.entryHeight / 2), LLibrary.CONFIG.getTextColor(), false);
         y += (float)this.entryHeight;
         ++entryIndex;
      }

      GL11.glDisable(3089);
      this.drawRectangle((double)(this.getPosX() + (float)this.getWidth() - 9.0F), (double)(this.getPosY() + 1.0F), 6.0D, (double)(this.getHeight() - 2), this.getColorScheme().getPrimaryColor());
   }

   public boolean mouseClicked(float mouseX, float mouseY, int button) {
      if (this.isSelected(mouseX, mouseY)) {
         float y = -this.scrollbar.getScrollOffset();

         for(int entryIndex = 0; entryIndex < this.entries.size(); ++entryIndex) {
            float entryX = this.getPosX() + 2.0F;
            float entryY = this.getPosY() + y + 1.0F;
            float entryWidth = (float)(this.getWidth() - this.entryHeight);
            if (this.isSelected(entryX, entryY, entryWidth, (float)this.entryHeight, mouseX, mouseY)) {
               int previousSelected = this.selectedEntry;
               this.selectedEntry = entryIndex;
               if (((Boolean)this.function.apply(this)).booleanValue()) {
                  this.gui.playClickSound();
               } else {
                  this.selectedEntry = previousSelected;
               }

               return true;
            }

            y += (float)this.entryHeight;
         }
      }

      return false;
   }

   private boolean isSelected(float entryX, float entryY, float entryWidth, float entryHeight, float mouseX, float mouseY) {
      return this.gui.isElementOnTop(this) && mouseX > entryX && mouseX < entryX + entryWidth && mouseY > entryY && mouseY < entryY + entryHeight;
   }

   public int getSelectedIndex() {
      return this.selectedEntry;
   }

   public void setSelectedIndex(int selectedIndex) {
      this.selectedEntry = selectedIndex;
   }

   public String getSelectedEntry() {
      return (String)this.entries.get(this.selectedEntry);
   }

   public ListElement<T> withPersistence(boolean persistent) {
      this.selectedEntry = 0;
      return this;
   }
}

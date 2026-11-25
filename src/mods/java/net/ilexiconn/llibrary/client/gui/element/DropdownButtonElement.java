package net.ilexiconn.llibrary.client.gui.element;

import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.server.property.IStringSelectionProperty;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DropdownButtonElement<T extends IElementGUI> extends Element<T> {
   private IStringSelectionProperty selected;
   private boolean dropped;
   private int dropdownWidth;

   public DropdownButtonElement(T gui, float posX, float posY, int width, int height, IStringSelectionProperty selected) {
      super(gui, posX, posY, width, height);
      this.selected = selected;

      for(String value : this.selected.getValidStringValues()) {
         int entryWidth = gui.getFontRenderer().getStringWidth(value) + 5;
         if (entryWidth > this.dropdownWidth) {
            this.dropdownWidth = entryWidth;
         }
      }

   }

   public void render(float mouseX, float mouseY, float partialTicks) {
      this.drawRectangle((double)this.getPosX(), (double)this.getPosY(), (double)this.getWidth(), (double)this.getHeight(), !this.isEnabled() || !this.isSelected(mouseX, mouseY) && !this.dropped ? this.getColorScheme().getSecondaryColor() : this.getColorScheme().getPrimaryColor());
      FontRenderer fontRenderer = this.gui.getFontRenderer();
      String text = this.selected.getString();
      fontRenderer.drawString(text, this.getPosX() + (float)(this.getWidth() / 2) - (float)(fontRenderer.getStringWidth(text) / 2), this.getPosY() + (float)(this.getHeight() / 2) - (float)(fontRenderer.FONT_HEIGHT / 2), LLibrary.CONFIG.getTextColor(), false);
      if (this.dropped) {
         this.drawRectangle((double)this.getPosX(), (double)(this.getPosY() + (float)this.getHeight()), (double)this.dropdownWidth, (double)(this.selected.getValidStringValues().size() * 12), this.getColorScheme().getSecondaryColor());
         float y = this.getPosY() + (float)this.getHeight() + 2.0F;

         for(String entry : this.selected.getValidStringValues()) {
            if (this.isEntrySelected(mouseX, mouseY, y)) {
               this.drawRectangle((double)this.getPosX(), (double)(y - 2.0F), (double)this.dropdownWidth, 12.0D, this.getColorScheme().getPrimaryColor());
            }

            fontRenderer.drawString(entry, this.getPosX() + 3.0F, y, LLibrary.CONFIG.getTextColor(), false);
            y += 12.0F;
         }
      }

   }

   public boolean mouseClicked(float mouseX, float mouseY, int button) {
      if (this.isSelected(mouseX, mouseY)) {
         this.dropped = !this.dropped;
         this.gui.playClickSound();
         return true;
      } else {
         boolean clicked = false;
         if (this.dropped) {
            float y = this.getPosY() + (float)this.getHeight() + 2.0F;

            for(String value : this.selected.getValidStringValues()) {
               if (this.isEntrySelected(mouseX, mouseY, y) && this.selected.trySetString(value)) {
                  this.gui.playClickSound();
                  clicked = true;
                  break;
               }

               y += 12.0F;
            }
         }

         this.dropped = false;
         return clicked;
      }
   }

   public String getSelected() {
      return this.selected.getString();
   }

   private boolean isEntrySelected(float mouseX, float mouseY, float y) {
      float x = this.getPosX() + 3.0F;
      return mouseX >= x && mouseX <= x + (float)this.dropdownWidth && mouseY >= y && mouseY <= y + 12.0F;
   }
}

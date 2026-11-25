package me.ichun.mods.ichunutil.client.gui.window.element;

import me.ichun.mods.ichunutil.client.gui.Theme;
import me.ichun.mods.ichunutil.client.gui.window.Window;
import me.ichun.mods.ichunutil.client.render.RendererHelper;
import net.minecraft.client.gui.GuiTextField;

public class ElementTextInput extends Element implements ITextInput {
   public GuiTextField textField;
   public String tooltip;
   public int spacing;

   public ElementTextInput(Window window, int x, int y, int w, int h, int ID, String tip, int maxLength, String currentText) {
      super(window, x, y, w, 12, ID, false);
      this.textField = new GuiTextField(0, this.parent.workspace.getFontRenderer(), this.parent.posX + this.posX + 2, this.parent.posY + this.posY + 2, this.width - 9, this.parent.workspace.getFontRenderer().FONT_HEIGHT);
      this.textField.setMaxStringLength(maxLength);
      this.textField.setEnableBackgroundDrawing(false);
      this.textField.setTextColor(Theme.getAsHex(this.parent.workspace.currentTheme.font));
      this.textField.setCanLoseFocus(false);
      this.textField.setText(currentText);
      this.tooltip = tip;
      this.spacing = this.parent.width - this.width - x;
   }

   public ElementTextInput(Window window, int x, int y, int w, int h, int ID, String tip, int maxLength) {
      this(window, x, y, w, h, ID, tip, maxLength, "");
   }

   public ElementTextInput(Window window, int x, int y, int w, int h, int ID, String tip) {
      this(window, x, y, w, h, ID, tip, 80, "");
   }

   public ElementTextInput(Window window, int x, int y, int w, int h, int ID, String tip, String currentText) {
      this(window, x, y, w, h, ID, tip, 80, currentText);
   }

   public void update() {
      this.textField.updateCursorCounter();
      this.textField.setTextColor(Theme.getAsHex(this.parent.workspace.currentTheme.font));
   }

   public boolean onClick(int mouseX, int mouseY, int id) {
      if (id == 1) {
         this.textField.setText("");
      }

      if (this.textField.isFocused()) {
         this.textField.mouseClicked(this.parent.posX + mouseX + 2, this.parent.posY + mouseY + 2, id);
      }

      return true;
   }

   public void draw(int mouseX, int mouseY, boolean hover) {
      RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.elementInputBorder[0], this.parent.workspace.currentTheme.elementInputBorder[1], this.parent.workspace.currentTheme.elementInputBorder[2], 255, (double)this.getPosX(), (double)this.getPosY(), (double)this.width, (double)this.height, 0.0D);
      if (hover) {
         RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.elementInputBackgroundHover[0], this.parent.workspace.currentTheme.elementInputBackgroundHover[1], this.parent.workspace.currentTheme.elementInputBackgroundHover[2], 255, (double)(this.getPosX() + 1), (double)(this.getPosY() + 1), (double)(this.width - 2), (double)(this.height - 2), 0.0D);
      } else {
         RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.elementInputBackgroundInactive[0], this.parent.workspace.currentTheme.elementInputBackgroundInactive[1], this.parent.workspace.currentTheme.elementInputBackgroundInactive[2], 255, (double)(this.getPosX() + 1), (double)(this.getPosY() + 1), (double)(this.width - 2), (double)(this.height - 2), 0.0D);
      }

      if (this.textField.getVisible()) {
         this.textField.drawTextBox();
      }

   }

   public void keyInput(char c, int key) {
      if (key == 15) {
         this.tabHit();
      } else {
         if (key == 28) {
            this.parent.elementTriggered(this);
         }

         String prevText = this.textField.getText();
         this.textField.textboxKeyTyped(c, key);
         this.checkAndCorrectText(prevText);
      }
   }

   public void checkAndCorrectText(String oldText) {
   }

   public void selected() {
      this.textField.setFocused(true);
   }

   public void deselected() {
      this.textField.setFocused(false);
      this.resized();
   }

   public void resized() {
      this.textField.x = this.parent.posX + this.posX + 2;
      this.textField.y = this.parent.posY + this.posY + 2;
      this.textField.width = this.width - 9;
      this.textField.setCursorPositionZero();
      this.width = this.parent.width - this.posX - this.spacing;
   }

   public String tooltip() {
      return this.tooltip;
   }

   public void tabHit() {
      this.deselected();
      boolean found = false;
      boolean foundSelf = false;

      for(int i = 0; i < this.parent.elements.size(); ++i) {
         Element e = (Element)this.parent.elements.get(i);
         if (e == this) {
            foundSelf = true;
         } else if (e instanceof ITextInput && foundSelf) {
            found = true;
            ((ITextInput)e).cycledTo();
            break;
         }
      }

      if (!found) {
         for(int i = 0; i < this.parent.elements.size(); ++i) {
            Element e = (Element)this.parent.elements.get(i);
            if (e instanceof ITextInput) {
               ((ITextInput)e).cycledTo();
               break;
            }
         }
      }

   }

   public void cycledTo() {
      this.parent.workspace.elementSelected = this;
      this.selected();
   }
}

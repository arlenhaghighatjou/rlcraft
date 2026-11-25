package me.ichun.mods.ichunutil.client.gui.window.element;

import me.ichun.mods.ichunutil.client.gui.window.Window;

public class ElementTextInputNumber extends ElementTextInput {
   public boolean allowDecimal;

   public ElementTextInputNumber(Window window, int x, int y, int w, int h, int ID, String tip, boolean allowDec) {
      super(window, x, y, w, h, ID, tip, 60);
      this.allowDecimal = allowDec;
   }

   public ElementTextInputNumber(Window window, int x, int y, int w, int h, int ID, String tip, String curText, boolean allowDec) {
      super(window, x, y, w, h, ID, tip, 60, curText);
      this.allowDecimal = allowDec;
   }

   public void checkAndCorrectText(String oldText) {
      String newString = this.textField.getText();
      if (!newString.isEmpty() && !newString.equals("-") && (!this.allowDecimal || !newString.equals("."))) {
         boolean reject = true;

         try {
            if (this.allowDecimal) {
               Double.parseDouble(newString);
            } else {
               Integer.parseInt(newString);
            }

            reject = false;
         } catch (NumberFormatException var5) {
            ;
         }

         if (reject) {
            int pos = this.textField.getCursorPosition();
            this.textField.setText(oldText);
            this.textField.setCursorPosition(pos - 1);
         }

      }
   }
}

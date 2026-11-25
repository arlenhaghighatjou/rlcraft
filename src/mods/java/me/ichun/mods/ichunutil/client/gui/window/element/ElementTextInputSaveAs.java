package me.ichun.mods.ichunutil.client.gui.window.element;

import me.ichun.mods.ichunutil.client.gui.window.Window;

public class ElementTextInputSaveAs extends ElementTextInput {
   private static final String[] invalidChars = new String[]{"\\", "/", ":", "*", "?", "\"", "<", ">", "|"};

   public ElementTextInputSaveAs(Window window, int x, int y, int w, int h, int ID, String tip) {
      super(window, x, y, w, h, ID, tip, 60);
   }

   public ElementTextInputSaveAs(Window window, int x, int y, int w, int h, int ID, String tip, String curText) {
      super(window, x, y, w, h, ID, tip, 60, curText);
   }

   public void checkAndCorrectText(String oldText) {
      String newString = this.textField.getText();
      if (!newString.isEmpty()) {
         boolean reject = false;

         for(String c : invalidChars) {
            if (newString.contains(c)) {
               reject = true;
               break;
            }
         }

         if (reject) {
            int pos = this.textField.getCursorPosition();
            this.textField.setText(oldText);
            this.textField.setCursorPosition(pos - 1);
         }

      }
   }
}

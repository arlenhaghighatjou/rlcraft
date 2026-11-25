package com.fantasticsource.mctools.gui.element.view;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.text.GUIMultilineTextInput;

public class GUIMultilineTextInputView extends GUIScrollView {
   public final GUIMultilineTextInput multilineTextInput;

   public GUIMultilineTextInputView(GUIScreen screen, double width, double height, GUIMultilineTextInput multilineTextInput) {
      super(screen, width, height, multilineTextInput);
      this.multilineTextInput = multilineTextInput;
   }

   public GUIMultilineTextInputView(GUIScreen screen, double x, double y, double width, double height, GUIMultilineTextInput multilineTextInput) {
      super(screen, x, y, width, height, multilineTextInput);
      this.multilineTextInput = multilineTextInput;
   }

   public GUIMultilineTextInputView recalc(int subIndexChanged) {
      if (this.multilineTextInput == null) {
         return this;
      } else {
         this.recalcThisOnly();
         int line = this.multilineTextInput.cursorLine();
         double ratio = 1.0D / (double)this.multilineTextInput.fullLineCount();
         double lineTop = (double)line * ratio * this.internalHeight;
         double lineBottom = (double)(line + 1) * ratio * this.internalHeight;
         if (lineTop < this.top) {
            this.progress = lineTop / (this.internalHeight - 1.0D);
         } else if (lineBottom > this.bottom) {
            this.progress = (lineBottom - 1.0D) / (this.internalHeight - 1.0D);
         }

         super.recalc(subIndexChanged);
         return this;
      }
   }

   public boolean valid() {
      return this.multilineTextInput.valid();
   }

   public String getText() {
      return this.multilineTextInput.getText();
   }

   public void setText(String text) {
      this.multilineTextInput.setText(text);
   }

   public void click() {
      this.multilineTextInput.setActive(true);
      super.click();
   }
}

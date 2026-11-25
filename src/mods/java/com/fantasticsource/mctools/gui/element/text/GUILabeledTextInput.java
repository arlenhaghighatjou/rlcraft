package com.fantasticsource.mctools.gui.element.text;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.text.filter.TextFilter;
import com.fantasticsource.mctools.gui.element.view.GUIAutocroppedView;

public class GUILabeledTextInput extends GUIAutocroppedView {
   public final GUIText label;
   public final GUITextInput input;

   public GUILabeledTextInput(GUIScreen screen, String label, String defaultInput, TextFilter filter) {
      this(screen, label, defaultInput, filter, 1.0D);
   }

   public GUILabeledTextInput(GUIScreen screen, String label, String defaultInput, TextFilter filter, double scale) {
      super(screen);
      this.input = new GUITextInput(screen, defaultInput, filter, scale);
      this.label = new GUIText(screen, label, scale);
      this.add(this.label.addClickActions(new Runnable[]{() -> {
         int length = this.input.text.length();
         this.input.cursorPosition = length;
         this.input.selectorPosition = length == 0 ? -1 : 0;
         this.input.setActive(true);
      }}));
      this.add(this.input);
   }

   public GUILabeledTextInput(GUIScreen screen, double x, double y, String label, String defaultInput, TextFilter filter) {
      this(screen, x, y, label, defaultInput, filter, 1.0D);
   }

   public GUILabeledTextInput(GUIScreen screen, double x, double y, String label, String defaultInput, TextFilter filter, double scale) {
      super(screen, x, y);
      this.input = new GUITextInput(screen, defaultInput, filter, scale);
      this.label = new GUIText(screen, label, scale);
      this.add(this.label.addClickActions(new Runnable[]{() -> {
         int length = this.input.text.length();
         this.input.cursorPosition = length;
         this.input.selectorPosition = length == 0 ? -1 : 0;
         this.input.setActive(true);
      }}));
      this.add(this.input);
   }

   public GUILabeledTextInput setInput(String text) {
      this.input.setText(text);
      return this;
   }

   public GUILabeledTextInput setNamespace(String namespace) {
      this.input.setNamespace(namespace);
      return this;
   }

   public void recalcAndRepositionSubElements(int startIndex) {
      super.recalcAndRepositionSubElements(startIndex);
      if (this.label != null && this.input != null) {
         this.label.recalc(0);
         this.input.x = this.label.width;
         this.input.y = 0.0D;
      }

   }

   public String toString() {
      return this.input.toString();
   }

   public boolean valid() {
      return this.input.valid();
   }

   public String getText() {
      return this.input.getText();
   }

   public void setText(String text) {
      this.input.setText(text);
   }

   public GUILabeledTextInput addEditActions(Runnable... actions) {
      this.input.addEditActions(actions);
      return this;
   }
}

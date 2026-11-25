package com.fantasticsource.mctools.gui.element.text;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.view.GUIAutocroppedView;
import com.fantasticsource.tools.datastructures.Color;

public class GUILabeledBoolean extends GUIAutocroppedView {
   public final GUIText label;
   public final GUIText input;

   public GUILabeledBoolean(GUIScreen screen, String label, boolean defaultInput) {
      this(screen, label, defaultInput, 1.0D);
   }

   public GUILabeledBoolean(GUIScreen screen, String label, boolean defaultInput, double scale) {
      super(screen);
      this.input = new GUIText(screen, "" + defaultInput, GUIScreen.getIdleColor(Color.WHITE), GUIScreen.getHoverColor(Color.WHITE), Color.WHITE, scale);
      this.label = new GUIText(screen, label, scale);
      GUIText var10001 = this.label;
      Runnable[] var10002 = new Runnable[1];
      GUIText var10005 = this.input;
      this.input.getClass();
      var10002[0] = var10005::click;
      this.add(var10001.addClickActions(var10002));
      this.add(this.input.addClickActions(new Runnable[]{() -> {
         this.input.setText(this.input.getText().equals("true") ? "false" : "true");
      }}));
   }

   public GUILabeledBoolean(GUIScreen screen, double x, double y, String label, boolean defaultInput) {
      this(screen, x, y, label, defaultInput, 1.0D);
   }

   public GUILabeledBoolean(GUIScreen screen, double x, double y, String label, boolean defaultInput, double scale) {
      super(screen, x, y);
      this.input = new GUIText(screen, "" + defaultInput, GUIScreen.getIdleColor(Color.WHITE), GUIScreen.getHoverColor(Color.WHITE), Color.WHITE, scale);
      this.label = new GUIText(screen, label, scale);
      this.add(this.label.addClickActions(new Runnable[]{() -> {
         this.input.setText(this.input.getText().equals("true") ? "false" : "true");
      }}));
      this.add(this.input.addClickActions(new Runnable[]{() -> {
         this.input.setText(this.input.getText().equals("true") ? "false" : "true");
      }}));
   }

   public GUILabeledBoolean setInput(boolean value) {
      this.input.setText("" + value);
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

   public boolean getValue() {
      return Boolean.parseBoolean(this.input.getText());
   }

   public void setValue(boolean value) {
      this.input.setText("" + value);
   }

   public GUILabeledBoolean addEditActions(Runnable... actions) {
      this.input.addEditActions(actions);
      return this;
   }
}

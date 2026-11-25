package com.fantasticsource.mctools.gui.element.text;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.other.GUIGradientBorder;
import com.fantasticsource.mctools.gui.element.view.GUIAutocroppedView;
import com.fantasticsource.tools.datastructures.Color;

public class GUITextButton extends GUIAutocroppedView {
   public static final double DEFAULT_PADDING = 0.5D;
   public GUIText internalText;

   public GUITextButton(GUIScreen screen, String text) {
      this(screen, text, Color.WHITE, 1.0D);
   }

   public GUITextButton(GUIScreen screen, String text, double scale) {
      this(screen, text, Color.WHITE, scale);
   }

   public GUITextButton(GUIScreen screen, String text, Color color) {
      this(screen, text, color, GUIScreen.getIdleColor(color).setAF(color.af() * 0.4F), 1.0D);
   }

   public GUITextButton(GUIScreen screen, String text, Color color, double scale) {
      this(screen, text, color, GUIScreen.getIdleColor(color).setAF(color.af() * 0.4F), scale);
   }

   public GUITextButton(GUIScreen screen, String text, Color border, Color center) {
      this(screen, text, border, center, 1.0D);
   }

   public GUITextButton(GUIScreen screen, String text, Color border, Color center, double scale) {
      super(screen, 0.5D, new GUIGradientBorder(screen, 1.0D, 1.0D, 0.16666666666666666D, GUIScreen.getIdleColor(border), GUIScreen.getIdleColor(center), GUIScreen.getHoverColor(border), GUIScreen.getHoverColor(center), border, center));
      this.internalText = new GUIText(screen, text, GUIScreen.getIdleColor(border), GUIScreen.getHoverColor(border), border, scale);
      this.add(this.internalText);
      this.linkMouseActivity(this.internalText);
      this.linkMouseActivity(this.background);
      this.recalc(0);
   }

   public GUITextButton(GUIScreen screen, double x, double y, String text) {
      this(screen, x, y, text, Color.WHITE, 1.0D);
   }

   public GUITextButton(GUIScreen screen, double x, double y, String text, double scale) {
      this(screen, x, y, text, Color.WHITE, scale);
   }

   public GUITextButton(GUIScreen screen, double x, double y, String text, Color color) {
      this(screen, x, y, text, color, GUIScreen.getIdleColor(color).setAF(color.af() * 0.4F), 1.0D);
   }

   public GUITextButton(GUIScreen screen, double x, double y, String text, Color color, double scale) {
      this(screen, x, y, text, color, GUIScreen.getIdleColor(color).setAF(color.af() * 0.4F), scale);
   }

   public GUITextButton(GUIScreen screen, double x, double y, String text, Color border, Color center) {
      this(screen, x, y, text, border, center, 1.0D);
   }

   public GUITextButton(GUIScreen screen, double x, double y, String text, Color border, Color center, double scale) {
      super(screen, x, y, 0.5D, new GUIGradientBorder(screen, 1.0D, 1.0D, 0.16666666666666666D, GUIScreen.getIdleColor(border), GUIScreen.getIdleColor(center), GUIScreen.getHoverColor(border), GUIScreen.getHoverColor(center), border, center));
      this.internalText = new GUIText(screen, text, GUIScreen.getIdleColor(border), GUIScreen.getHoverColor(border), border, scale);
      this.add(this.internalText);
      this.linkMouseActivity(this.internalText);
      this.linkMouseActivity(this.background);
      this.recalc(0);
   }

   public String toString() {
      return this.internalText.text;
   }
}

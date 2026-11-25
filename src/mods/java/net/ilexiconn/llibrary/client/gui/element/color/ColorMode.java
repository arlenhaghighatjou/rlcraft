package net.ilexiconn.llibrary.client.gui.element.color;

import java.util.ArrayList;
import java.util.List;

public class ColorMode {
   private static final List<ColorMode> COLOR_MODE_LIST = new ArrayList();
   public static final ColorMode DARK = create("dark", -14606047, -13224394, -12171706, -14606047, -14737633, -1, -16777216);
   public static final ColorMode LIGHT = create("light", -3289651, -5460820, -1250068, -3289651, -4013374, -16777216, -1);
   private final String name;
   private final int primaryColor;
   private final int secondaryColor;
   private final int tertiaryColor;
   private final int primarySubcolor;
   private final int secondarySubcolor;
   private final int textColor;
   private final int invertedTextColor;

   private ColorMode(String name, int primaryColor, int secondaryColor, int tertiaryColor, int primarySubcolor, int secondarySubcolor, int textColor, int invertedTextColor) {
      this.name = name;
      this.primaryColor = primaryColor;
      this.secondaryColor = secondaryColor;
      this.tertiaryColor = tertiaryColor;
      this.primarySubcolor = primarySubcolor;
      this.secondarySubcolor = secondarySubcolor;
      this.textColor = textColor;
      this.invertedTextColor = invertedTextColor;
   }

   public static ColorMode create(String name, int primaryColor, int secondaryColor, int tertiaryColor, int primarySubcolor, int secondarySubcolor, int textColor, int invertedTextColor) {
      ColorMode colorMode = new ColorMode(name, primaryColor, secondaryColor, tertiaryColor, primarySubcolor, secondarySubcolor, textColor, invertedTextColor);
      COLOR_MODE_LIST.add(colorMode);
      return colorMode;
   }

   public static ColorMode getColorMode(String name) {
      for(ColorMode colorMode : COLOR_MODE_LIST) {
         if (colorMode.getName().equals(name)) {
            return colorMode;
         }
      }

      return LIGHT;
   }

   public String getName() {
      return this.name;
   }

   public int getPrimaryColor() {
      return this.primaryColor;
   }

   public int getSecondaryColor() {
      return this.secondaryColor;
   }

   public int getTertiaryColor() {
      return this.tertiaryColor;
   }

   public int getPrimarySubcolor() {
      return this.primarySubcolor;
   }

   public int getSecondarySubcolor() {
      return this.secondarySubcolor;
   }

   public int getTextColor() {
      return this.textColor;
   }

   public int getInvertedTextColor() {
      return this.invertedTextColor;
   }
}

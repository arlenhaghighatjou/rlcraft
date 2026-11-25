package com.oblivioussp.spartanhudbaubles.client.gui;

public class AlignmentHelper {
   public AlignmentHelper() {
   }

   public static enum Alignment {
      TOP_LEFT(AlignmentHelper.VerticalAlignment.TOP, AlignmentHelper.HorizontalAlignment.LEFT),
      TOP_CENTER(AlignmentHelper.VerticalAlignment.TOP, AlignmentHelper.HorizontalAlignment.CENTER),
      TOP_RIGHT(AlignmentHelper.VerticalAlignment.TOP, AlignmentHelper.HorizontalAlignment.RIGHT),
      CENTER_LEFT(AlignmentHelper.VerticalAlignment.CENTER, AlignmentHelper.HorizontalAlignment.LEFT),
      CENTER(AlignmentHelper.VerticalAlignment.CENTER, AlignmentHelper.HorizontalAlignment.CENTER),
      CENTER_RIGHT(AlignmentHelper.VerticalAlignment.CENTER, AlignmentHelper.HorizontalAlignment.RIGHT),
      BOTTOM_LEFT(AlignmentHelper.VerticalAlignment.BOTTOM, AlignmentHelper.HorizontalAlignment.LEFT),
      BOTTOM_CENTER(AlignmentHelper.VerticalAlignment.BOTTOM, AlignmentHelper.HorizontalAlignment.CENTER),
      BOTTOM_RIGHT(AlignmentHelper.VerticalAlignment.BOTTOM, AlignmentHelper.HorizontalAlignment.RIGHT);

      private AlignmentHelper.VerticalAlignment vertical;
      private AlignmentHelper.HorizontalAlignment horizontal;

      private Alignment(AlignmentHelper.VerticalAlignment vertical, AlignmentHelper.HorizontalAlignment horizontal) {
         this.vertical = vertical;
         this.horizontal = horizontal;
      }

      public AlignmentHelper.VerticalAlignment getVertical() {
         return this.vertical;
      }

      public AlignmentHelper.HorizontalAlignment getHorizontal() {
         return this.horizontal;
      }

      public static String[] getValidConfigValues() {
         AlignmentHelper.Alignment[] validValues = values();
         String[] validStrings = new String[validValues.length];

         for(int i = 0; i < validValues.length; ++i) {
            AlignmentHelper.Alignment value = validValues[i];
            validStrings[i] = value.toString();
         }

         return validStrings;
      }
   }

   public static enum HorizontalAlignment {
      LEFT,
      CENTER,
      RIGHT;

      private HorizontalAlignment() {
      }
   }

   public static enum VerticalAlignment {
      TOP,
      CENTER,
      BOTTOM;

      private VerticalAlignment() {
      }
   }
}

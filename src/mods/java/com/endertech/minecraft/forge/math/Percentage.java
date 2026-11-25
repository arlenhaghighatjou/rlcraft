package com.endertech.minecraft.forge.math;

import com.endertech.common.CommonMath;
import com.endertech.common.Ordered;
import net.minecraft.block.material.MapColor;
import net.minecraft.util.text.TextFormatting;

public class Percentage implements Ordered<Percentage> {
   public static final float FACTOR = 100.0F;
   public static final Percentage ZERO = new Percentage(0.0F);
   public static final Percentage HUNDRED = new Percentage(100.0F);
   private final float value;

   public Percentage(float value) {
      this.value = value;
   }

   public static Percentage value(float value) {
      return new Percentage(value);
   }

   public static Percentage from(int value, int maxValue) {
      return CommonMath.isAlmostZero((double)maxValue) ? ZERO : from((float)value / (float)maxValue);
   }

   public static Percentage from(float rate) {
      return new Percentage(rate * 100.0F);
   }

   public static Percentage parse(String string) throws NumberFormatException {
      float value = Float.parseFloat(string.replace('%', ' ').trim());
      return new Percentage(value);
   }

   public float getValue() {
      return this.value;
   }

   public Percentage.Grade getGrade() {
      Percentage.Grade[] grades = Percentage.Grade.values();

      for(int i = grades.length - 1; i >= 0; --i) {
         if (this.getValue() > grades[i].threshold) {
            return grades[i];
         }
      }

      return Percentage.Grade.LOW;
   }

   public boolean takeChance() {
      return this.toFraction() > CommonMath.Random.getFloat();
   }

   public float toFraction() {
      return this.getValue() / 100.0F;
   }

   public String toString() {
      return String.format("%.0f%%", this.getValue());
   }

   public String toColoredText() {
      return this.getGrade().textColor + this.toString();
   }

   public boolean randomResult() {
      return CommonMath.Random.result(this.toFraction());
   }

   public int compareTo(Percentage other) {
      return Float.compare(this.getValue(), other.getValue());
   }

   public boolean equals(Object obj) {
      return obj instanceof Percentage && ((Percentage)obj).getValue() == this.getValue();
   }

   public static enum Grade {
      LOW(0.0F, TextFormatting.GREEN, MapColor.GREEN),
      MEDIUM(40.0F, TextFormatting.YELLOW, MapColor.YELLOW),
      HIGH(70.0F, TextFormatting.RED, MapColor.RED);

      public final float threshold;
      public final TextFormatting textColor;
      public final MapColor mapColor;

      private Grade(float threshold, TextFormatting textColor, MapColor mapColor) {
         this.threshold = threshold;
         this.textColor = textColor;
         this.mapColor = mapColor;
      }
   }
}

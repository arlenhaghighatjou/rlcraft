package com.fantasticsource.mctools;

import com.fantasticsource.tools.datastructures.Pair;

public class DoubleRequirement {
   public static final int MODE_GREATER_OR_EQUAL = 0;
   public static final int MODE_LESS_OR_EQUAL = 1;
   public static final int MODE_EQUAL = 2;
   public static final int MODE_GREATER = 3;
   public static final int MODE_LESS = 4;
   public static final int MODE_NOT_EQUAL = 5;
   private int mode;
   private double amount;

   public DoubleRequirement(double amount) {
      this(0, amount);
   }

   public DoubleRequirement(int mode, double amount) {
      this.mode = mode;
      if (mode >= 0 && mode <= 5) {
         this.amount = amount;
      } else {
         throw new IllegalArgumentException("Unknown mode: " + mode);
      }
   }

   public static Pair<String, DoubleRequirement> parse(String string) {
      Pair<String, DoubleRequirement> result = null;

      try {
         if (string.contains(">=")) {
            String[] tokens = string.split(">=");
            result = new Pair<String, DoubleRequirement>(tokens[0].trim(), new DoubleRequirement(0, Double.parseDouble(tokens[1].trim())));
         } else if (string.contains("<=")) {
            String[] tokens = string.split("<=");
            result = new Pair<String, DoubleRequirement>(tokens[0].trim(), new DoubleRequirement(1, Double.parseDouble(tokens[1].trim())));
         } else if (string.contains("!=")) {
            String[] tokens = string.split("!=");
            result = new Pair<String, DoubleRequirement>(tokens[0].trim(), new DoubleRequirement(5, Double.parseDouble(tokens[1].trim())));
         } else if (string.contains("=")) {
            String[] tokens = string.split("=");
            result = new Pair<String, DoubleRequirement>(tokens[0].trim(), new DoubleRequirement(2, Double.parseDouble(tokens[1].trim())));
         } else if (string.contains(">")) {
            String[] tokens = string.split(">");
            result = new Pair<String, DoubleRequirement>(tokens[0].trim(), new DoubleRequirement(3, Double.parseDouble(tokens[1].trim())));
         } else if (string.contains("<")) {
            String[] tokens = string.split("<");
            result = new Pair<String, DoubleRequirement>(tokens[0].trim(), new DoubleRequirement(4, Double.parseDouble(tokens[1].trim())));
         }

         return result;
      } catch (NumberFormatException var3) {
         return null;
      }
   }

   public boolean check(double amount) {
      switch(this.mode) {
      case 0:
         return amount >= this.amount;
      case 1:
         return amount <= this.amount;
      case 2:
         return amount == this.amount;
      case 3:
         return amount > this.amount;
      case 4:
         return amount < this.amount;
      case 5:
         return amount != this.amount;
      default:
         return false;
      }
   }
}

package de.Whitedraco.switchbow.Config.variable;

import de.Whitedraco.switchbow.helper.HelperMethodes;

public class ConfigValue {
   private static final String LINE_SEPARATORE = System.getProperty("line.separator");
   String key;
   String value = "";
   boolean bool;
   double doub;
   float floa;
   int integer;
   String text = "";

   public ConfigValue(String key, boolean bool) {
      this.key = "<" + key + ">";
      this.bool = bool;
      this.value = "bool";
   }

   public ConfigValue(String key, double doub) {
      this.key = "<" + key + ">";
      this.doub = doub;
      this.value = "doub";
   }

   public ConfigValue(String key, float floa) {
      this.key = "<" + key + ">";
      this.floa = floa;
      this.value = "floa";
   }

   public ConfigValue(String key, int integer) {
      this.key = "<" + key + ">";
      this.integer = integer;
      this.value = "integer";
   }

   public ConfigValue(String text) {
      this.text = text;
      this.value = "text";
   }

   public String getString() {
      if (this.value.equals("bool")) {
         return this.key + "=" + this.bool + LINE_SEPARATORE;
      } else if (this.value.equals("doub")) {
         return this.key + "=" + this.doub + LINE_SEPARATORE;
      } else if (this.value.equals("floa")) {
         return this.key + "=" + this.floa + LINE_SEPARATORE;
      } else if (this.value.equals("integer")) {
         return this.key + "=" + this.integer + LINE_SEPARATORE;
      } else {
         return this.value.equals("text") ? this.text + LINE_SEPARATORE : "";
      }
   }

   public boolean checkandreplaceData(String[] data) {
      if (!this.value.equals("text") && this.key.contains(data[0])) {
         if (this.value.equals("bool")) {
            this.bool = HelperMethodes.isTrueOrFalse(data[1]);
         } else if (this.value.equals("doub")) {
            this.doub = Double.parseDouble(data[1]);
         } else if (this.value.equals("floa")) {
            this.floa = Float.parseFloat(data[1]);
         } else {
            if (!this.value.equals("integer")) {
               return false;
            }

            this.integer = Integer.parseInt(data[1]);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean getBool() {
      return this.bool;
   }

   public void setBool(boolean bool) {
      this.bool = bool;
   }

   public double getDoub() {
      return this.doub;
   }

   public void setDoub(double doub) {
      this.doub = doub;
   }

   public float getFloa() {
      return this.floa;
   }

   public void setFloa(float floa) {
      this.floa = floa;
   }

   public int getInteger() {
      return this.integer;
   }

   public void setInteger(int integer) {
      this.integer = integer;
   }
}

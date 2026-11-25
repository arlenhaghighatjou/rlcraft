package net.silentchaos512.lib.config;

import javax.annotation.Nullable;
import net.silentchaos512.lib.util.LogHelper;

public class ConfigMultiValueLineParser {
   final String configId;
   @Nullable
   final LogHelper log;
   final String delimiter;
   final Class[] valueClasses;
   final boolean strict;

   public ConfigMultiValueLineParser(String configId, @Nullable LogHelper log, String delimiter, Class... valueClasses) {
      this(configId, log, delimiter, false, valueClasses);
   }

   public ConfigMultiValueLineParser(String configId, @Nullable LogHelper log, String delimiter, boolean strict, Class... valueClasses) {
      this.configId = configId;
      this.log = log;
      this.delimiter = delimiter;
      this.strict = strict;
      this.valueClasses = valueClasses;
   }

   @Nullable
   public Object[] parse(String line) {
      String[] values = line.split(this.delimiter);
      if (values.length < this.valueClasses.length) {
         this.warning("Too few values in line '" + line + "'. Ignoring the entire line.");
         return null;
      } else {
         if (values.length > this.valueClasses.length) {
            if (this.strict) {
               this.warning("Too many values in line '" + line + "'. Ignoring the entire line.");
               return null;
            }

            this.warning("Too many values in line '" + line + "'. Ignoring extra values and parsing others.");
         }

         Object[] result = new Object[this.valueClasses.length];

         for(int i = 0; i < this.valueClasses.length; ++i) {
            String value = values[i];
            if (this.valueClasses[i] == Integer.class) {
               try {
                  result[i] = Integer.parseInt(value);
               } catch (NumberFormatException var7) {
                  this.warning("Could not parse '" + value + "' as integer.");
                  return null;
               }
            } else if (this.valueClasses[i] == Float.class) {
               try {
                  result[i] = Float.parseFloat(value);
               } catch (NumberFormatException var8) {
                  this.warning("Could not parse '" + value + "' as float.");
                  return null;
               }
            } else if (this.valueClasses[i] == Boolean.class) {
               if (value.equalsIgnoreCase("true")) {
                  result[i] = true;
               } else {
                  if (!value.equalsIgnoreCase("false")) {
                     this.warning("Could not parse '" + value + "' as boolean.");
                     return null;
                  }

                  result[i] = false;
               }
            } else {
               if (this.valueClasses[i] != String.class) {
                  this.warning("Don't know how to parse " + this.valueClasses[i] + "!");
                  return null;
               }

               result[i] = value;
            }
         }

         return result;
      }
   }

   private void warning(String message) {
      if (this.log != null) {
         this.log.warn(this.configId + ": " + message);
      }

   }
}

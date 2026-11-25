package xzeroair.trinkets.util.helpers;

import java.math.BigDecimal;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import xzeroair.trinkets.util.Reference;

public class StringUtils {
   public StringUtils() {
   }

   public static String combineStringArray(String[] array) {
      if (array.length <= 0) {
         return "";
      } else {
         String combinedArray = "";

         for(int i = array.length - 1; i >= 0; --i) {
            combinedArray = array[i] + ", " + combinedArray;
         }

         return combinedArray;
      }
   }

   public static String[] deconstructStringArray(String string) {
      String[] stringArray = string.split(", ");
      return stringArray;
   }

   public static String getStringFromArray(String[] array, int index) {
      return array != null && index >= 0 && index < array.length ? array[index] : "";
   }

   public static String getCmdSubstring(String command, String cmdRegex) {
      Function<String, String> getRegexCmd = (cmd) -> {
         String regex = convertToRegex(cmdRegex, false);
         return regex.isEmpty() ? regex : regex + " ";
      };
      return getSubstring(command, (String)getRegexCmd.apply(cmdRegex));
   }

   public static String convertToRegex(String regexInput, boolean caseSensitive) {
      Function<String, String> getRegexCmd = (cmd) -> {
         if (!cmd.isEmpty()) {
            String[] array = cmd.split("");
            String finalString = "";

            for(String character : array) {
               if (!caseSensitive && character.matches("[a-zA-Z]")) {
                  String lower = character.toLowerCase();
                  String upper = character.toUpperCase();
                  finalString = finalString + "[" + lower + upper + "]";
               } else if (character.matches("[- ]")) {
                  finalString = finalString + "" + character + "?";
               } else {
                  finalString = finalString + "[" + character + "]";
               }
            }

            if (!finalString.isEmpty()) {
               return "(" + finalString + ")";
            }
         }

         return "";
      };
      return (String)getRegexCmd.apply(regexInput);
   }

   public static String getSubstring(String string, String regex) {
      if (!regex.isEmpty()) {
         String[] array = string.split(regex, 2);
         return array.length > 1 && array[0].replace(" ", "").isEmpty() ? array[1] : "";
      } else {
         return "";
      }
   }

   public static StringUtils.StringCommand getStringVariables(String string, String regex) {
      String prefix = "";
      String suffix = "";
      if (!regex.isEmpty()) {
         String[] array = string.split(regex, 2);
         String pre = array[0].replace("  ", " ").trim();
         if (!pre.isEmpty()) {
            prefix = pre;
         }

         if (array.length > 1) {
            suffix = array[1];
         }
      }

      return new StringUtils.StringCommand(prefix, suffix);
   }

   public static <T> T continueCommand(String string, Function<String, T> func) {
      return (T)func.apply(string);
   }

   public static <T> T continueCommand(String arg1, String arg2, BiFunction<String, String, T> func) {
      return (T)func.apply(arg1, arg2);
   }

   public static String formatConfigString(Object... options) {
      String returnString = "";

      for(Object obj : options) {
         if (!returnString.isEmpty()) {
            returnString = returnString + ";";
         }

         if (obj instanceof String) {
            returnString = returnString + obj;
         }

         if (obj instanceof Boolean) {
            returnString = returnString + "b=" + obj;
         }

         if (obj instanceof Integer) {
            returnString = returnString + "i=" + obj;
         }

         if (obj instanceof Float) {
            returnString = returnString + "f=" + obj;
         }

         if (obj instanceof Double) {
            returnString = returnString + "d=" + obj;
         }
      }

      return returnString;
   }

   public static double getDoubleFromString(String string) {
      if (string.startsWith("d=")) {
         try {
            return Double.parseDouble(string.replaceFirst("d=", ""));
         } catch (Exception var2) {
            ;
         }
      }

      return 0.0D;
   }

   public static int getIntegerFromString(String string) {
      if (string.startsWith("i=")) {
         try {
            return Integer.parseInt(string.replaceFirst("i=", "").replaceAll("[^.\\d]", "").replaceAll("(\\..*)", ""));
         } catch (Exception var2) {
            ;
         }
      }

      return 0;
   }

   public static float getFloatFromString(String string) {
      if (string.startsWith("f=")) {
         try {
            return Float.parseFloat(string.replaceFirst("f=", ""));
         } catch (Exception var2) {
            ;
         }
      }

      return 0.0F;
   }

   public static boolean getBooleanFromString(String string) {
      if (string.startsWith("b=")) {
         try {
            return Boolean.parseBoolean(string.replaceFirst("b=", ""));
         } catch (Exception var2) {
            ;
         }
      }

      return false;
   }

   public static double getAccurateDouble(double value) {
      return MathHelper.getDouble(Reference.DECIMALFORMAT.format(BigDecimal.valueOf(value)), value);
   }

   public static double getAccurateDouble(double value, double defaultValue) {
      return MathHelper.getDouble(Reference.DECIMALFORMAT.format(BigDecimal.valueOf(value)), defaultValue);
   }

   public static void sendMessageToPlayer(Entity entity, String msg, boolean onScreen) {
      if (entity instanceof EntityPlayer && !msg.isEmpty()) {
         TextComponentString message = new TextComponentString(msg);
         ((EntityPlayer)entity).sendStatusMessage(message, onScreen);
      }

   }

   public static class StringCommand {
      String Prefix;
      String Suffix;

      public StringCommand(String prefix, String suffix) {
         this.Prefix = prefix;
         this.Suffix = suffix;
      }

      public String getPrefix() {
         return this.Prefix;
      }

      public String getSuffix() {
         return this.Suffix;
      }
   }
}

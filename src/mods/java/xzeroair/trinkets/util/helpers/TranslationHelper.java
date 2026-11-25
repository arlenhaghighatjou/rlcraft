package xzeroair.trinkets.util.helpers;

import java.util.function.Function;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class TranslationHelper {
   public TextFormatting black = TextFormatting.BLACK;
   public TextFormatting white = TextFormatting.WHITE;
   public TextFormatting red = TextFormatting.RED;
   public TextFormatting dRed = TextFormatting.DARK_RED;
   public TextFormatting blue = TextFormatting.BLUE;
   public TextFormatting dBlue = TextFormatting.DARK_BLUE;
   public TextFormatting green = TextFormatting.GREEN;
   public TextFormatting dGreen = TextFormatting.DARK_GREEN;
   public TextFormatting lPurple = TextFormatting.LIGHT_PURPLE;
   public TextFormatting dPurple = TextFormatting.DARK_PURPLE;
   public TextFormatting yellow = TextFormatting.YELLOW;
   public TextFormatting aqua = TextFormatting.AQUA;
   public TextFormatting dAqua = TextFormatting.DARK_AQUA;
   public TextFormatting gray = TextFormatting.GRAY;
   public TextFormatting dGray = TextFormatting.DARK_GRAY;
   public TextFormatting gold = TextFormatting.GOLD;
   public TextFormatting ST = TextFormatting.STRIKETHROUGH;
   public TextFormatting UL = TextFormatting.UNDERLINE;
   public TextFormatting italic = TextFormatting.ITALIC;
   public TextFormatting bold = TextFormatting.BOLD;
   public TextFormatting reset = TextFormatting.RESET;
   public static final TranslationHelper INSTANCE = new TranslationHelper();

   public TranslationHelper() {
   }

   public String formatAddVariables(String string, TranslationHelper.KeyEntry... entries) {
      for(TranslationHelper.KeyEntry key : entries) {
         if (key instanceof TranslationHelper.LangEntry || this.hasLangReplacement(key.key(), string)) {
            string = this.langKeyReplace(string, key.enabled(), key.key(), key.option());
         }

         if (key instanceof TranslationHelper.OptionEntry || this.hasKey(key.key(), string)) {
            string = this.optionReplace(string, key.enabled(), key.key(), key.option());
         }

         if (key instanceof TranslationHelper.KeyBindEntry || this.hasKeyBind(key.key(), string)) {
            string = this.optionReplace(string, key.enabled(), key.key(), key.option());
         }
      }

      return string;
   }

   public boolean hasKey(String key, String string) {
      return string.contains(key);
   }

   public boolean hasKeyBind(String key, String string) {
      return string.contains("*" + key.toLowerCase() + ":");
   }

   public boolean hasOption(String key, String string) {
      return string.contains("$" + key.toLowerCase() + ":");
   }

   public boolean hasLangReplacement(String key, String string) {
      return string.contains("@" + key.replace("@", "").replace(":", "").toLowerCase() + ":");
   }

   public String langKeyReplace(String string, boolean enabled, String key, String replacementKey) {
      if (enabled) {
         String translatedKey = (new TextComponentTranslation(replacementKey, new Object[0])).getFormattedText().trim();
         return string.replace(key, translatedKey);
      } else {
         return string.replace(key, "");
      }
   }

   public String optionReplace(String string, boolean enabled, String key, String Variable) {
      return enabled ? string.replace(key, Variable) : string.replace(key, "");
   }

   public boolean isStringEmpty(String string) {
      return string.replace("\u00ef\u00bf\u00bdr", "").replace("\u00ef\u00bf\u00bd6", "").replace("\u00ef\u00bf\u00bd", "").replace("\u00c2\u00a7r", "").replaceAll(" ", "").isEmpty();
   }

   public String TranslationKeyReplace(String itemKey, boolean enabled, String key, String translation) {
      if (enabled) {
         String translatedKey = (new TextComponentTranslation(itemKey + "." + key, new Object[0])).getFormattedText();
         return translation.replace("@" + key.toLowerCase() + ":", translatedKey);
      } else {
         return translation.replace("@" + key.toLowerCase() + ":", "");
      }
   }

   public String VariableReplace(boolean enabled, String key, String Variable, String translation) {
      return enabled ? translation.replace("$" + key.toLowerCase() + ":", Variable) : translation.replace("$" + key.toLowerCase() + ":", "");
   }

   public String booleanCheckTranslation(boolean bool) {
      TextComponentTranslation enabled = new TextComponentTranslation("xat.tooltip.enabled", new Object[0]);
      TextComponentTranslation disabled = new TextComponentTranslation("xat.tooltip.disabled", new Object[0]);
      return bool ? enabled.getFormattedText() : disabled.getFormattedText();
   }

   public String toggleCheckTranslation(boolean bool) {
      TextComponentTranslation on = new TextComponentTranslation("xat.tooltip.on", new Object[0]);
      TextComponentTranslation off = new TextComponentTranslation("xat.tooltip.off", new Object[0]);
      return bool ? on.getFormattedText() : off.getFormattedText();
   }

   /** @deprecated */
   @Deprecated
   public String addTextColorFromLangKey(String string) {
      return string.replace("#black:", "" + this.black).replace("#darkblue:", "" + this.dBlue).replace("#darkgreen:", "" + this.dGreen).replace("#darkaqua:", "" + this.dAqua).replace("#darkred:", "" + this.dRed).replace("#darkpurple:", "" + this.dPurple).replace("#gold:", "" + this.gold).replace("#gray:", "" + this.gray).replace("#darkgray:", "" + this.dGray).replace("#blue:", "" + this.blue).replace("#green:", "" + this.green).replace("#aqua:", "" + this.aqua).replace("#red:", "" + this.red).replace("#lightpurple:", "" + this.lPurple).replace("#yellow:", "" + this.yellow).replace("#white:", "" + this.white).replace("#obfuscated:", "" + this.reset).replace("#bold:", "" + this.bold).replace("#strikethrough:", "" + this.ST).replace("#underline:", "" + this.UL).replace("#italic:", "" + this.italic).replace("#reset:", "" + this.reset);
   }

   public String translateAttributeValue(int OP, double Amount) {
      double TranslatedAmount = Amount;
      String string = "";
      if (OP > 0) {
         TranslatedAmount = (double)Math.round(Amount * 100.0D);
         if (TranslatedAmount > 0.0D) {
            string = "+" + TranslatedAmount + "%";
         } else {
            string = "" + TranslatedAmount + "%";
         }
      } else if (Amount > 0.0D) {
         string = "+" + Amount;
      } else {
         string = "" + Amount;
      }

      String color = TranslatedAmount > 0.0D ? "" + this.green : "" + this.red;
      return color + string;
   }

   public String getLangTranslation(TextComponentTranslation lang) {
      return this.getLangTranslation(lang, (Function)null);
   }

   public String getLangTranslation(TextComponentTranslation lang, Function<String, String> func) {
      boolean isEmpty = lang.getUnformattedComponentText().contentEquals(lang.getKey()) || lang.getUnformattedText().isEmpty();
      if (!isEmpty) {
         String text = func == null ? lang.getFormattedText() : (String)func.apply(lang.getFormattedText());
         if (!this.isStringEmpty(text)) {
            return text;
         }
      }

      return "";
   }

   public String getLangTranslation(String key) {
      return this.getLangTranslation(key, (Function)null);
   }

   public String getLangTranslation(String key, Function<String, String> func) {
      TextComponentTranslation lang = new TextComponentTranslation(key, new Object[0]);
      return this.getLangTranslation(lang, func);
   }

   public double parseDoubleSafely(String d) {
      String number = d.replaceAll("[^.\\d]", "");
      if (!number.isEmpty()) {
         try {
            return Double.parseDouble(number);
         } catch (Exception var4) {
            var4.printStackTrace();
         }
      }

      return 0.0D;
   }

   public static class KeyBindEntry extends TranslationHelper.KeyEntry {
      public KeyBindEntry(String key, String keybind) {
         this(key, true, keybind);
      }

      public KeyBindEntry(String key, boolean enabled, String keybind) {
         super("*" + key + ":", enabled, keybind);
      }
   }

   public static class KeyEntry {
      private String key;
      private String option;
      private boolean enabled;

      public <T> KeyEntry(String key, T option) {
         this(key, true, option);
      }

      public <T> KeyEntry(String key, boolean enabled, T option) {
         this.key = key;
         this.enabled = enabled;
         this.option = option + "";
      }

      public String key() {
         return this.key;
      }

      public String option() {
         return this.option;
      }

      public boolean enabled() {
         return this.enabled;
      }
   }

   public static class LangEntry extends TranslationHelper.KeyEntry {
      public <T> LangEntry(String prefix, String key) {
         this(prefix, key, true);
      }

      public <T> LangEntry(String prefix, String key, boolean enabled) {
         super("@" + key + ":", enabled, prefix + "." + key);
      }
   }

   public static class OptionEntry extends TranslationHelper.KeyEntry {
      public <T> OptionEntry(String key, T option) {
         this(key, true, option);
      }

      public <T> OptionEntry(String key, boolean enabled, T option) {
         super("$" + key + ":", enabled, option);
      }
   }
}

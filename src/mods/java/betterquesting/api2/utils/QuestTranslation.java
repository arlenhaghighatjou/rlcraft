package betterquesting.api2.utils;

import net.minecraft.client.resources.I18n;

public class QuestTranslation {
   public QuestTranslation() {
   }

   public static String translate(String text, Object... args) {
      return !I18n.hasKey(text) ? text : I18n.format(text, args);
   }

   public static String translateTrimmed(String text, Object... args) {
      return translate(text, args).replaceAll("\r", "");
   }
}

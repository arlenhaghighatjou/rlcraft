package mezz.jei.config;

import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import java.util.Arrays;
import java.util.Locale;
import mezz.jei.util.Translator;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class LocalizedConfiguration extends Configuration {
   private static final String commentPostfix = ".comment";
   private final String keyPrefix;
   private final String defaultLocalized;
   private final String validLocalized;

   public LocalizedConfiguration(String keyPrefix, VFile2 file, String configVersion) {
      super(file, configVersion);
      this.keyPrefix = keyPrefix + '.';
      this.defaultLocalized = Translator.translateToLocal(this.keyPrefix + "default");
      this.validLocalized = Translator.translateToLocal(this.keyPrefix + "valid");
   }

   public void addCategory(String categoryName) {
      String categoryKey = this.keyPrefix + categoryName;
      String commentKey = categoryKey + ".comment";
      String comment = Translator.translateToLocal(commentKey);
      this.setCategoryComment(categoryName, comment);
      this.setCategoryLanguageKey(categoryName, categoryKey);
   }

   public boolean getBoolean(String category, String name, boolean defaultValue) {
      String langKey = this.keyPrefix + category + '.' + name;
      String commentKey = langKey + ".comment";
      String comment = Translator.translateToLocal(commentKey);
      return this.getBoolean(name, category, defaultValue, comment, langKey);
   }

   public String getString(String name, String category, String defaultValue) {
      String langKey = this.keyPrefix + category + '.' + name;
      String commentKey = langKey + ".comment";
      String comment = Translator.translateToLocal(commentKey);
      return this.getString(name, category, defaultValue, comment, langKey);
   }

   public Property getString(String name, String category, String defaultValue, String[] validValues) {
      String langKey = this.keyPrefix + category + '.' + name;
      String commentKey = langKey + ".comment";
      String comment = Translator.translateToLocal(commentKey);
      Property prop = this.get(category, name, defaultValue);
      prop.setLanguageKey(langKey);
      prop.setComment(comment + " [" + this.defaultLocalized + ": " + defaultValue + "] [" + this.validLocalized + ": " + Arrays.toString(validValues) + ']');
      return prop;
   }

   public <T extends Enum<T>> T getEnum(String name, String category, T defaultValue, T[] validEnumValues) {
      String langKey = this.keyPrefix + category + '.' + name;
      String commentKey = langKey + ".comment";
      String comment = Translator.translateToLocal(commentKey);
      Property prop = this.get(category, name, defaultValue.name());
      String[] validValues = new String[validEnumValues.length];

      for(int i = 0; i < validEnumValues.length; ++i) {
         T enumValue = validEnumValues[i];
         validValues[i] = enumValue.name().toLowerCase(Locale.ENGLISH);
      }

      prop.setValidValues(validValues);
      prop.setLanguageKey(langKey);
      prop.setComment(comment + "\n[" + this.defaultLocalized + ": " + defaultValue.name().toLowerCase(Locale.ENGLISH) + "]\n[" + this.validLocalized + ": " + Arrays.toString(prop.getValidValues()) + ']');
      String stringValue = prop.getString();
      T enumValue = defaultValue;

      for(int i = 0; i < validValues.length; ++i) {
         if (stringValue.equalsIgnoreCase(validValues[i])) {
            enumValue = validEnumValues[i];
         }
      }

      return enumValue;
   }

   public <T extends Enum<T>> void setEnum(String name, String category, T value, T[] validEnumValues) {
      String langKey = this.keyPrefix + category + '.' + name;
      String commentKey = langKey + ".comment";
      String comment = Translator.translateToLocal(commentKey);
      Property prop = this.get(category, name, value.name());
      String[] validValues = new String[validEnumValues.length];

      for(int i = 0; i < validEnumValues.length; ++i) {
         T enumValue = validEnumValues[i];
         validValues[i] = enumValue.name().toLowerCase(Locale.ENGLISH);
      }

      prop.setValue(value.name().toLowerCase(Locale.ENGLISH));
      prop.setValidValues(validValues);
      prop.setLanguageKey(langKey);
      prop.setComment(comment + "\n[" + this.defaultLocalized + ": " + value.name().toLowerCase(Locale.ENGLISH) + "]\n[" + this.validLocalized + ": " + Arrays.toString(prop.getValidValues()) + ']');
   }

   public String[] getStringList(String name, String category, String[] defaultValue) {
      String langKey = this.keyPrefix + category + '.' + name;
      String commentKey = langKey + ".comment";
      String comment = Translator.translateToLocal(commentKey);
      Property prop = this.get(category, name, defaultValue);
      prop.setLanguageKey(langKey);
      prop.setComment(comment + " [" + this.defaultLocalized + ": " + Arrays.toString(defaultValue) + ']');
      return prop.getStringList();
   }

   public String[] getStringList(String name, String category, String[] defaultValue, String[] validValues) {
      String langKey = this.keyPrefix + category + '.' + name;
      String commentKey = langKey + ".comment";
      String comment = Translator.translateToLocal(commentKey);
      Property prop = this.get(category, name, defaultValue);
      prop.setLanguageKey(langKey);
      prop.setValidValues(validValues);
      prop.setComment(comment + " [" + this.defaultLocalized + ": " + Arrays.toString(defaultValue) + "] [" + this.validLocalized + ": " + Arrays.toString(prop.getValidValues()) + ']');
      return prop.getStringList();
   }

   public float getFloat(String name, String category, float defaultValue, float minValue, float maxValue) {
      String langKey = this.keyPrefix + category + '.' + name;
      String commentKey = langKey + ".comment";
      String comment = Translator.translateToLocal(commentKey);
      return this.getFloat(name, category, defaultValue, minValue, maxValue, comment, langKey);
   }

   public int getInt(String name, String category, int defaultValue, int minValue, int maxValue) {
      String langKey = this.keyPrefix + category + '.' + name;
      String commentKey = langKey + ".comment";
      String comment = Translator.translateToLocal(commentKey);
      return this.getInt(name, category, defaultValue, minValue, maxValue, comment, langKey);
   }

   public int setInt(String name, String category, int value, int minValue, int maxValue) {
      String langKey = this.keyPrefix + category + '.' + name;
      String commentKey = langKey + ".comment";
      String comment = Translator.translateToLocal(commentKey);
      Property property = this.get(category, name, value, comment, minValue, maxValue);
      property.setLanguageKey(langKey);
      property.setValue(value);
      return property.getInt();
   }
}

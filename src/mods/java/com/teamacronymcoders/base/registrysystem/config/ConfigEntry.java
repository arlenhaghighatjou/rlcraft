package com.teamacronymcoders.base.registrysystem.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.config.Property.Type;

public class ConfigEntry {
   private String category;
   private String propertyName;
   private boolean isArray;
   private Type type;
   private String value;
   private String comment;
   private boolean guiChangeable;

   public ConfigEntry(String propertyName, String value) {
      this("General", propertyName, Type.STRING, value);
   }

   public ConfigEntry(String category, String propertyName, Type type, String value) {
      this(category, propertyName, type, value, "");
   }

   public ConfigEntry(String category, String propertyName, Type type, String value, String comment) {
      this(category, propertyName, type, value, comment, true);
   }

   public ConfigEntry(String category, String propertyName, Type type, String value, String comment, boolean guiChangeable) {
      this(category, propertyName, type, value, comment, guiChangeable, false);
   }

   public ConfigEntry(String category, String propertyName, Type type, String value, String comment, boolean guiChangeable, boolean isArray) {
      this.setCategory(category);
      this.setPropertyName(propertyName);
      this.setType(type);
      this.setValue(value);
      this.setComment(comment);
      this.setGuiChangeable(guiChangeable);
      this.setArray(isArray);
   }

   public void saveProperty(Configuration configuration) {
      switch(this.getType()) {
      case INTEGER:
         Property property = configuration.get(this.getCategory(), this.getPropertyName(), Integer.parseInt(this.getValue()), this.getComment());
         this.value = Integer.toString(property.getInt());
         break;
      case BOOLEAN:
         Property property = configuration.get(this.getCategory(), this.getPropertyName(), Boolean.parseBoolean(this.getValue()), this.getComment());
         this.value = Boolean.toString(property.getBoolean());
         break;
      case DOUBLE:
         Property property = configuration.get(this.getCategory(), this.getPropertyName(), Double.parseDouble(this.getValue()), this.getComment());
         this.value = Double.toString(property.getDouble());
         break;
      default:
         if (!this.isArray) {
            this.value = configuration.get(this.getCategory(), this.getPropertyName(), this.getValue(), this.getComment()).getString();
         } else {
            String[] values = this.value.split(",");
            this.value = String.join(",", configuration.get(this.getCategory(), this.getPropertyName(), values, this.getComment()).getStringList());
         }
      }

   }

   public int getInt(int defaultValue) {
      try {
         return Integer.parseInt(this.getValue());
      } catch (NumberFormatException var3) {
         return defaultValue;
      }
   }

   public boolean getBoolean(boolean defaultValue) {
      return this.getType() == Type.BOOLEAN ? Boolean.parseBoolean(this.getValue()) : defaultValue;
   }

   public double getDouble(double defaultValue) {
      try {
         return Double.parseDouble(this.getValue());
      } catch (NumberFormatException var4) {
         return defaultValue;
      }
   }

   public String getString() {
      return this.getValue();
   }

   public String[] getStringArray() {
      String[] stringArray;
      if (this.isArray) {
         stringArray = this.getString().split(",");
      } else {
         stringArray = new String[]{this.getString()};
      }

      return stringArray;
   }

   public String getCategory() {
      return this.category;
   }

   public void setCategory(String category) {
      this.category = category;
   }

   public String getPropertyName() {
      return this.propertyName;
   }

   public void setPropertyName(String propertyName) {
      this.propertyName = propertyName;
   }

   public String getValue() {
      return this.value;
   }

   public void setValue(String value) {
      this.value = value;
   }

   public String getComment() {
      return this.comment;
   }

   public void setComment(String comment) {
      this.comment = comment;
   }

   public boolean isGuiChangeable() {
      return this.guiChangeable;
   }

   public void setGuiChangeable(boolean guiChangeable) {
      this.guiChangeable = guiChangeable;
   }

   public Type getType() {
      return this.type;
   }

   public void setType(Type type) {
      this.type = type;
   }

   public boolean isArray() {
      return this.isArray;
   }

   public void setArray(boolean array) {
      this.isArray = array;
   }
}

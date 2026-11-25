package com.endertech.minecraft.forge.configs;

import com.endertech.minecraft.forge.data.Names;

public interface IHaveConfig {
   static String getClassCategory(Class<?> classType) {
      return classType.getSimpleName();
   }

   static String getClassCategory(Class<?> classType, String subCategory) {
      return Names.dotted().join(getClassCategory(classType), subCategory);
   }

   static String expandClassCategory(Class<?> classType, String headCategory) {
      return Names.dotted().join(headCategory, getClassCategory(classType));
   }

   static String expandClassCategory(Class<?> classType, String headCategory, String subCategory) {
      return Names.dotted().join(expandClassCategory(classType, headCategory), subCategory);
   }

   default String expandClassCategory(String headCategory) {
      return expandClassCategory(this.getClass(), headCategory);
   }

   default String expandClassCategory(String headCategory, String subCategory) {
      return expandClassCategory(this.getClass(), headCategory, subCategory);
   }

   default String getClassCategory() {
      return getClassCategory(this.getClass());
   }

   default String getClassCategory(String subCategory) {
      return getClassCategory(this.getClass(), subCategory);
   }

   ForgeConfig getConfig();

   default void saveConfig() {
      ForgeConfig.save(this.getConfig());
   }
}

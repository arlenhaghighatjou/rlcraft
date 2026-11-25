package com.teamacronymcoders.base.util;

import com.teamacronymcoders.base.Base;

public class LanguageHelper {
   private String modid;

   public LanguageHelper(String modid) {
      this.modid = modid;
   }

   public String none(String message) {
      return Base.proxy.translateMessage("", message);
   }

   public String message(String message) {
      return Base.proxy.translateMessage("message", message);
   }

   public String label(String message) {
      return Base.proxy.translateMessage("label", message);
   }

   public String block(String message) {
      return Base.proxy.translateMessage("block", message);
   }

   public String item(String message) {
      return Base.proxy.translateMessage("item", message);
   }

   public String itemgroup(String message) {
      return Base.proxy.translateMessage("itemGroup", message);
   }

   public String description(String message) {
      return Base.proxy.translateMessage("description", message);
   }

   public String jei(String message) {
      return Base.proxy.translateMessage("jei", message);
   }

   public String material(String message) {
      return Base.proxy.translateMessage("material", message);
   }

   public String error(String message) {
      return Base.proxy.translateMessage("error", message);
   }
}

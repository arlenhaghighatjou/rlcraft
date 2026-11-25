package com.lycanitesmobs.core.info;

import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.JSONLoader;
import com.lycanitesmobs.core.config.ConfigBase;
import java.util.HashMap;
import java.util.Map;

public class ElementManager extends JSONLoader {
   public static ElementManager INSTANCE;
   public Map<String, ElementInfo> elements = new HashMap();

   public ElementManager() {
   }

   public static ElementManager getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new ElementManager();
      }

      return INSTANCE;
   }

   public void loadConfig() {
      ConfigBase config = ConfigBase.getConfig(LycanitesMobs.modInfo, "general");
   }

   public void loadAllFromJSON(ModInfo groupInfo) {
      this.elements.clear();
      this.loadAllJson(groupInfo, "Element", "elements", "name", false);

      for(ElementInfo elementInfo : this.elements.values()) {
         elementInfo.init();
      }

      LycanitesMobs.logDebug("Element", "Complete! " + this.elements.size() + " JSON Elements Loaded In Total.");
   }

   public void parseJson(ModInfo groupInfo, String name, JsonObject json) {
      ElementInfo elementInfo = new ElementInfo();
      elementInfo.loadFromJSON(json);
      if (elementInfo.name == null) {
         LycanitesMobs.logWarning("", "Unable to load " + name + " json due to missing name.");
      } else {
         this.elements.put(elementInfo.name, elementInfo);
      }
   }

   public ElementInfo getElement(String elementName) {
      return !this.elements.containsKey(elementName) ? null : (ElementInfo)this.elements.get(elementName);
   }
}
